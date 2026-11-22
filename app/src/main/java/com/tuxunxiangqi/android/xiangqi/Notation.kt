package com.tuxunxiangqi.android.xiangqi

/**
 * Chinese move notation （中文纵线记谱）, e.g. 炮二平五 / 马8进7 /
 * 前车进一. Red files are counted from Red's right hand with
 * Chinese numerals, Black files from Black's right with Arabic
 * numerals — the standard convention.
 *
 * The notation of a move depends on the position before the move,
 * so every entry point takes the cell list as it was.
 */
object Notation {

    private val cn = listOf("", "一", "二", "三", "四", "五", "六", "七", "八", "九")

    /** File number 1..9 of [col]. On the printed diagram both
     *  sides count from their own right hand, which works out to
     *  9 - col for both (check the standard opening: 红炮二 is the
     *  cannon on col 7, 黑马8 the horse on col 1, 黑车9 the chariot
     *  on col 0). Only the numeral style differs by side. */
    private fun fileOf(side: Side, col: Int): Int = 9 - col

    private fun fileText(side: Side, col: Int): String {
        val f = fileOf(side, col)
        return if (side == Side.RED) cn[f] else f.toString()
    }

    private fun stepText(side: Side, n: Int): String =
        if (side == Side.RED) cn[n] else n.toString()

    fun moveText(cells: List<Piece?>, from: Pos, to: Pos): String {
        val piece = cells.getOrNull(from.row * 9 + from.col) ?: return "?"
        val side = piece.side
        val sb = StringBuilder()
        sb.append(piece.type.label(side))

        // Disambiguation: another piece of the same type and side on
        // the same file means we say 前/后 (front/back) instead of
        // the file number. Front is the more advanced piece.
        val sameFile = ArrayList<Pos>()
        for (r in 0..9) {
            val p = cells.getOrNull(r * 9 + from.col)
            if (p != null && p.type == piece.type && p.side == side) {
                // Only pieces that share this exact file column count;
                // for soldiers several can share, handled below.
                if (r != from.row) sameFile.add(Pos(from.col, r))
            }
        }
        // sameFile currently holds every same-type piece in the same
        // column except the mover itself.
        val columnMates = sameFile.filter { true }
        var prefixUsed = false
        if (columnMates.isNotEmpty()) {
            val all = (columnMates + from).sortedBy { it.row }
            // Front = smallest row for Red (advancing up), largest
            // row for Black (advancing down).
            val ordered = if (side == Side.RED) all else all.reversed()
            val idx = ordered.indexOf(from)
            val tag = when {
                ordered.size == 2 -> if (idx == 0) "前" else "后"
                ordered.size == 3 -> listOf("前", "中", "后")[idx]
                else -> if (idx == 0) "前" else "后"
            }
            sb.insert(0, tag)
            prefixUsed = true
        }
        if (!prefixUsed) {
            // The piece name comes first, then its file.
            val name = sb.toString()
            sb.clear()
            sb.append(name)
            sb.append(fileText(side, from.col))
        }

        val forward = if (side == Side.RED) -1 else 1
        val dr = to.row - from.row
        val diagonal = piece.type == PieceType.HORSE ||
            piece.type == PieceType.ELEPHANT ||
            piece.type == PieceType.ADVISOR
        when {
            to.row == from.row -> {
                sb.append("平")
                sb.append(fileText(side, to.col))
            }
            dr * forward > 0 -> {
                sb.append("进")
                sb.append(
                    if (diagonal) fileText(side, to.col)
                    else stepText(side, kotlin.math.abs(dr))
                )
            }
            else -> {
                sb.append("退")
                sb.append(
                    if (diagonal) fileText(side, to.col)
                    else stepText(side, kotlin.math.abs(dr))
                )
            }
        }
        return sb.toString()
    }

    /** Formats a whole principal variation by simulating the moves
     *  on a copy of [startCells]. Stops at the first unreadable
     *  move. */
    fun pvText(startCells: List<Piece?>, uciMoves: List<String>, maxMoves: Int = 6): List<String> {
        val cells = startCells.toMutableList()
        val out = ArrayList<String>()
        for (uci in uciMoves.take(maxMoves)) {
            val mv = com.tuxunxiangqi.android.xiangqi.Fen.parseUciMove(uci) ?: break
            val (from, to) = mv
            val piece = cells.getOrNull(from.row * 9 + from.col) ?: break
            out.add(moveText(cells, from, to))
            cells[to.row * 9 + to.col] = piece
            cells[from.row * 9 + from.col] = null
        }
        return out
    }
}
