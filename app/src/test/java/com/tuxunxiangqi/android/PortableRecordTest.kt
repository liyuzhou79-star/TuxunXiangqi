package com.tuxunxiangqi.android

import com.tuxunxiangqi.android.data.GameRecord
import com.tuxunxiangqi.android.data.parsePortableRecord
import com.tuxunxiangqi.android.data.toPortableText
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
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
            evals = mapOf(0 to 15, 2 to -30)
        )
        val back = parsePortableRecord(rec.toPortableText(), 99L)
        assertNotNull(back)
        assertEquals("测试局", back!!.title)
        assertEquals("人机对战", back.modeLabel)
        assertEquals("红胜", back.resultText)
        assertEquals(rec.initialFen, back.initialFen)
        assertEquals(rec.movesUci, back.movesUci)
        assertEquals(rec.evals, back.evals)
        assertEquals(99L, back.id)
    }

    @Test
    fun garbageIsRejected() {
        assertNull(parsePortableRecord("随便一段文字，没有棋谱头", 1L))
        assertNull(parsePortableRecord("", 1L))
    }

    @Test
    fun minimalRecordParses() {
        val back = parsePortableRecord("[FEN] abc w - - 0 1\n[着法UCI] h2e2\n", 7L)
        assertNotNull(back)
        assertEquals(listOf("h2e2"), back!!.movesUci)
        assertEquals(7L, back.id)
    }
}
