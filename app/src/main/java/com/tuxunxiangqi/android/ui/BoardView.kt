package com.tuxunxiangqi.android.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
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

@Composable
fun BoardFrame(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF8A6238),
                        Color(0xFF6F4C2A),
                        Color(0xFF59391E)
                    )
                )
            )
            .border(1.dp, FrameDark, RoundedCornerShape(12.dp))
            .padding(9.dp)
    ) {
        Box(
            modifier = Modifier.border(
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
    modifier: Modifier = Modifier
) {
    val measurer = rememberTextMeasurer()
    // Board geometry in cell units: grid is 8 wide x 9 tall, with a
    // 0.62-cell margin so edge pieces never clip.
    val marginUnits = 0.62f
    val totalW = 8f + marginUnits * 2
    val totalH = 9f + marginUnits * 2
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(totalW / totalH)
            .pointerInput(state) {
                detectTapGestures { offset ->
                    val cell = size.width / totalW
                    val col = ((offset.x / cell) - marginUnits).roundToInt()
                    val row = ((offset.y / cell) - marginUnits).roundToInt()
                    val pos = Pos(col, row)
                    if (pos.inside()) onTap(pos)
                }
            }
    ) {
        val cell = size.width / totalW
        fun point(p: Pos) = Offset(
            (marginUnits + p.col) * cell,
            (marginUnits + p.row) * cell
        )

        drawWoodFace(cell, totalW, totalH)
        drawGrid(cell, marginUnits)
        drawRiverText(measurer, cell, marginUnits)

        // Last-move markers.
        state.lastMove?.let { mv ->
            drawCornerBrackets(point(mv.from), cell * 0.52f, WalnutBrown)
            drawCornerBrackets(point(mv.to), cell * 0.52f, WalnutBrown)
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
                drawCornerBrackets(c, cell * 0.54f, RedInk)
            }
        }
        // Pieces.
        for (r in 0..9) {
            for (c in 0..8) {
                val pos = Pos(c, r)
                val piece = state.pieceAt(pos) ?: continue
                drawPiece(measurer, point(pos), cell * 0.44f, piece, pos == selected)
            }
        }
    }
}

private fun DrawScope.drawWoodFace(cell: Float, totalW: Float, totalH: Float) {
    drawRect(
        brush = Brush.verticalGradient(listOf(FaceLight, FaceMid, FaceDeep)),
        size = size
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
            color = if (dark) Color(0xFF8A5A24).copy(alpha = 0.055f)
            else Color(0xFFFFEFC4).copy(alpha = 0.06f),
            style = Stroke(width = cell * (0.03f + rnd.nextFloat() * 0.05f))
        )
    }
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
    // Position markers at cannon and soldier points.
    val marks = listOf(
        Pos(1, 2), Pos(7, 2), Pos(1, 7), Pos(7, 7),
        Pos(0, 3), Pos(2, 3), Pos(4, 3), Pos(6, 3), Pos(8, 3),
        Pos(0, 6), Pos(2, 6), Pos(4, 6), Pos(6, 6), Pos(8, 6)
    )
    for (m in marks) drawPositionMarker(pt(m.col, m.row), cell, m.col)
}

private fun DrawScope.drawPositionMarker(center: Offset, cell: Float, col: Int) {
    val g = cell * 0.10f
    val l = cell * 0.17f
    val w = cell * 0.026f
    // Four L corners; the pair facing off the board edge is skipped.
    for (sx in listOf(-1f, 1f)) {
        if (col == 0 && sx < 0) continue
        if (col == 8 && sx > 0) continue
        for (sy in listOf(-1f, 1f)) {
            val cx = center.x + sx * (g + l)
            val cy = center.y + sy * (g + l)
            drawLine(
                GridInk,
                Offset(cx, cy),
                Offset(cx - sx * l, cy),
                strokeWidth = w
            )
            drawLine(
                GridInk,
                Offset(cx, cy),
                Offset(cx, cy - sy * l),
                strokeWidth = w
            )
        }
    }
}

private fun DrawScope.drawRiverText(
    measurer: TextMeasurer,
    cell: Float,
    marginUnits: Float
) {
    val style = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Bold,
        fontSize = (cell * 0.60f).toSp(),
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

private fun DrawScope.drawCornerBrackets(center: Offset, r: Float, color: Color) {
    val arm = r * 0.42f
    val w = r * 0.10f
    for (sx in listOf(-1f, 1f)) {
        for (sy in listOf(-1f, 1f)) {
            val cx = center.x + sx * r
            val cy = center.y + sy * r
            drawLine(color, Offset(cx, cy), Offset(cx - sx * arm, cy), strokeWidth = w)
            drawLine(color, Offset(cx, cy), Offset(cx, cy - sy * arm), strokeWidth = w)
        }
    }
}

private fun DrawScope.drawPiece(
    measurer: TextMeasurer,
    center: Offset,
    r: Float,
    piece: Piece,
    selected: Boolean
) {
    val ink = if (piece.side == Side.RED) RedInk else BlackInk
    // Drop shadow: the disc sits ON the board.
    drawCircle(
        color = Color.Black.copy(alpha = 0.22f),
        radius = r,
        center = center + Offset(r * 0.10f, r * 0.14f)
    )
    // Wooden disc face with a lit top-left.
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color(0xFFFBF0D6),
                Color(0xFFEFD9AC),
                Color(0xFFDCBA7E)
            ),
            center = center + Offset(-r * 0.35f, -r * 0.40f),
            radius = r * 1.55f
        ),
        radius = r,
        center = center
    )
    drawCircle(
        color = ink,
        radius = r - r * 0.045f,
        center = center,
        style = Stroke(width = r * 0.09f)
    )
    drawCircle(
        color = ink.copy(alpha = 0.75f),
        radius = r * 0.74f,
        center = center,
        style = Stroke(width = r * 0.045f)
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
    val style = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Bold,
        fontSize = (r * 1.06f).toSp(),
        color = ink
    )
    val layout = measurer.measure(piece.type.label(piece.side), style)
    drawText(
        textLayoutResult = layout,
        topLeft = Offset(
            center.x - layout.size.width / 2f,
            center.y - layout.size.height / 2f
        )
    )
}
