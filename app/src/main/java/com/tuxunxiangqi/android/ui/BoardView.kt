package com.tuxunxiangqi.android.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.tuxunxiangqi.android.R
import com.tuxunxiangqi.android.xiangqi.GameState
import com.tuxunxiangqi.android.xiangqi.Piece
import com.tuxunxiangqi.android.xiangqi.Pos
import com.tuxunxiangqi.android.xiangqi.Side
import kotlin.math.roundToInt
import kotlin.random.Random

/**
 * The board follows the game-grade board language established in
 * TuxunGo 2.60: a walnut frame with layered wood-tone gradients and
 * an inner bevel, a real wood-grain face, and pieces drawn as thick
 * discs with a lit top-left. The content is xiangqi: 9x10 lines,
 * the river with 楚河汉界, both palaces and red/black glyph pieces.
 */

private val FrameDark = Color(0xFF3E2712)
private val FaceLight = Color(0xFFDCB57F)
private val FaceMid = Color(0xFFD2A763)
private val FaceDeep = Color(0xFFC79A55)
private val GridInk = Color(0xFF2A1D0E)
private val RedInk = Color(0xFFB33A1F)
private val BlackInk = Color(0xFF2A241C)

/** Brush calligraphy for piece glyphs and the river text:
 *  Bakudai Bold （莫大毛筆體）, OFL-1.1, subset to the glyphs the
 *  board needs. See THIRD_PARTY_NOTICES.md. */
val PieceFontFamily = FontFamily(Font(R.font.bakudai_bold, FontWeight.Bold))

/** The bundled walnut-grain photo (CC BY-SA 3.0; see
 *  THIRD_PARTY_NOTICES.md), decoded once and remembered. */
@Composable
private fun rememberWoodBitmap(): ImageBitmap {
    val context = LocalContext.current
    return remember {
        android.graphics.BitmapFactory.decodeResource(
            context.resources, R.drawable.board_wood
        ).asImageBitmap()
    }
}

@Composable
fun BoardFrame(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val wood = rememberWoodBitmap()
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, FrameDark, RoundedCornerShape(12.dp))
    ) {
        // Real walnut grain under a translucent walnut gradient,
        // so the rim reads as solid dark wood, not a flat fill.
        Image(
            bitmap = wood,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            colorFilter = ColorFilter.tint(
                Color(0xFF8A6A3C),
                androidx.compose.ui.graphics.BlendMode.Modulate
            ),
            modifier = Modifier.matchParentSize()
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFF8A6238).copy(alpha = 0.72f),
                            Color(0xFF6F4C2A).copy(alpha = 0.78f),
                            Color(0xFF59391E).copy(alpha = 0.85f)
                        )
                    )
                )
        )
        Box(
            modifier = Modifier
                .padding(9.dp)
                .border(
                    1.dp, Color(0xFF2E1C0C).copy(alpha = 0.65f),
                    RoundedCornerShape(4.dp)
                )
        ) {
            content()
        }
    }
}

