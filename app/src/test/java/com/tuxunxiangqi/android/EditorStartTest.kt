package com.tuxunxiangqi.android

import com.tuxunxiangqi.android.xiangqi.Fen
import com.tuxunxiangqi.android.xiangqi.GameState
import com.tuxunxiangqi.android.xiangqi.Piece
import com.tuxunxiangqi.android.xiangqi.Rules
import com.tuxunxiangqi.android.xiangqi.Side
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** The editor is a free sandbox (1.85.0): the position gate is
 *  gone, so any layout — doubled generals, six soldiers, a
 *  missing general, facing generals — may start a game. The
 *  only remaining bottom line is that nothing crashes and the
 *  rules layer degrades gracefully: a side that can move plays
 *  on, a side that genuinely cannot move loses by stalemate,
 *  exactly as the normal end-of-game rule says. (The records
 *  import FEN gate is a different door and is NOT opened by
 *  this change.) */
class EditorStartTest {

    private fun stateOf(fen: String): GameState {
        val (cells, turn) = Fen.fromFen(fen)!!
        val gs = GameState()
        gs.newGame(cells, turn)
        return gs
    }

    @Test
    fun twoRedGeneralsCanStartAndPlay() {
        val gs = stateOf("2r1k4/9/9/9/9/9/9/9/9/3KK4 w - - 0 1")
        assertNull(gs.result)
        // A few plies of local-rules moves for both sides: no
        // crash, every offered move lands.
        repeat(4) {
            if (gs.result != null) return@repeat
            val mv = Rules.fallbackMove(gs.cells, gs.turn)
            assertNotNull(mv)
            assertTrue(gs.tryMove(mv!!.from, mv.to))
        }
    }

    @Test
    fun sixSoldiersCanStart() {
        val gs = stateOf("4k4/9/9/2P1P1P2/1P1P5/2P6/9/9/9/4K4 w - - 0 1")
        assertNull(gs.result)
        assertTrue(Rules.allLegalMoves(gs.cells, Side.RED).isNotEmpty())
    }

    @Test
    fun missingGeneralSideStillMoves() {
        // Black has no general at all, just a chariot. It is
        // never "in check" (there is no general to attack), its
        // pieces move normally, and the game goes on.
        val gs = stateOf("3r5/9/9/9/9/9/9/9/9/4K4 w - - 0 1")
        assertNull(gs.result)
        val redMove = Rules.fallbackMove(gs.cells, Side.RED)
        assertNotNull(redMove)
        assertTrue(gs.tryMove(redMove!!.from, redMove.to))
        val blackMove = Rules.fallbackMove(gs.cells, Side.BLACK)
        assertNotNull(blackMove)
        assertTrue(gs.tryMove(blackMove!!.from, blackMove.to))
        assertNull(gs.result)
    }

    @Test
    fun facingGeneralsCanStart() {
        // Generals staring at each other: Red to move is in
        // check by the flying-general rule and must step aside
        // (or block); those moves exist and play normally.
        val gs = stateOf("4k4/9/9/9/9/9/9/9/9/4K4 w - - 0 1")
        assertNull(gs.result)
        assertTrue(gs.inCheck)
        val moves = Rules.allLegalMoves(gs.cells, Side.RED)
        assertEquals(2, moves.size)
        assertTrue(gs.tryMove(moves[0].from, moves[0].to))
        assertNull(gs.result)
    }

    @Test
    fun generalStrandedOutsidePalaceIsStalemateNotCrash() {
        // A red general stranded in the corner can never move
        // (generals may only step inside the palace), so the
        // game is simply over by stalemate at the start.
        val gs = stateOf("4k4/9/9/9/9/9/9/9/9/K8 w - - 0 1")
        assertNotNull(gs.result)
        assertEquals("困毙", gs.result!!.reason)
        assertEquals(Side.BLACK, gs.result!!.winner)
    }

    @Test
    fun emptyBoardIsStalemateNotCrash() {
        val gs = GameState()
        gs.loadPosition(MutableList<Piece?>(90) { null }, Side.RED)
        assertNotNull(gs.result)
        assertEquals("困毙", gs.result!!.reason)
    }

    @Test
    fun advisorOutsidePalaceAndElephantAcrossRiverCanStart() {
        // Both pieces are immobile where they stand (their move
        // rules only allow their home territory), but the rest
        // of the position plays normally.
        val gs = stateOf("4k4/9/B8/9/9/A8/9/9/9/3K4R w - - 0 1")
        assertNull(gs.result)
        assertTrue(
            Rules.legalTargets(gs.cells, com.tuxunxiangqi.android.xiangqi.Pos(0, 5)).isEmpty()
        )
        assertTrue(
            Rules.legalTargets(gs.cells, com.tuxunxiangqi.android.xiangqi.Pos(0, 2)).isEmpty()
        )
        val rookMove = Rules.allLegalMoves(gs.cells, Side.RED)
            .firstOrNull { it.from == com.tuxunxiangqi.android.xiangqi.Pos(8, 9) }
        assertNotNull(rookMove)
        assertTrue(gs.tryMove(rookMove!!.from, rookMove.to))
    }
}
