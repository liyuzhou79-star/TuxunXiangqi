package com.tuxunxiangqi.android

import com.tuxunxiangqi.android.xiangqi.Fen
import com.tuxunxiangqi.android.xiangqi.GameState
import com.tuxunxiangqi.android.xiangqi.Pos
import com.tuxunxiangqi.android.xiangqi.Rules
import com.tuxunxiangqi.android.xiangqi.Side
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** The engine side never quits mid-game (1.85.0): when the
 *  engine cannot move — error, "(none)", or a move our rules
 *  reject — the UI falls back to [Rules.fallbackMove]. These
 *  tests pin the contract at the rules level: a hopeless side
 *  still has to answer, and the game ends only at a real
 *  checkmate or stalemate. */
class FallbackMoveTest {

    private fun stateOf(fen: String): GameState {
        val (cells, turn) = Fen.fromFen(fen)!!
        val gs = GameState()
        gs.loadPosition(cells, turn)
        return gs
    }

    @Test
    fun hopelessSideMustStillReply() {
        // Black: bare king + one advisor against king + two
        // chariots — down over a thousand points of material, a
        // position any score-based resignation would abandon.
        // Black to move, not in check, with legal moves: the
        // fallback must produce one and the game must go on.
        val gs = stateOf("4k4/4a4/9/9/9/R8/9/9/8R/4K4 b - - 0 1")
        assertTrue(
            gs.materialScore(Side.RED) - gs.materialScore(Side.BLACK) >= 1000
        )
        assertNull(gs.result)
        val fb = Rules.fallbackMove(gs.cells, Side.BLACK)
        assertNotNull("a lost side with legal moves must still move", fb)
        assertTrue(gs.tryMove(fb!!.from, fb.to))
        assertNull("the reply must not end the game", gs.result)
        assertEquals(Side.RED, gs.turn)
    }

    @Test
    fun fallbackPrefersTheGreedyCapture() {
        // Black chariot can take a horse (270) down the file or
        // a soldier (60) along the rank; the stand-in move takes
        // the horse.
        val gs = stateOf("4k4/9/9/9/4r1P2/9/9/4N4/9/4K4 b - - 0 1")
        val fb = Rules.fallbackMove(gs.cells, Side.BLACK)
        assertNotNull(fb)
        assertEquals(Pos(4, 7), fb!!.to)
    }

    @Test
    fun gameEndsOnlyAtRealCheckmate() {
        // Red to move mates in one: the chariot steps onto the
        // king's file point-blank, covered by both horses; the
        // bare black king has no flight square left.
        val gs = stateOf("4k4/R8/2N3N2/9/9/9/9/9/9/3K5 w - - 0 1")
        assertNull(gs.result)
        assertTrue(gs.tryMove(Pos(0, 1), Pos(4, 1)))
        assertNotNull(gs.result)
        assertEquals(Side.RED, gs.result!!.winner)
        assertEquals("将死", gs.result!!.reason)
    }

    @Test
    fun matedPositionIsTerminalForTheSideToMove() {
        // The position right after that mate, Black to move:
        // already over before anyone "replies" — the fallback
        // has nothing to offer, which is the only acceptable
        // reason for the engine side not to move.
        val gs = stateOf("4k4/4R4/2N3N2/9/9/9/9/9/9/3K5 b - - 0 1")
        assertNotNull(gs.result)
        assertEquals("将死", gs.result!!.reason)
        assertNull(Rules.fallbackMove(gs.cells, Side.BLACK))
    }
}