@Composable
fun XiangqiBoard(
    state: GameState,
    selected: Pos?,
    legalTargets: List<Pos>,
    onTap: (Pos) -> Unit,
    modifier: Modifier = Modifier,
    hint: Pair<Pos, Pos>? = null,
    flipped: Boolean = false,
    showCoords: Boolean = false,
    extraHints: List<Pair<Pos, Pos>> = emptyList()
) {
    val measurer = rememberTextMeasurer()
    val wood = rememberWoodBitmap()
    // Board geometry in cell units: grid is 8 wide x 9 tall, with a
    // 0.62-cell margin so edge pieces never clip.
    val marginUnits = 0.62f
    val totalW = 8f + marginUnits * 2
    val totalH = 9f + marginUnits * 2
    fun disp(p: Pos): Pos =
        if (flipped) Pos(8 - p.col, 9 - p.row) else p
    // Move animation: the just-moved piece glides from its origin
    // to its destination over ~180 ms with a slight lift, instead
    // of teleporting. Keyed on moveCount so undo/redo and review
    // stepping retrigger it, while the first composition does not.
    val moveProgress = remember { Animatable(1f) }
    LaunchedEffect(state.moveCount) {
        if (state.moveCount > 0 && state.lastMove != null) {
            moveProgress.snapTo(0f)
            moveProgress.animateTo(
                1f,
                tween(durationMillis = 180, easing = FastOutSlowInEasing)
            )
        } else {
            moveProgress.snapTo(1f)
        }
    }
    val animating = moveProgress.value < 1f && state.lastMove != null
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(totalW / totalH)
            .pointerInput(state, flipped) {
                detectTapGestures { offset ->
                    val cell = size.width / totalW
                    val col = ((offset.x / cell) - marginUnits).roundToInt()
                    val row = ((offset.y / cell) - marginUnits).roundToInt()
                    val pos = disp(Pos(col, row))
                    if (pos.inside()) onTap(pos)
                }
            }
    ) {
        val cell = size.width / totalW
        fun point(p: Pos): Offset {
            val d = disp(p)
            return Offset(
                (marginUnits + d.col) * cell,
                (marginUnits + d.row) * cell
            )
        }

        drawWoodFace(wood, cell, totalW, totalH)
        drawGrid(cell, marginUnits)
        drawRiverText(measurer, cell, marginUnits)
        if (showCoords) drawCoords(measurer, cell, marginUnits, flipped)

        // Last-move markers, kept deliberately quiet: a soft
        // glowing dot where the piece came from, and a white glow
        // ring around the piece where it landed. No frames.
        state.lastMove?.let { mv ->
            drawGlowDot(point(mv.from), cell)
            drawGlowHalo(point(mv.to), cell)
        }
        // Legal-target hints for the selected piece.
        for (t in legalTargets) {
            val c = point(t)
            if (state.pieceAt(t) == null) {
                drawCircle(
                    color = RedInk.copy(alpha = 0.45f),
                    radius = cell * 0.11f,
                    center = c
                )
            } else {
                drawCircle(
                    color = RedInk.copy(alpha = 0.16f),
                    radius = cell * 0.46f,
                    center = c
                )
                drawCircle(
                    color = RedInk,
                    radius = cell * 0.46f,
                    center = c,
                    style = Stroke(width = cell * 0.045f)
                )
            }
        }
        // Pieces. While the move animation runs, the moved piece
        // is drawn separately at its interpolated position.
        val mvNow = state.lastMove
        for (r in 0..9) {
            for (c in 0..8) {
                val pos = Pos(c, r)
                val piece = state.pieceAt(pos) ?: continue
                if (animating && mvNow != null && pos == mvNow.to) continue
                drawPiece(measurer, point(pos), cell * 0.44f, piece, pos == selected)
            }
        }
        if (animating && mvNow != null) {
            val moving = state.pieceAt(mvNow.to)
            if (moving != null) {
                val p = moveProgress.value
                val a = point(mvNow.from)
                val b = point(mvNow.to)
                val at = Offset(
                    a.x + (b.x - a.x) * p,
                    a.y + (b.y - a.y) * p
                )
                val lift = kotlin.math.sin(p * Math.PI.toFloat()).toFloat()
                drawPiece(
                    measurer, at, cell * 0.44f * (1f + 0.10f * lift),
                    moving, selected = false, liftShadow = lift
                )
            }
        }
        // The bright rim of the destination glow, above the piece
        // so it wraps the disc that just landed.
        state.lastMove?.let { mv -> drawGlowRing(point(mv.to), cell) }
        // Engine hint arrow, drawn on top so it stays readable.
        hint?.let { (from, to) ->
            drawHintArrow(point(from), point(to), cell)
        }
        // Extra candidate arrows (ranks 2..4) in fading blue.
        for ((from, to) in extraHints) {
            drawHintArrow(point(from), point(to), cell, faint = true)
        }
    }
}

private fun DrawScope.drawCoords(
    measurer: TextMeasurer,
    cell: Float,
    marginUnits: Float,
    flipped: Boolean
) {
    val style = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = (cell * 0.26f).toSp(),
        color = GridInk.copy(alpha = 0.75f)
    )
    for (c in 0..8) {
        val logicalCol = if (flipped) 8 - c else c
        val label = ('a' + logicalCol).toString()
        val layout = measurer.measure(label, style)
        drawText(
            textLayoutResult = layout,
            topLeft = Offset(
                (marginUnits + c) * cell - layout.size.width / 2f,
                (marginUnits + 9) * cell + cell * 0.18f
            )
        )
    }
    for (r in 0..9) {
        val logicalRow = if (flipped) 9 - r else r
        val label = (9 - logicalRow).toString()
        val layout = measurer.measure(label, style)
        drawText(
            textLayoutResult = layout,
            topLeft = Offset(
                cell * 0.14f,
                (marginUnits + r) * cell - layout.size.height / 2f
            )
        )
    }
}

