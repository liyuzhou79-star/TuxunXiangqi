package com.tuxunxiangqi.android

import com.tuxunxiangqi.android.data.GuessPositions
import com.tuxunxiangqi.android.data.Puzzles
import com.tuxunxiangqi.android.xiangqi.Fen
import com.tuxunxiangqi.android.xiangqi.GameState
import com.tuxunxiangqi.android.xiangqi.Pos
import com.tuxunxiangqi.android.xiangqi.Rules
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** The tap-to-move chain the puzzle and guess screens run
 *  (1.24.0 regression): load the position, pick a piece of the
 *  side to move, judge the tap on a target with the SAME live
 *  calls the screens' onTap makes (Rules.isLegal + tryMove),
 *  and require the move to land. The original bug was in the
 *  Compose layer — the board's gesture handler held the first
 *  composition's onTap, whose precomputed target list was
 *  empty forever — so these tests pin the logic half of the
 *  chain for every built-in position, and the screens no
 *  longer gate moves on any precomputed list. */
class TapChainTest {

    /** Plays the first available legal move of the side to
     *  move, exactly as a select-then-tap sequence would, and
     *  returns the move count afterwards. */
    private fun tapFirstMove(fen: String): Int {
        val parsed = Fen.fromFen(fen)
        assertNotNull("unparseable: $fen", parsed)
        val gs = GameState()
        gs.newGame(parsed!!.first, parsed.second)
        assertNull("position starts finished: $fen", gs.result)
        var moved = false
        outer@ for (r in 0..9) {
            for (c in 0..8) {
                val from = Pos(c, r)
                val piece = gs.pieceAt(from) ?: continue
                if (piece.side != gs.turn) continue
                val targets = Rules.legalTargets(gs.cells, from)
                if (targets.isEmpty()) continue
                val to = targets.first()
                assertTrue(
                    "isLegal disagrees with legalTargets: $fen $from->$to",
                    Rules.isLegal(gs.cells, from, to)
                )
                assertTrue("tryMove refused: $fen $from->$to", gs.tryMove(from, to))
                moved = true
                break@outer
            }
        }
        assertTrue("side to move has no legal move at all: $fen", moved)
        return gs.moveCount
    }

    @Test
    fun everyPuzzleAcceptsATapMove() {
        for (p in Puzzles.all) {
            assertEquals("puzzle ${p.id}", 1, tapFirstMove(p.fen))
        }
    }

    @Test
    fun everyGuessPositionAcceptsATapMove() {
        for ((_, fen) in GuessPositions.easy + GuessPositions.normal +
            GuessPositions.hard) {
            assertEquals(1, tapFirstMove(fen))
        }
    }

    @Test
    fun standardStartAcceptsATapMove() {
        assertEquals(1, tapFirstMove(Fen.toFen(GameState())))
    }
}
