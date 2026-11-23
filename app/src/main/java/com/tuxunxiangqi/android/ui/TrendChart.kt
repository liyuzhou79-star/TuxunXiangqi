package com.tuxunxiangqi.android.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp

/**
 * Evaluation trend: one point per evaluated position, x = move
 * number, y = score from Red's point of view (centipawns, clamped
 * to ±1000 for display). Points where the score swung by 250cp or
 * more against the previous point are marked as key moments.
 */
@Composable
fun EvalTrendChart(
    points: List<Pair<Int, Int>>,
    modifier: Modifier = Modifier,
    big: Boolean = false,
    highlightMove: Int? = null,
    onSelectMove: ((Int) -> Unit)? = null
) {
    val lineColor = MaterialTheme.colorScheme.primary
    val keyColor = MaterialTheme.colorScheme.error
    val axisColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
    val highlightColor = MaterialTheme.colorScheme.tertiary
    val tapModifier = if (onSelectMove != null && points.size >= 2) {
        Modifier.pointerInput(points, onSelectMove) {
            detectTapGestures { offset ->
                nearestTrendMove(points, offset.x / size.width)
                    ?.let { onSelectMove(it) }
            }
        }
    } else {
        Modifier
    }
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(if (big) 180.dp else 64.dp)
            .then(tapModifier)
    ) {
        if (points.size < 2) {
            // Zero axis only; the card text explains the state.
            drawLine(
                color = axisColor,
                start = Offset(0f, size.height / 2f),
                end = Offset(size.width, size.height / 2f),
                strokeWidth = 1.5f
            )
            return@Canvas
        }
        val maxMove = maxOf(points.maxOf { it.first }, 10)
        fun x(moveIdx: Int) = size.width * (moveIdx.toFloat() / maxMove)
        fun y(cp: Int): Float {
            val clamped = cp.coerceIn(-1000, 1000)
            return size.height / 2f - (clamped / 1000f) * (size.height / 2f - 4f)
        }
        drawLine(
            color = axisColor,
            start = Offset(0f, size.height / 2f),
            end = Offset(size.width, size.height / 2f),
            strokeWidth = 1.5f
        )
        val path = Path()
        points.forEachIndexed { i, (moveIdx, cp) ->
            val px = x(moveIdx)
            val py = y(cp)
            if (i == 0) path.moveTo(px, py) else path.lineTo(px, py)
        }
        drawPath(path = path, color = lineColor, style = Stroke(width = 2.5f))
        points.forEachIndexed { i, (moveIdx, cp) ->
            val isKey = i > 0 && kotlin.math.abs(cp - points[i - 1].second) >= 250
            drawCircle(
                color = if (isKey) keyColor else lineColor,
                radius = if (isKey) 5f else 3f,
                center = Offset(x(moveIdx), y(cp))
            )
        }
        // Review linkage: ring + guide line on the step currently
        // shown on the board, so chart and board stay in sync.
        if (highlightMove != null) {
            val hp = points.firstOrNull { it.first == highlightMove }
            if (hp != null) {
                val hx = x(hp.first)
                drawLine(
                    color = highlightColor.copy(alpha = 0.45f),
                    start = Offset(hx, 0f),
                    end = Offset(hx, size.height),
                    strokeWidth = 1.5f
                )
                drawCircle(
                    color = highlightColor,
                    radius = 7f,
                    center = Offset(hx, y(hp.second)),
                    style = Stroke(width = 2.5f)
                )
            }
        }
    }
}

/** The evaluated move nearest to a horizontal tap position on
 *  the chart (fraction = tap x / chart width, clamped to 0..1).
 *  Uses the same x mapping as the drawing code — move index over
 *  max(last move, 10) — so the tapped point is the one the finger
 *  is actually on. Pure, so the mapping is pinned by unit tests. */
fun nearestTrendMove(points: List<Pair<Int, Int>>, fraction: Float): Int? {
    if (points.isEmpty()) return null
    val maxMove = maxOf(points.maxOf { it.first }, 10)
    val target = fraction.coerceIn(0f, 1f) * maxMove
    return points.minByOrNull { kotlin.math.abs(it.first - target) }?.first
}

/** Move numbers (1-based move counts) where the evaluation swung
 *  hard, with the swing in pawns, biggest first. */
fun keySwings(points: List<Pair<Int, Int>>): List<Triple<Int, Int, Int>> {
    val out = ArrayList<Triple<Int, Int, Int>>()
    for (i in 1 until points.size) {
        val delta = points[i].second - points[i - 1].second
        if (kotlin.math.abs(delta) >= 250) {
            out.add(Triple(points[i].first, points[i].second, delta))
        }
    }
    return out.sortedByDescending { kotlin.math.abs(it.third) }
}
