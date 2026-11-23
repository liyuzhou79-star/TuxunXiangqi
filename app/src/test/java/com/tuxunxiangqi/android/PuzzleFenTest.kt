package com.tuxunxiangqi.android

import com.tuxunxiangqi.android.data.GuessPositions
import com.tuxunxiangqi.android.data.Puzzles
import com.tuxunxiangqi.android.xiangqi.Fen
import com.tuxunxiangqi.android.xiangqi.PieceType
import com.tuxunxiangqi.android.xiangqi.Side
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** Every built-in training position must parse, contain both
 *  generals, and not start with the generals facing each other. */
class PuzzleFenTest {

    private fun checkFen(fen: String) {
        val parsed = Fen.fromFen(fen)
        assertNotNull("unparseable: $fen", parsed)
        val cells = parsed!!.first
        var redGeneral = -1
        var blackGeneral = -1
        for (i in cells.indices) {
            val p = cells[i] ?: continue
            if (p.type == PieceType.GENERAL) {
                if (p.side == Side.RED) redGeneral = i else blackGeneral = i
            }
        }
        assertTrue("missing red general: $fen", redGeneral >= 0)
        assertTrue("missing black general: $fen", blackGeneral >= 0)
        val rc = redGeneral % 9
        val bc = blackGeneral % 9
        if (rc == bc) {
            val lo = minOf(redGeneral, blackGeneral)
            val hi = maxOf(redGeneral, blackGeneral)
            var blocked = false
            var i = lo + 9
            while (i < hi) {
                if (cells[i] != null) blocked = true
                i += 9
            }
            assertTrue("generals facing: $fen", blocked)
        }
    }

    @Test
    fun allPuzzleFensAreSane() {
        for (p in Puzzles.all) checkFen(p.fen)
        assertEquals(32, Puzzles.all.size)
    }

    @Test
    fun allGuessFensAreSane() {
        for ((_, fen) in GuessPositions.easy + GuessPositions.normal + GuessPositions.hard) {
            checkFen(fen)
        }
        assertEquals(8, GuessPositions.easy.size)
        assertEquals(8, GuessPositions.normal.size)
        assertEquals(8, GuessPositions.hard.size)
    }
}
