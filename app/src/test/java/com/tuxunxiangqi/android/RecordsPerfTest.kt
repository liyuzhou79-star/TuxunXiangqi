package com.tuxunxiangqi.android

import com.tuxunxiangqi.android.data.GameRecord
import com.tuxunxiangqi.android.data.filterRecords
import com.tuxunxiangqi.android.data.parsePortableRecord
import com.tuxunxiangqi.android.data.toPortableText
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** Library scale check (1.13.0): a heavy user's library holds
 *  hundreds of records. Building 200 full-length records,
 *  round-tripping every one through the portable text format
 *  and running the library filter/search over the whole set
 *  must stay comfortably fast — the bounds here are generous
 *  (shared CI runners), they exist to catch pathological
 *  regressions, not to micro-benchmark. */
class RecordsPerfTest {

    private fun makeRecords(n: Int): List<GameRecord> {
        val modes = listOf("人机对弈 · 普通", "人人对弈", "AI 表演赛")
        val moves = (1..120).map { "h2e2" }
        return (1..n).map { i ->
            GameRecord(
                id = i.toLong(),
                title = "对局 $i" + (if (i % 10 == 0) " 经典" else ""),
                dateMs = 1_700_000_000_000L + i * 60_000L,
                modeLabel = modes[i % modes.size],
                resultText = if (i % 3 == 0) "红方胜 · 将死" else "黑方胜 · 困毙",
                initialFen = "rnbakabnr/9/1c5c1/p1p1p1p1p/9/9/P1P1P1P1P/1C5C1/9/RNBAKABNR w - - 0 1",
                movesUci = moves,
                evals = mapOf(0 to 15, 60 to -120, 120 to 300),
                marks = mapOf(30 to "?", 90 to "??")
            )
        }
    }

    @Test
    fun twoHundredRecordsParseAndFilterFast() {
        val records = makeRecords(200)
        assertEquals(200, records.size)

        // Serialise + parse the whole library (the import path
        // and the shape of loadAll's per-record work).
        val t0 = System.nanoTime()
        var parsed = 0
        for (r in records) {
            val back = parsePortableRecord(r.toPortableText(), r.id)
            assertNotNull(back)
            assertEquals(120, back!!.movesUci.size)
            parsed++
        }
        val parseMs = (System.nanoTime() - t0) / 1_000_000
        assertEquals(200, parsed)
        assertTrue("200-record parse too slow: $parseMs ms", parseMs < 10_000)

        // Filter + search over the whole library, repeatedly
        // (typing in the search box re-runs this per keystroke).
        val t1 = System.nanoTime()
        repeat(50) {
            assertEquals(200, filterRecords(records, "全部", "").size)
            assertEquals(20, filterRecords(records, "全部", "经典").size)
            assertEquals(66, filterRecords(records, "人机对弈", "").size)
            assertEquals(66, filterRecords(records, "全部", "将死").size)
        }
        val filterMs = (System.nanoTime() - t1) / 1_000_000
        assertTrue("200-record filter too slow: $filterMs ms", filterMs < 5_000)
    }
}
