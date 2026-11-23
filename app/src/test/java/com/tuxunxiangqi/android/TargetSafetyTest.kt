package com.tuxunxiangqi.android

import com.tuxunxiangqi.android.xiangqi.Piece
import com.tuxunxiangqi.android.xiangqi.PieceType
import com.tuxunxiangqi.android.xiangqi.Pos
import com.tuxunxiangqi.android.xiangqi.Rules
import com.tuxunxiangqi.android.xiangqi.Side
import com.tuxunxiangqi.android.xiangqi.TargetSafety
import com.tuxunxiangqi.android.xiangqi.GameState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** The safety markers' judgement, in the four canonical cases:
 *  a safe quiet move, a suicidal quiet move （送死）, a safe
 *  capture, and a capture that gets recaptured （吃完被反吃）.
 *  Layout: red chariot on the open a-file against a black
 *  chariot, generals parked off each other's file. */
class TargetSafetyTest {

    private fun cellsOf(vararg placed: Triple<Int, Int, Piece>): List<Piece?> {
        val cells = MutableList<Piece?>(90) { null }
        for ((c, r, p) in placed) cells[r * 9 + c] = p
        return cells
    }

    /** Black chariot (0,0) owns the a-file; red chariot (0,9). */
    private fun openFilePosition(): List<Piece?> = cellsOf(
        Triple(3, 0, Piece(PieceType.GENERAL, Side.BLACK)),
        Triple(5, 9, Piece(PieceType.GENERAL, Side.RED)),
        Triple(0, 9, Piece(PieceType.CHARIOT, Side.RED)),
        Triple(0, 0, Piece(PieceType.CHARIOT, Side.BLACK))
    )

    @Test
    fun safeQuietMoveIsSafe() {
        val cells = openFilePosition()
        // Sideways along the back rank: nothing black reaches (1,9).
        assertEquals(
            TargetSafety.SAFE,
            Rules.targetSafety(cells, Pos(0, 9), Pos(1, 9))
        )
    }

    @Test
    fun quietMoveIntoTheEnemyChariotsFileIsUnsafe() {
        val cells = openFilePosition()
        // Advancing up the a-file stays under the black chariot's
        // guns: the piece would be taken next move （送死）.
        assertEquals(
            TargetSafety.UNSAFE,
            Rules.targetSafety(cells, Pos(0, 9), Pos(0, 5))
        )
    }

    @Test
    fun captureWithNoRecaptureIsSafe() {
        val cells = openFilePosition()
        // Taking the black chariot: the black general is too far
        // away to strike back at (0,0).
        assertEquals(
            TargetSafety.SAFE,
            Rules.targetSafety(cells, Pos(0, 9), Pos(0, 0))
        )
    }

    @Test
    fun captureGuardedByAHorseIsUnsafe() {
        // Same capture, but a black horse on (1,2) covers (0,0)
        // (its hobble square (1,1) is empty): 吃完被反吃.
        val cells = cellsOf(
            Triple(3, 0, Piece(PieceType.GENERAL, Side.BLACK)),
            Triple(5, 9, Piece(PieceType.GENERAL, Side.RED)),
            Triple(0, 9, Piece(PieceType.CHARIOT, Side.RED)),
            Triple(0, 0, Piece(PieceType.CHARIOT, Side.BLACK)),
            Triple(1, 2, Piece(PieceType.HORSE, Side.BLACK))
        )
        assertEquals(
            TargetSafety.UNSAFE,
            Rules.targetSafety(cells, Pos(0, 9), Pos(0, 0))
        )
        // And a quiet square away from every black piece stays safe.
        assertEquals(
            TargetSafety.SAFE,
            Rules.targetSafety(cells, Pos(0, 9), Pos(2, 9))
        )
    }

    @Test
    fun cannonRecaptureThroughAScreenIsUnsafe() {
        // Red cannon takes the black soldier on (4,3) using the
        // black soldier on (4,5) as its screen. Behind the taken
        // soldier, a black cannon on (4,0) fires back through
        // the black soldier on (4,1): exactly one screen, so
        // the capture is answered — 吃完被反吃.
        val withScreen = cellsOf(
            Triple(3, 0, Piece(PieceType.GENERAL, Side.BLACK)),
            Triple(5, 9, Piece(PieceType.GENERAL, Side.RED)),
            Triple(4, 7, Piece(PieceType.CANNON, Side.RED)),
            Triple(4, 5, Piece(PieceType.SOLDIER, Side.BLACK)),
            Triple(4, 3, Piece(PieceType.SOLDIER, Side.BLACK)),
            Triple(4, 1, Piece(PieceType.SOLDIER, Side.BLACK)),
            Triple(4, 0, Piece(PieceType.CANNON, Side.BLACK))
        )
        assertEquals(
            TargetSafety.UNSAFE,
            Rules.targetSafety(withScreen, Pos(4, 7), Pos(4, 3))
        )
        // Remove the screening soldier and the same capture is
        // safe: a cannon with no screen cannot strike back.
        val noScreen = cellsOf(
            Triple(3, 0, Piece(PieceType.GENERAL, Side.BLACK)),
            Triple(5, 9, Piece(PieceType.GENERAL, Side.RED)),
            Triple(4, 7, Piece(PieceType.CANNON, Side.RED)),
            Triple(4, 5, Piece(PieceType.SOLDIER, Side.BLACK)),
            Triple(4, 3, Piece(PieceType.SOLDIER, Side.BLACK)),
            Triple(4, 0, Piece(PieceType.CANNON, Side.BLACK))
        )
        assertEquals(
            TargetSafety.SAFE,
            Rules.targetSafety(noScreen, Pos(4, 7), Pos(4, 3))
        )
    }