private val HintBlue = Color(0xFF1E6FB8)

private fun DrawScope.drawHintArrow(
    from: Offset,
    to: Offset,
    cell: Float,
    faint: Boolean = false
) {
    val r = cell * 0.46f
    val blue = if (faint) HintBlue.copy(alpha = 0.45f) else HintBlue
    // Rings on both ends.
    drawCircle(
        color = blue,
        radius = r,
        center = from,
        style = Stroke(width = cell * 0.07f)
    )
    drawCircle(
        color = blue.copy(alpha = 0.30f),
        radius = r,
        center = to
    )
    drawCircle(
        color = blue,
        radius = r,
        center = to,
        style = Stroke(width = cell * 0.07f)
    )
    // Shaft, shortened so it does not hide the pieces.
    val dx = to.x - from.x
    val dy = to.y - from.y
    val len = kotlin.math.sqrt(dx * dx + dy * dy)
    if (len > 1f) {
        val ux = dx / len
        val uy = dy / len
        val start = Offset(from.x + ux * r, from.y + uy * r)
        val end = Offset(to.x - ux * r, to.y - uy * r)
        drawLine(
            color = blue,
            start = start,
            end = end,
            strokeWidth = cell * 0.10f
        )
        // Arrowhead.
        val ah = cell * 0.30f
        val px = -uy
        val py = ux
        drawLine(
            color = blue,
            start = end,
            end = Offset(end.x - ux * ah + px * ah * 0.55f, end.y - uy * ah + py * ah * 0.55f),
            strokeWidth = cell * 0.10f
        )
        drawLine(
            color = blue,
            start = end,
            end = Offset(end.x - ux * ah - px * ah * 0.55f, end.y - uy * ah - py * ah * 0.55f),
            strokeWidth = cell * 0.10f
        )
    }
}

private fun DrawScope.drawWoodFace(
    wood: ImageBitmap,
    cell: Float,
    totalW: Float,
    totalH: Float
) {
    drawRect(
        brush = Brush.verticalGradient(listOf(FaceLight, FaceMid, FaceDeep)),
        size = size
    )
    // Real walnut grain (Wikimedia Commons, CC BY-SA 3.0; see
    // THIRD_PARTY_NOTICES.md), center-cropped to the face's
    // portrait aspect and laid over the warm base so the board
    // has true photographic grain instead of flat color.
    val srcW = (wood.height * (totalW / totalH)).toInt()
        .coerceAtMost(wood.width)
    drawImage(
        image = wood,
        srcOffset = IntOffset((wood.width - srcW) / 2, 0),
        srcSize = IntSize(srcW, wood.height),
        dstSize = IntSize(size.width.toInt(), size.height.toInt()),
        alpha = 0.52f
    )
    // Procedural grain: soft darker and lighter streaks running
    // across the board, deterministic so the face never flickers.
    val rnd = Random(20261123)
    repeat(22) { i ->
        val yBase = (i + rnd.nextFloat() * 0.7f) * size.height / 22f
        val dark = i % 3 != 0
        val path = Path().apply {
            moveTo(-10f, yBase)
            var x = 0f
            var y = yBase
            while (x < size.width) {
                val nx = x + size.width / 4f
                val ny = yBase + (rnd.nextFloat() - 0.5f) * cell * 0.22f
                quadraticBezierTo(x + size.width / 8f, y, nx, ny)
                x = nx
                y = ny
            }
        }
        drawPath(
            path = path,
            color = if (dark) Color(0xFF8A5A24).copy(alpha = 0.04f)
            else Color(0xFFFFEFC4).copy(alpha = 0.045f),
            style = Stroke(width = cell * (0.03f + rnd.nextFloat() * 0.05f))
        )
    }
    // Vignette: edges fall off into shade so the face feels like
    // a lit wooden surface rather than a flat sheet.
    drawRect(
        brush = Brush.radialGradient(
            colors = listOf(
                Color.Transparent,
                Color.Transparent,
                Color(0xFF3A2410).copy(alpha = 0.16f)
            ),
            center = center,
            radius = size.maxDimension * 0.72f
        ),
        size = size
    )
    drawRect(
        color = GridInk.copy(alpha = 0.55f),
        style = Stroke(width = 1.5f)
    )
}

