package com.tuxunxiangqi.android.xiangqi

/**
 * Xiangqi FEN and UCI coordinate conversion.
 *
 * FEN ranks run from Black's back rank (our row 0) to Red's back
 * rank (row 9). Letters: K general, A advisor, B elephant, N horse,
 * R chariot, C cannon, P soldier; uppercase is Red, lowercase Black.
 *
 * UCI moves use files a-i for columns 0-8 and ranks 0-9 counted from
 * Red's side, so rank = 9 - row (the smoke-tested "b2e2" is the red
 * cannon moving 炮二平五 from (1,7) to (4,7)).
 */
object Fen {

    private fun letter(type: PieceType): Char = when (type) {
        PieceType.GENERAL -> 'K'
        PieceType.ADVISOR -> 'A'
        PieceType.ELEPHANT -> 'B'
        PieceType.HORSE -> 'N'
        PieceType.CHARIOT -> 'R'
        PieceType.CANNON -> 'C'
        PieceType.SOLDIER -> 'P'
    }

    fun toFen(state: GameState): String {
        val sb = StringBuilder()
        for (r in 0..9) {
            var empty = 0
            for (c in 0..8) {
                val piece = state.pieceAt(Pos(c, r))
                if (piece == null) {
                    empty++
                } else {
                    if (empty > 0) {
                        sb.append(empty)
                        empty = 0
                    }
                    val ch = letter(piece.type)
                    sb.append(if (piece.side == Side.RED) ch else ch.lowercaseChar())
                }
            }
            if (empty > 0) sb.append(empty)
            if (r < 9) sb.append('/')
        }
        sb.append(if (state.turn == Side.RED) " w" else " b")
        sb.append(" - - 0 ")
        sb.append(state.moveCount / 2 + 1)
        return sb.toString()
    }

    private fun typeOf(ch: Char): PieceType? = when (ch.uppercaseChar()) {
        'K' -> PieceType.GENERAL
        'A' -> PieceType.ADVISOR
        'B' -> PieceType.ELEPHANT
        'N' -> PieceType.HORSE
        'R' -> PieceType.CHARIOT
        'C' -> PieceType.CANNON
        'P' -> PieceType.SOLDIER
        else -> null
    }

    /** Parses a FEN string back into cells + side to move, or null
     *  when the board part is malformed. */
    fun fromFen(fen: String): Pair<List<Piece?>, Side>? {
        val fields = fen.trim().split(Regex("\\s+"))
        if (fields.isEmpty()) return null
        val rows = fields[0].split('/')
        if (rows.size != 10) return null
        val cells = MutableList<Piece?>(90) { null }
        for (r in 0..9) {
            var c = 0
            for (ch in rows[r]) {
                when {
                    ch.isDigit() -> c += ch - '0'
                    else -> {
                        val type = typeOf(ch) ?: return null
                        if (c !in 0..8) return null
                        val side = if (ch.isUpperCase()) Side.RED else Side.BLACK
                        cells[r * 9 + c] = Piece(type, side)
                        c++
                    }
                }
            }
            if (c != 9) return null
        }
        val side = if (fields.getOrNull(1) == "b") Side.BLACK else Side.RED
        return cells to side
    }

    /** FEN of an arbitrary cell layout (records, editor starts). */
    fun toFen(cells: List<Piece?>, turn: Side, fullmove: Int = 1): String {
        val sb = StringBuilder()
        for (r in 0..9) {
            var empty = 0
            for (c in 0..8) {
                val piece = cells[r * 9 + c]
                if (piece == null) {
                    empty++
                } else {
                    if (empty > 0) {
                        sb.append(empty)
                        empty = 0
                    }
                    val ch = letter(piece.type)
                    sb.append(if (piece.side == Side.RED) ch else ch.lowercaseChar())
                }
            }
            if (empty > 0) sb.append(empty)
            if (r < 9) sb.append('/')
        }
        sb.append(if (turn == Side.RED) " w" else " b")
        sb.append(" - - 0 ")
        sb.append(fullmove.coerceAtLeast(1))
        return sb.toString()
    }

    fun toUci(from: Pos, to: Pos): String {
        fun sq(p: Pos) = "${'a' + p.col}${9 - p.row}"
        return sq(from) + sq(to)
    }

    fun parseUciMove(uci: String): Pair<Pos, Pos>? {
        if (uci.length < 4) return null
        fun sq(file: Char, rank: Char): Pos? {
            val col = file - 'a'
            val row = 9 - (rank - '0')
            val pos = Pos(col, row)
            return if (pos.inside()) pos else null
        }
        val from = sq(uci[0], uci[1]) ?: return null
        val to = sq(uci[2], uci[3]) ?: return null
        return from to to
    }
}
