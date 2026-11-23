package com.tuxunxiangqi.android

import com.tuxunxiangqi.android.xiangqi.GameState
import com.tuxunxiangqi.android.xiangqi.Piece
import com.tuxunxiangqi.android.xiangqi.PieceType
import com.tuxunxiangqi.android.xiangqi.Pos
import com.tuxunxiangqi.android.xiangqi.Side
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** capturedBy / materialScore derive from the starting layout
 *  versus the live board, so they must stay right across
 *  captures, undo, and reduced (handicap / editor) starts. */
class CapturedPiecesTest {

    private fun cellsOf(vararg placed: Triple<Int, Int, Piece>): List<Piece?> {
        val cells = MutableList<Piece?>(90) { null }
        for ((c, r, p) in placed) cells[r * 9 + c] = p
        return cells
    }

    /** Red: chariot (600). Black: soldier (60) + horse (270) = 330.
     *  Initial material difference is therefore 270. */
    private fun duelPosition(): List<Piece?> = cellsOf(
        Triple(4, 0, Piece(PieceType.GENERAL, Side.BLACK)),
        Triple(5, 9, Piece(PieceType.GENERAL, Side.RED)),
        Triple(0, 6, Piece(PieceType.CHARIOT, Side.RED)),
        Triple(0, 3, Piece(PieceType.SOLDIER, Side.BLACK)),
        Triple(8, 4, Piece(PieceType.HORSE, Side.BLACK))
    )

    @Test
    fun freshStartHasNoCaptures() {
        val s = GameState()
        assertTrue(s.capturedBy(Side.RED).isEmpty())
        assertTrue(s.capturedBy(Side.BLACK).isEmpty())
        assertEquals(s.materialScore(Side.RED), s.materialScore(Side.BLACK))
    }

    @Test
    fun captureShowsUpForTheCapturer() {
        val s = GameState()
        s.loadPosition(duelPosition(), Side.RED)
        assertEquals(270, s.materialScore(Side.RED) - s.materialScore(Side.BLACK))
        assertTrue(s.tryMove(Pos(0, 6), Pos(0, 3))) // chariot takes soldier
        assertEquals(
            listOf(Piece(PieceType.SOLDIER, Side.BLACK)),
            s.capturedBy(Side.RED)
        )
        assertTrue(s.capturedBy(Side.BLACK).isEmpty())
        // The soldier (60) is off the board: difference grows to 330.
        assertEquals(330, s.materialScore(Side.RED) - s.materialScore(Side.BLACK))
    }

    @Test
    fun undoRestoresTheCapturedPiece() {
        val s = GameState()
        s.loadPosition(duelPosition(), Side.RED)
        assertTrue(s.tryMove(Pos(0, 6), Pos(0, 3)))
        assertTrue(s.undo())
        assertTrue(s.capturedBy(Side.RED).isEmpty())
        assertEquals(270, s.materialScore(Side.RED) - s.materialScore(Side.BLACK))
    }

    @Test
    fun reducedStartDoesNotCountMissingPiecesAsCaptured() {
        // Handicap-style start: Black simply has fewer pieces in
        // this layout — that must not read as "Red captured them".
        val s = GameState()
        s.loadPosition(duelPosition(), Side.RED)
        assertTrue(s.capturedBy(Side.RED).isEmpty())
        assertTrue(s.capturedBy(Side.BLACK).isEmpty())
    }

    @Test
    fun capturesAreSortedStrongestFirst() {
        val t = GameState()
        t.loadPosition(
            cellsOf(
                Triple(4, 0, Piece(PieceType.GENERAL, Side.BLACK)),
                Triple(5, 9, Piece(PieceType.GENERAL, Side.RED)),
                Triple(0, 6, Piece(PieceType.CHARIOT, Side.RED)),
                Triple(0, 3, Piece(PieceType.SOLDIER, Side.BLACK)),
                Triple(0, 0, Piece(PieceType.CHARIOT, Side.BLACK))
            ),
            Side.RED
        )
        assertTrue(t.tryMove(Pos(0, 6), Pos(0, 3))) // red takes soldier
        assertTrue(t.tryMove(Pos(0, 0), Pos(0, 1))) // black chariot steps down
        assertTrue(t.tryMove(Pos(0, 3), Pos(0, 1))) // red takes chariot
        assertEquals(
            listOf(
                Piece(PieceType.CHARIOT, Side.BLACK),
                Piece(PieceType.SOLDIER, Side.BLACK)
            ),
            t.capturedBy(Side.RED)
        )
    }
}
