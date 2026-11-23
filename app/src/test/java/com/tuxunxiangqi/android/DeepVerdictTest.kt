package com.tuxunxiangqi.android

import com.tuxunxiangqi.android.xiangqi.EvalUtil
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/** The review page's single-step deep analysis turns an engine
 *  candidate list into two plain facts: where the move actually
 *  played ranks, and how much it gave away versus the best line.
 *  Both come from these pure helpers. */
class DeepVerdictTest {

    private val ucis = listOf("b2e2", "h2e2", "b0c2", "a0a1")

    @Test
    fun rankOfBestMoveIsOne() {
        assertEquals(1, EvalUtil.rankOfMove(ucis, "b2e2"))
    }

    @Test
    fun rankOfLastCandidateIsFour() {
        assertEquals(4, EvalUtil.rankOfMove(ucis, "a0a1"))
    }

    @Test
    fun moveOutsideCandidatesHasNoRank() {
        assertNull(EvalUtil.rankOfMove(ucis, "c3c4"))
        assertNull(EvalUtil.rankOfMove(emptyList(), "b2e2"))
    }

    @Test
    fun lossIsBestMinusMove() {
        assertEquals(0, EvalUtil.lossVsBestCp(120, null, 120, null))
        assertEquals(180, EvalUtil.lossVsBestCp(120, null, -60, null))
        // A move that scores better than the "best" (should not
        // happen with engine output) yields a negative loss.
        assertEquals(-30, EvalUtil.lossVsBestCp(50, null, 80, null))
    }

    @Test
    fun mateScoresFlattenToTheCap() {
        assertEquals(1500, EvalUtil.flatScore(null, 3))
        assertEquals(-1500, EvalUtil.flatScore(null, -2))
        assertEquals(1500 - 400, EvalUtil.lossVsBestCp(null, 3, 400, null))
        assertEquals(1500 - (-1500), EvalUtil.lossVsBestCp(null, 1, null, -1))
    }

    @Test
    fun deepLossPointsUseCandidateScores() {
        // The 1.36.0 loss chart: played move among the deep
        // candidates — loss is best-minus-played at that step.
        val steps = mapOf(
            0 to listOf("b2e2" to 120, "h2e2" to 40),
            1 to listOf("b0c2" to -80, "a0a1" to -260)
        )
        val pts = EvalUtil.deepLossPoints(
            steps, listOf("h2e2", "a0a1")
        ) { null }
        assertEquals(listOf(0 to 80, 1 to 180), pts)
    }

    @Test
    fun deepLossPointsFallBackToBatchLoss() {
        // Played move outside the deep top four: the whole-game
        // batch loss for that move number (1-based) stands in.
        val steps = mapOf(2 to listOf("b2e2" to 300))
        val pts = EvalUtil.deepLossPoints(
            steps, listOf("h2e2", "h9g7", "c3c4")
        ) { moveNo -> if (moveNo == 3) 145 else null }
        assertEquals(listOf(2 to 145), pts)
    }

    @Test
    fun deepLossPointsSkipUnmeasuredSteps() {
        val steps = mapOf(
            0 to listOf("b2e2" to 10),
            5 to listOf("b2e2" to 10)
        )
        // Step 5 is past the end of the game: no played move.
        val pts = EvalUtil.deepLossPoints(steps, listOf("b2e2")) { null }
        assertEquals(listOf(0 to 0), pts)
        // Played move outside candidates and no batch loss: gone.
        val pts2 = EvalUtil.deepLossPoints(steps, listOf("c3c4")) { null }
        assertEquals(emptyList<Pair<Int, Int>>(), pts2)
    }

    @Test
    fun deepLossPointsComeBackInStepOrder() {
        val steps = mapOf(
            3 to listOf("b2e2" to 0, "h2e2" to -50),
            1 to listOf("b2e2" to 0, "h2e2" to -30)
        )
        val actual = listOf("x0x0", "h2e2", "x0x0", "h2e2")
        val pts = EvalUtil.deepLossPoints(steps, actual) { null }
        assertEquals(listOf(1 to 30, 3 to 50), pts)
    }

    @Test
    fun missingScoresGiveNoLoss() {
        assertNull(EvalUtil.lossVsBestCp(null, null, 100, null))
        assertNull(EvalUtil.lossVsBestCp(100, null, null, null))
        assertNull(EvalUtil.flatScore(null, null))
    }
}
