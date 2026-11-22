package com.tuxunxiangqi.android.xiangqi

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlin.math.abs

/** Board coordinates: col 0..8 left to right, row 0..9 top to bottom.
 *  Row 0 is Black's back rank, row 9 is Red's back rank (Red sits at
 *  the bottom, as on a physical board seen from Red's side). */
data class Pos(val col: Int, val row: Int) {
    fun inside(): Boolean = col in 0..8 && row in 0..9
}

enum class Side {
    RED, BLACK;

    fun other(): Side = if (this == RED) BLACK else RED
}

enum class PieceType(
    private val redLabel: String,
    private val blackLabel: String
) {
    GENERAL("帥", "將"),
    ADVISOR("仕", "士"),
    ELEPHANT("相", "象"),
    HORSE("傌", "馬"),
    CHARIOT("俥", "車"),
    CANNON("炮", "炮"),
    SOLDIER("兵", "卒");

    fun label(side: Side): String = if (side == Side.RED) redLabel else blackLabel
}

data class Piece(val type: PieceType, val side: Side)

data class Move(val from: Pos, val to: Pos, val captured: Piece?)

/**
 * Basic movement rules ("pseudo-legal"): piece geometry, blockers,
 * palace and river limits, and the flying-generals rule. What is
 * deliberately NOT here yet (next milestone, behind [Rules]):
 * check / checkmate / stalemate detection and rejecting moves that
 * leave the own general in check.
 */
object Rules {

    private fun at(cells: List<Piece?>, p: Pos): Piece? =
        if (p.inside()) cells[p.row * 9 + p.col] else null

    private fun inPalace(side: Side, p: Pos): Boolean {
        if (p.col !in 3..5) return false
        return if (side == Side.RED) p.row in 7..9 else p.row in 0..2
    }

    private fun ownHalf(side: Side, p: Pos): Boolean =
        if (side == Side.RED) p.row in 5..9 else p.row in 0..4

    private fun crossedRiver(side: Side, p: Pos): Boolean =
        if (side == Side.RED) p.row <= 4 else p.row >= 5

    /** Pieces strictly between two squares on the same rank/file. */
    private fun countBetween(cells: List<Piece?>, from: Pos, to: Pos): Int {
        var n = 0
        if (from.col == to.col) {
            val step = if (to.row > from.row) 1 else -1
            var r = from.row + step
            while (r != to.row) {
                if (at(cells, Pos(from.col, r)) != null) n++
                r += step
            }
        } else if (from.row == to.row) {
            val step = if (to.col > from.col) 1 else -1
            var c = from.col + step
            while (c != to.col) {
                if (at(cells, Pos(c, from.row)) != null) n++
                c += step
            }
        }
        return n
    }

    fun isPseudoLegal(cells: List<Piece?>, from: Pos, to: Pos): Boolean {
        if (!from.inside() || !to.inside() || from == to) return false
        val piece = at(cells, from) ?: return false
        val target = at(cells, to)
        if (target != null && target.side == piece.side) return false

        val dc = to.col - from.col
        val dr = to.row - from.row
        return when (piece.type) {
            PieceType.GENERAL -> {
                // Flying generals: a general may "capture" the opposing
                // general along a clear file.
                if (target != null && target.type == PieceType.GENERAL &&
                    from.col == to.col &&
                    countBetween(cells, from, to) == 0
                ) {
                    true
                } else {
                    abs(dc) + abs(dr) == 1 && inPalace(piece.side, to)
                }
            }

            PieceType.ADVISOR ->
                abs(dc) == 1 && abs(dr) == 1 && inPalace(piece.side, to)

            PieceType.ELEPHANT -> {
                if (abs(dc) != 2 || abs(dr) != 2) false
                else if (!ownHalf(piece.side, to)) false
                else at(cells, Pos(from.col + dc / 2, from.row + dr / 2)) == null
            }

            PieceType.HORSE -> {
                when {
                    abs(dc) == 1 && abs(dr) == 2 ->
                        at(cells, Pos(from.col, from.row + dr / 2)) == null
                    abs(dc) == 2 && abs(dr) == 1 ->
                        at(cells, Pos(from.col + dc / 2, from.row)) == null
                    else -> false
                }
            }

            PieceType.CHARIOT ->
                (dc == 0 || dr == 0) && countBetween(cells, from, to) == 0

            PieceType.CANNON -> {
                if (dc != 0 && dr != 0) false
                else {
                    val between = countBetween(cells, from, to)
                    if (target == null) between == 0 else between == 1
                }
            }

            PieceType.SOLDIER -> {
                val forward = if (piece.side == Side.RED) -1 else 1
                when {
                    dc == 0 && dr == forward -> true
                    crossedRiver(piece.side, from) && dr == 0 && abs(dc) == 1 -> true
                    else -> false
                }
            }
        }
    }

