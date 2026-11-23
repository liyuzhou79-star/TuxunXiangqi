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
    fun missingScoresGiveNoLoss() {
        assertNull(EvalUtil.lossVsBestCp(null, null, 100, null))
        assertNull(EvalUtil.lossVsBestCp(100, null, null, null))
        assertNull(EvalUtil.flatScore(null, null))
    }
}