private fun DrawScope.drawGrid(cell: Float, marginUnits: Float) {
    fun pt(col: Int, row: Int) = Offset(
        (marginUnits + col) * cell,
        (marginUnits + row) * cell
    )
    val w = cell * 0.028f
    // Horizontal lines.
    for (r in 0..9) {
        drawLine(GridInk, pt(0, r), pt(8, r), strokeWidth = w)
    }
    // Vertical lines: the two edges run full height; inner lines
    // stop at the river.
    for (c in 0..8) {
        if (c == 0 || c == 8) {
            drawLine(GridInk, pt(c, 0), pt(c, 9), strokeWidth = w)
        } else {
            drawLine(GridInk, pt(c, 0), pt(c, 4), strokeWidth = w)
            drawLine(GridInk, pt(c, 5), pt(c, 9), strokeWidth = w)
        }
    }
    // Palace diagonals.
    drawLine(GridInk, pt(3, 0), pt(5, 2), strokeWidth = w)
    drawLine(GridInk, pt(5, 0), pt(3, 2), strokeWidth = w)
    drawLine(GridInk, pt(3, 7), pt(5, 9), strokeWidth = w)
    drawLine(GridInk, pt(5, 7), pt(3, 9), strokeWidth = w)
    // Thicker outer border.
    drawRect(
        color = GridInk,
        topLeft = pt(0, 0),
        size = androidx.compose.ui.geometry.Size(8 * cell, 9 * cell),
        style = Stroke(width = cell * 0.05f)
    )
    // Note: the traditional printed corner ticks at cannon and
    // soldier points are intentionally NOT drawn — on a phone
    // board they read as leftover frames around empty points
    // once the pieces move away (user feedback, 1.1.0).
}

private fun DrawScope.drawRiverText(
    measurer: TextMeasurer,
    cell: Float,
    marginUnits: Float
) {
    val style = TextStyle(
        fontFamily = PieceFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = (cell * 0.62f).toSp(),
        color = GridInk.copy(alpha = 0.85f)
    )
    val yCenter = (marginUnits + 4.5f) * cell
    for ((text, colCenter) in listOf("楚河" to 2f, "汉界" to 6f)) {
        val layout = measurer.measure(text, style)
        drawText(
            textLayoutResult = layout,
            topLeft = Offset(
                (marginUnits + colCenter) * cell - layout.size.width / 2f,
                yCenter - layout.size.height / 2f
            )
        )
    }
}

/** Origin marker for the last move: one small white glowing
 *  dot where the piece stood. Nothing else — no frame. */
private fun DrawScope.drawGlowDot(center: Offset, cell: Float) {
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.85f),
                Color.White.copy(alpha = 0.35f),
                Color.Transparent
            ),
            center = center,
            radius = cell * 0.20f
        ),
        radius = cell * 0.20f,
        center = center
    )
    drawCircle(
        color = Color.White.copy(alpha = 0.95f),
        radius = cell * 0.055f,
        center = center
    )
}

/** Soft white halo under the destination piece (drawn before
 *  the pieces); its bright rim is drawn separately, above the
 *  piece, by [drawGlowRing]. */
private fun DrawScope.drawGlowHalo(center: Offset, cell: Float) {
    val r = cell * 0.44f
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.50f),
                Color.White.copy(alpha = 0.18f),
                Color.Transparent
            ),
            center = center,
            radius = r * 1.45f
        ),
        radius = r * 1.45f,
        center = center
    )
}

/** Destination marker: a clean white ring of light hugging the
 *  piece that just moved. */
private fun DrawScope.drawGlowRing(center: Offset, cell: Float) {
    val r = cell * 0.44f
    drawCircle(
        color = Color.White.copy(alpha = 0.95f),
        radius = r * 1.10f,
        center = center,
        style = Stroke(width = cell * 0.045f)
    )
    drawCircle(
        color = Color.White.copy(alpha = 0.35f),
        radius = r * 1.22f,
        center = center,
        style = Stroke(width = cell * 0.06f)
    )
}

