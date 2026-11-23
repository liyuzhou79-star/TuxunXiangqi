package com.tuxunxiangqi.android

import com.tuxunxiangqi.android.xiangqi.EvalUtil
import com.tuxunxiangqi.android.xiangqi.Side
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/** The review page's mistake-replay list (1.30.0): blunders
 *  before questionables, biggest score loss first inside each
 *  group, unmeasured moves last in their group. */
class MarkedMovesTest {

    @Test
    fun blundersComeFirstThenByLoss() {
        // Red starts. Move 1 (Red) loses 300, move 2 (Black)
        // loses 150, move 3 (Red) loses 500, move 4 (Black)
        // loses 130.
        val evals = mapOf(0 to 0, 1 to -300, 2 to -150, 3 to -650, 4 to -520)
        val marks = mapOf(1 to "??", 2 to "?", 3 to "??", 4 to "?")
        val out = EvalUtil.markedMoves(marks, evals, Side.RED)
        assertEquals(listOf(3, 1, 2, 4), out.map { it.moveNo })
        assertEquals(listOf(500, 300, 150, 130), out.map { it.lossCp })
        assertEquals("??", out[0].mark)
        assertEquals("?", out[2].mark)
    }

    @Test
    fun unmeasuredMovesSortLastInTheirGroup() {
        // Move 5 is marked but its "after" eval was never
        // measured, so its loss is unknown.
        val evals = mapOf(0 to 0, 1 to -300)
        val marks = mapOf(1 to "??", 5 to "??", 3 to "?")
        val out = EvalUtil.markedMoves(marks, evals, Side.RED)
        assertEquals(listOf(1, 5, 3), out.map { it.moveNo })
        assertNull(out[1].lossCp)
        assertNull(out[2].lossCp)
    }

    @Test
    fun blackStartFlipsTheMover() {
        // Black to move at 0: move 1 is Black's, and Red's
        // score RISING by 400 means Black gave 400 away.
        val evals = mapOf(0 to 0, 1 to 400)
        val marks = mapOf(1 to "??")
        val out = EvalUtil.markedMoves(marks, evals, Side.BLACK)
        assertEquals(400, out.single().lossCp)
    }

    @Test
    fun emptyMarksGiveEmptyList() {
        assertEquals(
            emptyList<EvalUtil.MarkedMove>(),
            EvalUtil.markedMoves(emptyMap(), emptyMap(), Side.RED)
        )
    }
}
