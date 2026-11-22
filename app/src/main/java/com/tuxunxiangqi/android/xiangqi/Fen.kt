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
