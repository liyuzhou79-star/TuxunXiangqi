package com.tuxunxiangqi.android

import com.tuxunxiangqi.android.xiangqi.GameState
import com.tuxunxiangqi.android.xiangqi.Piece
import com.tuxunxiangqi.android.xiangqi.PieceType
import com.tuxunxiangqi.android.xiangqi.Pos
import com.tuxunxiangqi.android.xiangqi.Rules
import com.tuxunxiangqi.android.xiangqi.Side
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class XiangqiRulesTest {

    private fun emptyBoard(): MutableList<Piece?> = MutableList(90) { null }

    private fun MutableList<Piece?>.put(col: Int, row: Int, type: PieceType, side: Side) {
        this[row * 9 + col] = Piece(type, side)
    }

    @Test
    fun initialSetup_has32PiecesAndGeneralsFacing() {
        val s = GameState()
        assertEquals(32, s.cells.count { it != null })
        assertEquals(
            Piece(PieceType.GENERAL, Side.RED), s.pieceAt(Pos(4, 9))
        )
        assertEquals(
            Piece(PieceType.GENERAL, Side.BLACK), s.pieceAt(Pos(4, 0))
        )
        assertEquals(Side.RED, s.turn)
    }

    @Test
    fun chariot_blockedByOwnSoldier() {
        val s = GameState()
        val cells = s.cells
        assertTrue(Rules.isPseudoLegal(cells, Pos(0, 9), Pos(0, 7)))
        // Own soldier stands on (0,6): cannot land on it, cannot jump it.
        assertFalse(Rules.isPseudoLegal(cells, Pos(0, 9), Pos(0, 6)))
        assertFalse(Rules.isPseudoLegal(cells, Pos(0, 9), Pos(0, 5)))
    }

    @Test
    fun horse_hobbledLeg() {
        val s = GameState()
        val cells = s.cells
        // Leg square (1,8) is empty: both forward moves are fine.
        assertTrue(Rules.isPseudoLegal(cells, Pos(1, 9), Pos(0, 7)))
        assertTrue(Rules.isPseudoLegal(cells, Pos(1, 9), Pos(2, 7)))
        // Leg square (2,9) holds the elephant: the sideways move is hobbled.
        assertFalse(Rules.isPseudoLegal(cells, Pos(1, 9), Pos(3, 8)))
    }

    @Test
    fun elephant_eyeBlockedAndRiverLimit() {
        val cells = emptyBoard()
        cells.put(2, 9, PieceType.ELEPHANT, Side.RED)
        assertTrue(Rules.isPseudoLegal(cells, Pos(2, 9), Pos(0, 7)))
        // Block the eye at (1,8).
        cells.put(1, 8, PieceType.SOLDIER, Side.BLACK)
        assertFalse(Rules.isPseudoLegal(cells, Pos(2, 9), Pos(0, 7)))
        // Elephants may not cross the river.
        val cells2 = emptyBoard()
        cells2.put(2, 5, PieceType.ELEPHANT, Side.RED)
        assertFalse(Rules.isPseudoLegal(cells2, Pos(2, 5), Pos(0, 3)))
        assertTrue(Rules.isPseudoLegal(cells2, Pos(2, 5), Pos(4, 7)))
    }

    @Test
    fun cannon_needsExactlyOneScreenToCapture() {
        val cells = emptyBoard()
        cells.put(1, 7, PieceType.CANNON, Side.RED)
        cells.put(1, 2, PieceType.CANNON, Side.BLACK)
        // No screen between: cannot capture, cannot jump.
        assertFalse(Rules.isPseudoLegal(cells, Pos(1, 7), Pos(1, 2)))
        // Add one screen: capture becomes legal.
        cells.put(1, 4, PieceType.SOLDIER, Side.BLACK)
        assertTrue(Rules.isPseudoLegal(cells, Pos(1, 7), Pos(1, 2)))
        // Quiet move still needs a clear path.
        assertFalse(Rules.isPseudoLegal(cells, Pos(1, 7), Pos(1, 3)))
        assertTrue(Rules.isPseudoLegal(cells, Pos(1, 7), Pos(1, 5)))
    }

    @Test
    fun soldier_forwardOnlyUntilRiverCrossed() {
        val cells = emptyBoard()
        cells.put(0, 6, PieceType.SOLDIER, Side.RED)
        assertTrue(Rules.isPseudoLegal(cells, Pos(0, 6), Pos(0, 5)))
        assertFalse(Rules.isPseudoLegal(cells, Pos(0, 6), Pos(1, 6)))
        assertFalse(Rules.isPseudoLegal(cells, Pos(0, 6), Pos(0, 7)))
        val cells2 = emptyBoard()
        cells2.put(0, 4, PieceType.SOLDIER, Side.RED)
        assertTrue(Rules.isPseudoLegal(cells2, Pos(0, 4), Pos(1, 4)))
        assertTrue(Rules.isPseudoLegal(cells2, Pos(0, 4), Pos(0, 3)))
    }

    @Test
    fun advisorAndGeneral_stayInPalace() {
        val cells = emptyBoard()
        cells.put(3, 9, PieceType.ADVISOR, Side.RED)
        assertTrue(Rules.isPseudoLegal(cells, Pos(3, 9), Pos(4, 8)))
        cells.put(4, 9, PieceType.GENERAL, Side.RED)
        assertTrue(Rules.isPseudoLegal(cells, Pos(4, 9), Pos(4, 8)))
        assertFalse(Rules.isPseudoLegal(cells, Pos(4, 9), Pos(4, 7)))
        val cells2 = emptyBoard()
        cells2.put(3, 9, PieceType.GENERAL, Side.RED)
        assertFalse(Rules.isPseudoLegal(cells2, Pos(3, 9), Pos(2, 9)))
    }

    @Test
    fun generals_flyingCaptureAlongClearFile() {
        val cells = emptyBoard()
        cells.put(4, 9, PieceType.GENERAL, Side.RED)
        cells.put(4, 0, PieceType.GENERAL, Side.BLACK)
        assertTrue(Rules.isPseudoLegal(cells, Pos(4, 9), Pos(4, 0)))
        cells.put(4, 5, PieceType.SOLDIER, Side.RED)
        assertFalse(Rules.isPseudoLegal(cells, Pos(4, 9), Pos(4, 0)))
    }

    @Test
    fun gameState_turnAlternatesAndUndoRestores() {
        val s = GameState()
        assertTrue(s.tryMove(Pos(0, 6), Pos(0, 5)))
        assertEquals(Side.BLACK, s.turn)
        assertEquals(1, s.moveCount)
        // Black cannot move a red piece.
        assertFalse(s.tryMove(Pos(2, 6), Pos(2, 5)))
        assertTrue(s.undo())
        assertEquals(Side.RED, s.turn)
        assertEquals(0, s.moveCount)
        assertEquals(
            Piece(PieceType.SOLDIER, Side.RED), s.pieceAt(Pos(0, 6))
        )
    }
}
