package com.tuxunxiangqi.android

import com.tuxunxiangqi.android.ui.deepLossBand
import com.tuxunxiangqi.android.ui.deepLossBarFraction
import com.tuxunxiangqi.android.ui.nearestDeepLossStep
import com.tuxunxiangqi.android.ui.nearestTrendMove
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/** The trend chart's tap-to-jump mapping (1.27.0): a tap at a
 *  horizontal fraction of the chart width must land on the
 *  evaluated move drawn nearest to that x, using the same
 *  max(last move, 10) scale as the drawing code. */
class TrendChartTest {

    private val points = listOf(0 to 10, 5 to 40, 10 to -20, 20 to 300, 40 to 120)

    @Test
    fun emptyPointsGiveNull() {
        assertNull(nearestTrendMove(emptyList(), 0.5f))
    }

    @Test
    fun tapAtLeftEdgePicksFirstPoint() {
        assertEquals(0, nearestTrendMove(points, 0f))
    }

    @Test
    fun tapAtRightEdgePicksLastPoint() {
        assertEquals(40, nearestTrendMove(points, 1f))
    }

    @Test
    fun tapNearAMovePicksThatMove() {
        // maxMove = 40, so move 20 sits at fraction 0.5 and
        // move 10 at 0.25.
        assertEquals(20, nearestTrendMove(points, 0.5f))
        assertEquals(10, nearestTrendMove(points, 0.27f))
        assertEquals(5, nearestTrendMove(points, 0.1f))
    }

    @Test
    fun outOfRangeFractionsClamp() {
        assertEquals(0, nearestTrendMove(points, -0.4f))
        assertEquals(40, nearestTrendMove(points, 1.7f))
    }

    @Test
    fun shortGamesUseTheTenMoveFloor() {
        // With a last move of 4 the scale floor is 10, so move 4
        // sits at fraction 0.4 — not at the right edge.
        val short = listOf(0 to 0, 2 to 50, 4 to -30)
        assertEquals(4, nearestTrendMove(short, 0.4f))
        assertEquals(4, nearestTrendMove(short, 0.9f))
        assertEquals(0, nearestTrendMove(short, 0.05f))
    }

    @Test
    fun deepLossScaleClamps() {
        // The 1.36.0 deep-loss bars: 500cp (five pawns) is full
        // height, negatives draw as zero.
        assertEquals(0f, deepLossBarFraction(-50), 0.001f)
        assertEquals(0f, deepLossBarFraction(0), 0.001f)
        assertEquals(0.5f, deepLossBarFraction(250), 0.001f)
        assertEquals(1f, deepLossBarFraction(500), 0.001f)
        assertEquals(1f, deepLossBarFraction(1400), 0.001f)
    }

    @Test
    fun deepLossBandsFollowTheMoveMarks() {
        assertEquals(0, deepLossBand(0))
        assertEquals(0, deepLossBand(119))
        assertEquals(1, deepLossBand(120))
        assertEquals(1, deepLossBand(249))
        assertEquals(2, deepLossBand(250))
    }

    @Test
    fun deepLossTapPicksTheSlot() {
        val pts = listOf(2 to 10, 4 to 300, 7 to 60)
        assertEquals(2, nearestDeepLossStep(pts, 0f))
        assertEquals(4, nearestDeepLossStep(pts, 0.5f))
        assertEquals(7, nearestDeepLossStep(pts, 1f))
        assertNull(nearestDeepLossStep(emptyList(), 0.5f))
    }
}
