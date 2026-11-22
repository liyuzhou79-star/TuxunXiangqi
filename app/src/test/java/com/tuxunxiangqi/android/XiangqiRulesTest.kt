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
    fun check_chariotOnOpenFileAndFlyingGenerals() {
        val cells = emptyBoard()
        cells.put(4, 0, PieceType.GENERAL, Side.BLACK)
        cells.put(4, 9, PieceType.GENERAL, Side.RED)
        cells.put(4, 5, PieceType.CHARIOT, Side.RED)
        assertTrue(Rules.isInCheck(cells, Side.BLACK))
        assertFalse(Rules.isInCheck(cells, Side.RED))
        // Remove the chariot: the generals now face each other, which
        // counts as check for both sides.
        val cells2 = emptyBoard()
        cells2.put(4, 0, PieceType.GENERAL, Side.BLACK)
        cells2.put(4, 9, PieceType.GENERAL, Side.RED)
        assertTrue(Rules.isInCheck(cells2, Side.BLACK))
        assertTrue(Rules.isInCheck(cells2, Side.RED))
    }

    @Test
    fun legalTargets_excludesMoveExposingGeneral() {
        // Red chariot shields the red general from the black chariot
        // on file 4: sliding sideways would be 送将 and is excluded,
        // sliding along the file stays legal.
        val cells = emptyBoard()
        cells.put(4, 9, PieceType.GENERAL, Side.RED)
        cells.put(3, 0, PieceType.GENERAL, Side.BLACK)
        cells.put(4, 0, PieceType.CHARIOT, Side.BLACK)
        cells.put(4, 5, PieceType.CHARIOT, Side.RED)
        val targets = Rules.legalTargets(cells, Pos(4, 5))
        assertFalse(targets.contains(Pos(5, 5)))
        assertFalse(targets.contains(Pos(3, 5)))
        assertTrue(targets.contains(Pos(4, 4)))
        assertTrue(targets.contains(Pos(4, 6)))
        assertFalse(Rules.isLegal(cells, Pos(4, 5), Pos(5, 5)))
    }

    @Test
    fun checkmate_detectedAsGameOver() {
        // Black general is checked by the chariot on (4,1); capturing
        // it is illegal (defended along the rank), and both flight
        // squares are covered by the two horses.
        val cells = emptyBoard()
        cells.put(4, 0, PieceType.GENERAL, Side.BLACK)
        cells.put(4, 9, PieceType.GENERAL, Side.RED)
        cells.put(4, 1, PieceType.CHARIOT, Side.RED)
        cells.put(0, 1, PieceType.CHARIOT, Side.RED)
        cells.put(2, 2, PieceType.HORSE, Side.RED)
        cells.put(6, 2, PieceType.HORSE, Side.RED)
        assertTrue(Rules.isInCheck(cells, Side.BLACK))
        assertTrue(Rules.allLegalMoves(cells, Side.BLACK).isEmpty())
        val s = GameState()
        s.loadPosition(cells, Side.BLACK)
        assertTrue(s.inCheck)
        assertEquals(Side.RED, s.result?.winner)
        assertEquals("将死", s.result?.reason)
    }

    @Test
    fun stalemate_sideToMoveLoses() {
        // Black general is NOT in check, but (3,0), (5,0) and (4,1)
        // are all covered: 困毙, black loses.
        val cells = emptyBoard()
        cells.put(4, 0, PieceType.GENERAL, Side.BLACK)
        cells.put(3, 9, PieceType.GENERAL, Side.RED)
        cells.put(3, 1, PieceType.CHARIOT, Side.RED)
        cells.put(5, 2, PieceType.CHARIOT, Side.RED)
        assertFalse(Rules.isInCheck(cells, Side.BLACK))
        assertTrue(Rules.allLegalMoves(cells, Side.BLACK).isEmpty())
        val s = GameState()
        s.loadPosition(cells, Side.BLACK)
        assertFalse(s.inCheck)
        assertEquals(Side.RED, s.result?.winner)
        assertEquals("困毙", s.result?.reason)
    }

    @Test
    fun gameState_checkFlagAfterMoveAndUndoClears() {
        // Red chariot drops to the general's file: check. Undo clears.
        val cells = emptyBoard()
        cells.put(4, 0, PieceType.GENERAL, Side.BLACK)
        cells.put(3, 9, PieceType.GENERAL, Side.RED)
        cells.put(0, 5, PieceType.CHARIOT, Side.RED)
        val s = GameState()
        s.loadPosition(cells, Side.RED)
        assertFalse(s.inCheck)
        assertTrue(s.tryMove(Pos(0, 5), Pos(4, 5)))
        assertTrue(s.inCheck)
        assertEquals(null, s.result)
        assertTrue(s.undo())
        assertFalse(s.inCheck)
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
