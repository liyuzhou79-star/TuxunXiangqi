package com.tuxunxiangqi.android

import com.tuxunxiangqi.android.xiangqi.EvalUtil
import com.tuxunxiangqi.android.xiangqi.Side
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/** Whole-game analysis verdicts: a move is judged by how much
 *  score the MOVER gave away between the positions before and
 *  after it, with Red-perspective stored scores. */
class BatchAnalysisTest {

    @Test
    fun redBlunderIsMarked() {
        // Red to move at 0, throws away 300cp with move 1.
        val evals = mapOf(0 to 0, 1 to -300)
        assertEquals(300, EvalUtil.moveLossCp(evals, Side.RED, 1))
        assertEquals(
            mapOf(1 to "??"),
            EvalUtil.classifyMoves(evals, Side.RED, 1)
        )
    }

    @Test
    fun blackGainIsNotAMark() {
        // Move 2 is Black's: Red's score falling is Black gaining.
        val evals = mapOf(0 to 0, 1 to 50, 2 to -200)
        assertEquals(-250, EvalUtil.moveLossCp(evals, Side.RED, 2))
        assertEquals(
            emptyMap<Int, String>(),
            EvalUtil.classifyMoves(evals, Side.RED, 2)
        )
    }

    @Test
    fun blackBlunderIsMarkedFromBlackPerspective() {
        // Move 2 is Black's and Red's score jumps: Black blundered.
        val evals = mapOf(0 to 0, 1 to 10, 2 to 280)
        assertEquals(270, EvalUtil.moveLossCp(evals, Side.RED, 2))
        assertEquals(
            mapOf(2 to "??"),
            EvalUtil.classifyMoves(evals, Side.RED, 2)
        )
    }

    @Test
    fun questionableBandAndThresholds() {
        val evals = mapOf(0 to 0, 1 to -150, 2 to -160, 3 to -270)
        // Move 1 (Red): loss 150 -> "?". Move 2 (Black): loss
        // (before -160... mover Black) = (-150 - -160) * -1 = -10
        // -> nothing. Move 3 (Red): loss = -160 - -270 = 110 ->
        // below the questionable threshold, nothing.
        assertEquals(
            mapOf(1 to "?"),
            EvalUtil.classifyMoves(evals, Side.RED, 3)
        )
    }

    @Test
    fun missingMeasurementsYieldNoVerdict() {
        val evals = mapOf(0 to 0, 2 to -500)
        assertNull(EvalUtil.moveLossCp(evals, Side.RED, 1))
        assertNull(EvalUtil.moveLossCp(evals, Side.RED, 2))
        assertEquals(
            emptyMap<Int, String>(),
            EvalUtil.classifyMoves(evals, Side.RED, 2)
        )
    }

    @Test
    fun blackToMoveFirstFlipsMover() {
        // Position 0 is Black to move (handicap / editor game):
        // move 1 is Black's, Red gaining 200 means Black lost 200.
        val evals = mapOf(0 to 0, 1 to 200)
        assertEquals(200, EvalUtil.moveLossCp(evals, Side.BLACK, 1))
        assertEquals(
            mapOf(1 to "?"),
            EvalUtil.classifyMoves(evals, Side.BLACK, 1)
        )
    }
}
