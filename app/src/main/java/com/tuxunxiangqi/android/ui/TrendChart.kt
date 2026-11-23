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
import com.tuxunxiangqi.android.xiangqi.EvalUtil

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

/** Display fraction (0..1) for one deep-loss bar: the scale
 *  tops out at 500cp (five pawns), so anything at or beyond
 *  that draws full height. A negative loss (the played move
 *  scored above the listed best, possible with stale data)
 *  draws as zero. Pure, so the scale is pinned by unit tests. */
fun deepLossBarFraction(lossCp: Int): Float =
    lossCp.coerceIn(0, 500) / 500f

/** Colour band for a deep-loss value, mirroring the move
 *  marks: 0 below the questionable threshold, 1 in the
 *  questionable band, 2 at blunder height. Pure, so the
 *  thresholds are pinned by unit tests. */
fun deepLossBand(lossCp: Int): Int = when {
    lossCp >= EvalUtil.BLUNDER_CP -> 2
    lossCp >= EvalUtil.QUESTIONABLE_CP -> 1
    else -> 0
}

/** The deep-loss point nearest to a horizontal tap position:
 *  bars are laid out in equal slots, so the tap fraction picks
 *  the slot. Pure, same mapping as the drawing code. */
fun nearestDeepLossStep(
    points: List<Pair<Int, Int>>,
    fraction: Float
): Int? {
    if (points.isEmpty()) return null
    val idx = (fraction.coerceIn(0f, 1f) * points.size)
        .toInt().coerceIn(0, points.size - 1)
    return points[idx].first
}

/**
 * Deep-analysis loss bars (1.36.0): one bar per deep-analysed
 * step, height = how much the move actually played gave away
 * against the engine's deep best line at that step. Tapping a
 * bar jumps the review to that step.
 */
@Composable
fun DeepLossChart(
    points: List<Pair<Int, Int>>,
    modifier: Modifier = Modifier,
    onSelectStep: ((Int) -> Unit)? = null
) {
    val plainColor = MaterialTheme.colorScheme.onSurfaceVariant
        .copy(alpha = 0.45f)
    val questionColor = MaterialTheme.colorScheme.primary
    val blunderColor = MaterialTheme.colorScheme.error
    val axisColor = MaterialTheme.colorScheme.onSurfaceVariant
        .copy(alpha = 0.5f)
    val tapModifier = if (onSelectStep != null && points.isNotEmpty()) {
        Modifier.pointerInput(points, onSelectStep) {
            detectTapGestures { offset ->
                nearestDeepLossStep(points, offset.x / size.width)
                    ?.let { onSelectStep(it) }
            }
        }
    } else {
        Modifier
    }
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .then(tapModifier)
    ) {
        drawLine(
            color = axisColor,
            start = Offset(0f, size.height),
            end = Offset(size.width, size.height),
            strokeWidth = 1.5f
        )
        if (points.isEmpty()) return@Canvas
        val slot = size.width / points.size
        val barWidth = slot * 0.55f
        points.forEachIndexed { i, (_, loss) ->
            val barHeight = deepLossBarFraction(loss) *
                (size.height - 4f)
            val color = when (deepLossBand(loss)) {
                2 -> blunderColor
                1 -> questionColor
                else -> plainColor
            }
            val left = slot * i + (slot - barWidth) / 2f
            drawRect(
                color = color,
                topLeft = Offset(left, size.height - barHeight),
                size = androidx.compose.ui.geometry.Size(
                    barWidth, barHeight
                )
            )
        }
    }
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
