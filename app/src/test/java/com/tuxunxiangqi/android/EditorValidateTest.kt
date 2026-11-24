package com.tuxunxiangqi.android

import com.tuxunxiangqi.android.ui.validatePosition
import com.tuxunxiangqi.android.xiangqi.Fen
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** The editor's position gate (1.76.0): piece-count caps join
 *  the older presence/palace/facing checks. The palette places
 *  pieces freely, so this function is the only thing standing
 *  between a hand-built position and the rules/engine layers. */
class EditorValidateTest {

    private fun cellsOf(fen: String) = Fen.fromFen(fen)!!.first

    @Test
    fun standardStartAndComposedPositionsPass() {
        assertNull(
            validatePosition(
                cellsOf("rnbakabnr/9/1c5c1/p1p1p1p1p/9/9/P1P1P1P1P/1C5C1/9/RNBAKABNR w - - 0 1")
            )
        )
        // A built-in puzzle position (full black defence set).
        assertNull(
            validatePosition(
                cellsOf("2bakab2/9/3R1R3/9/4C4/9/9/9/9/4K4 w - - 0 1")
            )
        )
    }

    @Test
    fun twoRedGeneralsAreRejected() {
        val problem = validatePosition(
            cellsOf("4k4/9/9/9/9/9/9/9/9/3KK4 w - - 0 1")
        )
        assertNotNull(problem)
        assertTrue(problem!!.contains("只能放一个帅"))
    }

    @Test
    fun tooManyChariotsAndPawnsAreRejected() {
        val chariots = validatePosition(
            cellsOf("4k4/9/9/9/9/9/9/9/9/R1R1K3R w - - 0 1")
        )
        assertNotNull(chariots)
        assertTrue(chariots!!.contains("车最多放2个"))
        val pawns = validatePosition(
            cellsOf("4k4/9/9/2P1P1P2/9/2P1P4/9/9/9/4K4 w - - 0 1")
        )
        assertEquals(null, pawns) // five pawns: exactly at the cap
        val sixPawns = validatePosition(
            cellsOf("4k4/9/9/2P1P1P2/1P1P5/2P6/9/9/9/4K4 w - - 0 1")
        )
        assertNotNull(sixPawns)
        assertTrue(sixPawns!!.contains("兵/卒最多放5个"))
    }

    @Test
    fun blackOverCapIsRejectedToo() {
        val problem = validatePosition(
            cellsOf("4k4/9/1c5c1/9/9/9/9/9/9/4K4 w - - 0 1")
                .toMutableList()
                .also { cells ->
                    // Add a third black cannon by hand: the FEN
                    // above has two; place one more at (0, 4).
                    val cannon = com.tuxunxiangqi.android.xiangqi.Piece(
                        com.tuxunxiangqi.android.xiangqi.PieceType.CANNON,
                        com.tuxunxiangqi.android.xiangqi.Side.BLACK
                    )
                    cells[4 * 9 + 0] = cannon
                }
        )
        assertNotNull(problem)
        assertTrue(problem!!.contains("黑方"))
        assertTrue(problem.contains("炮最多放2个"))
    }
}
