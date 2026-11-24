package com.tuxunxiangqi.android

import com.tuxunxiangqi.android.data.GameRecord
import com.tuxunxiangqi.android.data.parsePortableRecord
import com.tuxunxiangqi.android.data.parsePortableRecords
import com.tuxunxiangqi.android.data.toPortableText
import com.tuxunxiangqi.android.data.toPortableTextBundle
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** The portable text format is the app's import/export contract:
 *  what export writes, import must read back unchanged. */
class PortableRecordTest {

    @Test
    fun roundTripPreservesEverything() {
        val rec = GameRecord(
            id = 42L,
            title = "测试局",
            dateMs = 123456L,
            modeLabel = "人机对战",
            resultText = "红胜",
            initialFen = "rnbakabnr/9/1c5c1/p1p1p1p1p/9/9/P1P1P1P1P/1C5C1/9/RNBAKABNR w - - 0 1",
            movesUci = listOf("h2e2", "h9g7", "h0g2"),
            evals = mapOf(0 to 15, 2 to -30),
            marks = mapOf(1 to "?", 3 to "??")
        )
        val back = parsePortableRecord(rec.toPortableText(), 99L)
        assertNotNull(back)
        assertEquals("测试局", back!!.title)
        assertEquals("人机对战", back.modeLabel)
        assertEquals("红胜", back.resultText)
        assertEquals(rec.initialFen, back.initialFen)
        assertEquals(rec.movesUci, back.movesUci)
        assertEquals(rec.evals, back.evals)
        assertEquals(rec.marks, back.marks)
        assertEquals(99L, back.id)
    }

    @Test
    fun garbageIsRejected() {
        assertNull(parsePortableRecord("随便一段文字，没有棋谱头", 1L))
        assertNull(parsePortableRecord("", 1L))
    }

    @Test
    fun minimalRecordParses() {
        val back = parsePortableRecord(
            "[FEN] rnbakabnr/9/1c5c1/p1p1p1p1p/9/9/P1P1P1P1P/1C5C1/9/RNBAKABNR w - - 0 1\n[着法UCI] h2e2\n",
            7L
        )
        assertNotNull(back)
        assertEquals(listOf("h2e2"), back!!.movesUci)
        assertEquals(7L, back.id)
    }

    /** The 1.80.0 import gate: a block whose FEN does not parse
     *  or lacks a general is refused outright (before 1.80.0 any
     *  non-blank string entered the library and the review page
     *  silently fell back to the standard start position). */
    @Test
    fun unusableFenIsRejected() {
        assertNull(parsePortableRecord("[FEN] abc w - - 0 1\n[着法UCI] h2e2\n", 7L))
        assertNull(
            parsePortableRecord(
                "[FEN] 9/9/9/9/9/9/9/9/9/4K4 w - - 0 1\n[着法UCI] h2e2\n", 7L
            )
        )
    }

    /** Only the replayable prefix of the move list is kept, so
     *  the stored record is exactly what the review page can
     *  replay — a corrupt token or an illegal move ends the
     *  list there instead of leaving phantom steps behind. */
    @Test
    fun movesTruncateAtFirstUnplayable() {
        val fen = "[FEN] rnbakabnr/9/1c5c1/p1p1p1p1p/9/9/P1P1P1P1P/1C5C1/9/RNBAKABNR w - - 0 1\n"
        val corrupt = parsePortableRecord(fen + "[着法UCI] h2e2 zzzz h0g2\n", 7L)
        assertEquals(listOf("h2e2"), corrupt!!.movesUci)
        val illegal = parsePortableRecord(fen + "[着法UCI] h2e2 h9g7 a0a9\n", 7L)
        assertEquals(listOf("h2e2", "h9g7"), illegal!!.movesUci)
    }

    private fun sample(id: Long, title: String, moves: List<String>) = GameRecord(
        id = id,
        title = title,
        dateMs = 1000L + id,
        modeLabel = "人机对战",
        resultText = "红胜",
        initialFen = "rnbakabnr/9/1c5c1/p1p1p1p1p/9/9/P1P1P1P1P/1C5C1/9/RNBAKABNR w - - 0 1",
        movesUci = moves,
        evals = mapOf(0 to 10),
        marks = emptyMap()
    )

    /** The 1.19.0 multi-select export contract: a bundle of N
     *  records imports back as exactly N records, each intact,
     *  with distinct ids derived from the fallback. */
    @Test
    fun bundleRoundTripPreservesCountAndContent() {
        val recs = listOf(
            sample(1L, "第一局", listOf("h2e2", "h9g7")),
            sample(2L, "第二局", listOf("b2e2")),
            sample(3L, "第三局", listOf("h0g2", "b9c7", "g2e1"))
        )
        val back = parsePortableRecords(recs.toPortableTextBundle(), 5000L)
        assertEquals(3, back.size)
        assertEquals(listOf("第一局", "第二局", "第三局"), back.map { it.title })
        assertEquals(recs.map { it.movesUci }, back.map { it.movesUci })
        assertEquals(recs.map { it.initialFen }, back.map { it.initialFen })
        assertEquals(listOf(5000L, 5001L, 5002L), back.map { it.id })
        assertEquals(back.map { it.id }.distinct().size, 3)
    }

    @Test
    fun bundleGarbageYieldsEmptyAndSingleStillWorks() {
        assertTrue(parsePortableRecords("随便一段文字", 1L).isEmpty())
        assertTrue(parsePortableRecords("", 1L).isEmpty())
        val one = sample(9L, "单局", listOf("h2e2"))
        val back = parsePortableRecords(one.toPortableText(), 77L)
        assertEquals(1, back.size)
        assertEquals(77L, back[0].id)
        // The single-record reader keeps returning the first
        // record of a bundle, as before.
        val first = parsePortableRecord(
            listOf(one, sample(10L, "另一局", listOf("b2e2"))).toPortableTextBundle(), 5L
        )
        assertEquals("单局", first!!.title)
    }
}