    fun legalTargets(cells: List<Piece?>, from: Pos): List<Pos> {
        val out = ArrayList<Pos>()
        for (r in 0..9) {
            for (c in 0..8) {
                val to = Pos(c, r)
                if (isPseudoLegal(cells, from, to)) out.add(to)
            }
        }
        return out
    }
}

/** Mutable game state for the prototype board screen. */
class GameState {
    val cells = mutableStateListOf<Piece?>()
    var turn: Side by mutableStateOf(Side.RED)
        private set
    var lastMove: Move? by mutableStateOf(null)
        private set
    val moveCount: Int
        get() = history.size
    private val history = ArrayList<Move>()

    init {
        reset()
    }

    fun pieceAt(p: Pos): Piece? = if (p.inside()) cells[p.row * 9 + p.col] else null

    private fun set(p: Pos, piece: Piece?) {
        cells[p.row * 9 + p.col] = piece
    }

    fun reset() {
        cells.clear()
        repeat(90) { cells.add(null) }
        val backRank = listOf(
            PieceType.CHARIOT, PieceType.HORSE, PieceType.ELEPHANT,
            PieceType.ADVISOR, PieceType.GENERAL, PieceType.ADVISOR,
            PieceType.ELEPHANT, PieceType.HORSE, PieceType.CHARIOT
        )
        for (c in 0..8) {
            set(Pos(c, 0), Piece(backRank[c], Side.BLACK))
            set(Pos(c, 9), Piece(backRank[c], Side.RED))
        }
        set(Pos(1, 2), Piece(PieceType.CANNON, Side.BLACK))
        set(Pos(7, 2), Piece(PieceType.CANNON, Side.BLACK))
        set(Pos(1, 7), Piece(PieceType.CANNON, Side.RED))
        set(Pos(7, 7), Piece(PieceType.CANNON, Side.RED))
        for (c in listOf(0, 2, 4, 6, 8)) {
            set(Pos(c, 3), Piece(PieceType.SOLDIER, Side.BLACK))
            set(Pos(c, 6), Piece(PieceType.SOLDIER, Side.RED))
        }
        turn = Side.RED
        lastMove = null
        history.clear()
    }

    /** Applies the move when it passes the basic rules. Returns success. */
    fun tryMove(from: Pos, to: Pos): Boolean {
        val piece = pieceAt(from) ?: return false
        if (piece.side != turn) return false
        if (!Rules.isPseudoLegal(cells, from, to)) return false
        val move = Move(from, to, pieceAt(to))
        set(to, piece)
        set(from, null)
        history.add(move)
        lastMove = move
        turn = turn.other()
        return true
    }

    fun undo(): Boolean {
        val move = history.removeLastOrNull() ?: return false
        val piece = pieceAt(move.to) ?: return false
        set(move.from, piece)
        set(move.to, move.captured)
        turn = turn.other()
        lastMove = history.lastOrNull()
        return true
    }
}