private fun DrawScope.drawPiece(
    measurer: TextMeasurer,
    center: Offset,
    r: Float,
    piece: Piece,
    selected: Boolean,
    liftShadow: Float = 0f
) {
    val ink = if (piece.side == Side.RED) RedInk else BlackInk
    // Ground shadow: an ellipse squashed toward the light, growing
    // softer and larger while the piece is lifted mid-move.
    val shadowOff = Offset(r * 0.10f, r * (0.16f + 0.22f * liftShadow))
    drawOval(
        color = Color.Black.copy(alpha = 0.26f - 0.08f * liftShadow),
        topLeft = center + shadowOff - Offset(r * 1.02f, r * 0.94f),
        size = androidx.compose.ui.geometry.Size(r * 2.04f, r * 1.88f)
    )
    // Side wall: the extruded rim of the disc, visible below the
    // top face — this is what gives the piece real thickness.
    drawCircle(
        color = Color(0xFFA87F45),
        radius = r,
        center = center + Offset(0f, r * 0.13f)
    )
    drawCircle(
        color = Color(0xFF8A6530),
        radius = r,
        center = center + Offset(0f, r * 0.13f),
        style = Stroke(width = r * 0.05f)
    )
    // Wooden top face with a lit top-left.
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color(0xFFFCF2DA),
                Color(0xFFF0DBAC),
                Color(0xFFDDBB7D)
            ),
            center = center + Offset(-r * 0.35f, -r * 0.40f),
            radius = r * 1.55f
        ),
        radius = r * 0.97f,
        center = center
    )
    // Bevel light: a bright arc on the lit side, a shade arc on
    // the opposite side, so the rim reads as a chamfered edge.
    drawArc(
        color = Color.White.copy(alpha = 0.50f),
        startAngle = 150f,
        sweepAngle = 115f,
        useCenter = false,
        topLeft = center - Offset(r * 0.90f, r * 0.90f),
        size = androidx.compose.ui.geometry.Size(r * 1.80f, r * 1.80f),
        style = Stroke(width = r * 0.07f)
    )
    drawArc(
        color = Color(0xFF6E4E20).copy(alpha = 0.30f),
        startAngle = -35f,
        sweepAngle = 115f,
        useCenter = false,
        topLeft = center - Offset(r * 0.90f, r * 0.90f),
        size = androidx.compose.ui.geometry.Size(r * 1.80f, r * 1.80f),
        style = Stroke(width = r * 0.07f)
    )
    // Engraved double ring.
    drawCircle(
        color = ink,
        radius = r * 0.90f,
        center = center,
        style = Stroke(width = r * 0.085f)
    )
    drawCircle(
        color = ink.copy(alpha = 0.75f),
        radius = r * 0.70f,
        center = center,
        style = Stroke(width = r * 0.042f)
    )
    if (selected) {
        drawCircle(
            color = RedInk.copy(alpha = 0.30f),
            radius = r + r * 0.30f,
            center = center,
            style = Stroke(width = r * 0.16f)
        )
        drawCircle(
            color = RedInk,
            radius = r + r * 0.13f,
            center = center,
            style = Stroke(width = r * 0.07f)
        )
    }
    // Glyph in brush calligraphy, carved: a light offset copy
    // under the ink gives the character a chiselled relief.
    val glyph = piece.type.label(piece.side)
    val embossStyle = TextStyle(
        fontFamily = PieceFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = (r * 1.04f).toSp(),
        color = Color.White.copy(alpha = 0.38f)
    )
    val emboss = measurer.measure(glyph, embossStyle)
    drawText(
        textLayoutResult = emboss,
        topLeft = Offset(
            center.x - emboss.size.width / 2f,
            center.y - emboss.size.height / 2f + r * 0.05f
        )
    )
    val style = TextStyle(
        fontFamily = PieceFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = (r * 1.04f).toSp(),
        color = ink
    )
    val layout = measurer.measure(glyph, style)
    drawText(
        textLayoutResult = layout,
        topLeft = Offset(
            center.x - layout.size.width / 2f,
            center.y - layout.size.height / 2f
        )
    )
}
