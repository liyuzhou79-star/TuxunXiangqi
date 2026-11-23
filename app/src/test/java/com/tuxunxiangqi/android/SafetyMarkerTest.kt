package com.tuxunxiangqi.android

import com.tuxunxiangqi.android.xiangqi.Piece
import com.tuxunxiangqi.android.xiangqi.PieceType
import com.tuxunxiangqi.android.xiangqi.Pos
import com.tuxunxiangqi.android.xiangqi.Rules
import com.tuxunxiangqi.android.xiangqi.SafetyMarker
import com.tuxunxiangqi.android.xiangqi.Side
import com.tuxunxiangqi.android.xiangqi.TargetSafety
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** The full classification -> marker chain, pinned end to end
 *  (1.38.1 audit): the four canonical positions are judged by
 *  Rules.targetSafety and then mapped by Rules.markerFor, the
 *  exact two calls the board makes. Also pins the unjudged
 *  (null) markers — plain, never the danger/safe colours — and
 *  the shared Rules.safetyMap wiring every play screen uses,
 *  so a screen that forgets to pass judgements (the puzzle
 *  screen shipped that way) or a swapped branch turns a test
 *  red instead of reaching the user's board. */
class SafetyMarkerTest {

    private fun cellsOf(vararg placed: Triple<Int, Int, Piece>): List<Piece?> {
        val cells = MutableList<Piece?>(90) { null }
        for ((c, r, p) in placed) cells[r * 9 + c] = p
        return cells
    }

    /** Same open-file layout as TargetSafetyTest. */
    private fun openFilePosition(): List<Piece?> = cellsOf(
        Triple(3, 0, Piece(PieceType.GENERAL, Side.BLACK)),
        Triple(5, 9, Piece(PieceType.GENERAL, Side.RED)),
        Triple(0, 9, Piece(PieceType.CHARIOT, Side.RED)),
        Triple(0, 0, Piece(PieceType.CHARIOT, Side.BLACK))
    )

    private fun markerAt(
        cells: List<Piece?>,
        from: Pos,
        to: Pos
    ): SafetyMarker {
        val map = Rules.safetyMap(true, cells, from)
        return Rules.markerFor(cells[to.row * 9 + to.col] != null, map[to])
    }

    @Test
    fun safeQuietMoveGetsTheGreenDot() {
        val cells = openFilePosition()
        assertEquals(
            SafetyMarker.SAFE_DOT,
            markerAt(cells, Pos(0, 9), Pos(1, 9))
        )
    }

    @Test
    fun suicidalQuietMoveGetsTheRedDot() {
        val cells = openFilePosition()
        assertEquals(
            SafetyMarker.DANGER_DOT,
            markerAt(cells, Pos(0, 9), Pos(0, 5))
        )
    }

    @Test
    fun safeCaptureGetsTheGreenRing() {
        val cells = openFilePosition()
        assertEquals(
            SafetyMarker.SAFE_RING,
            markerAt(cells, Pos(0, 9), Pos(0, 0))
        )
    }

    @Test
    fun recapturedCaptureGetsNoMarkerAtAll() {
        val cells = cellsOf(
            Triple(3, 0, Piece(PieceType.GENERAL, Side.BLACK)),
            Triple(5, 9, Piece(PieceType.GENERAL, Side.RED)),
            Triple(0, 9, Piece(PieceType.CHARIOT, Side.RED)),
            Triple(0, 0, Piece(PieceType.CHARIOT, Side.BLACK)),
            Triple(1, 2, Piece(PieceType.HORSE, Side.BLACK))
        )
        assertEquals(
            SafetyMarker.NO_MARKER,
            markerAt(cells, Pos(0, 9), Pos(0, 0))
        )
    }

    @Test
    fun unjudgedMarkersArePlainNeverColoured() {
        // Hints off / a screen that must not judge: the marker
        // may say "you can go here" but never "safe" or "dead".
        assertEquals(
            SafetyMarker.PLAIN_DOT,
            Rules.markerFor(capture = false, safety = null)
        )
        assertEquals(
            SafetyMarker.PLAIN_RING,
            Rules.markerFor(capture = true, safety = null)
        )
    }

    @Test
    fun safetyMapFollowsTheSharedWiringContract() {
        val cells = openFilePosition()
        // Enabled + a selection: judgements for exactly the
        // legal targets, with the canonical verdicts inside.
        val on = Rules.safetyMap(true, cells, Pos(0, 9))
        assertEquals(Rules.legalTargets(cells, Pos(0, 9)).toSet(), on.keys)
        assertEquals(TargetSafety.SAFE, on[Pos(1, 9)])
        assertEquals(TargetSafety.UNSAFE, on[Pos(0, 5)])
        // Disabled, or nothing selected: no judgements at all,
        // so the board falls back to plain markers everywhere.
        assertTrue(Rules.safetyMap(false, cells, Pos(0, 9)).isEmpty())
        assertTrue(Rules.safetyMap(true, cells, null).isEmpty())
    }
}