    @Test
    fun soldierAttacksForwardAlwaysAndSidewaysOnlyAcrossTheRiver() {
        // Forward: the black soldier on (1,3) covers (1,4)
        // straight ahead — driving the chariot there is suicide.
        val forward = cellsOf(
            Triple(3, 0, Piece(PieceType.GENERAL, Side.BLACK)),
            Triple(5, 9, Piece(PieceType.GENERAL, Side.RED)),
            Triple(2, 4, Piece(PieceType.CHARIOT, Side.RED)),
            Triple(1, 3, Piece(PieceType.SOLDIER, Side.BLACK))
        )
        assertEquals(
            TargetSafety.UNSAFE,
            Rules.targetSafety(forward, Pos(2, 4), Pos(1, 4))
        )
        // Sideways before crossing: the black soldier on (0,4)
        // has not crossed the river, so (1,4) beside it is safe.
        val uncrossed = cellsOf(
            Triple(3, 0, Piece(PieceType.GENERAL, Side.BLACK)),
            Triple(5, 9, Piece(PieceType.GENERAL, Side.RED)),
            Triple(2, 4, Piece(PieceType.CHARIOT, Side.RED)),
            Triple(0, 4, Piece(PieceType.SOLDIER, Side.BLACK))
        )
        assertEquals(
            TargetSafety.SAFE,
            Rules.targetSafety(uncrossed, Pos(2, 4), Pos(1, 4))
        )
        // Sideways after crossing: the black soldier on (0,5)
        // covers (1,5) beside it — the same step is suicide.
        val crossed = cellsOf(
            Triple(3, 0, Piece(PieceType.GENERAL, Side.BLACK)),
            Triple(5, 9, Piece(PieceType.GENERAL, Side.RED)),
            Triple(2, 5, Piece(PieceType.CHARIOT, Side.RED)),
            Triple(0, 5, Piece(PieceType.SOLDIER, Side.BLACK))
        )
        assertEquals(
            TargetSafety.UNSAFE,
            Rules.targetSafety(crossed, Pos(2, 5), Pos(1, 5))
        )
    }

    @Test
    fun safetiesCoverExactlyTheLegalTargets() {
        val cells = openFilePosition()
        val safeties = Rules.targetSafeties(cells, Pos(0, 9))
        assertEquals(
            Rules.legalTargets(cells, Pos(0, 9)).toSet(),
            safeties.keys
        )
    }

    /** The markers must appear in the same frame as the tap:
     *  the judgement is pure local arithmetic and must stay in
     *  the millisecond range even for a whole-board sweep. This
     *  test pins that down — if anyone ever routes the judgement
     *  through the engine or an accidental O(n^3) path, it goes
     *  red. Thresholds are set ~100x above the expected cost so
     *  slow CI hardware cannot flake them. */
    @Test
    fun safetyComputationStaysMillisecondFast() {
        val start = GameState() // full 32-piece starting position
        val cells = start.cells.toList()
        val origins = ArrayList<Pos>()
        for (r in 0..9) for (c in 0..8) {
            if (start.pieceAt(Pos(c, r)) != null) origins.add(Pos(c, r))
        }
        assertEquals(32, origins.size)

        fun sweepOnce(): Int {
            var n = 0
            for (from in origins) n += Rules.targetSafeties(cells, from).size
            return n
        }

        repeat(3) { sweepOnce() } // warm up the JIT
        val t0 = System.nanoTime()
        val rounds = 20
        var total = 0
        repeat(rounds) { total += sweepOnce() }
        val sweepMs = (System.nanoTime() - t0) / 1_000_000.0 / rounds
        println("SAFETY_PERF full-board sweep (32 pieces): $sweepMs ms/round, $total targets")
        assertTrue("full-board sweep too slow: $sweepMs ms", sweepMs < 150.0)

        // One selection, the way the board UI uses it.
        val t1 = System.nanoTime()
        repeat(100) { Rules.targetSafeties(cells, Pos(1, 7)) } // red cannon
        val oneMs = (System.nanoTime() - t1) / 1_000_000.0 / 100
        println("SAFETY_PERF single selection (cannon): $oneMs ms")
        assertTrue("single selection too slow: $oneMs ms", oneMs < 50.0)
    }
}
