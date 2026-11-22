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

/** Outcome of a finished game: [winner] (null for a draw) plus a
 *  plain reason string ("将死" checkmate / "困毙" stalemate — in
 *  xiangqi the side that cannot move loses either way). */
data class GameResult(val winner: Side?, val reason: String)

/**
 * Movement rules. [isPseudoLegal] covers piece geometry, blockers,
 * palace and river limits, and the flying-generals rule.
 * [isLegal] additionally rejects moves that capture a general and
 * moves that leave the own general in check （送将）, which is also
 * what makes the generals-facing position unreachable in real play.
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

    fun generalPos(cells: List<Piece?>, side: Side): Pos? {
        for (r in 0..9) {
            for (c in 0..8) {
                val p = at(cells, Pos(c, r))
                if (p != null && p.type == PieceType.GENERAL && p.side == side) {
                    return Pos(c, r)
                }
            }
        }
        return null
    }

    /** True when [side]'s general is attacked by any opposing piece
     *  (the flying-generals rule counts as an attack along the file). */
    fun isInCheck(cells: List<Piece?>, side: Side): Boolean {
        val general = generalPos(cells, side) ?: return false
        for (r in 0..9) {
            for (c in 0..8) {
                val from = Pos(c, r)
                val p = at(cells, from) ?: continue
                if (p.side == side) continue
                if (isPseudoLegal(cells, from, general)) return true
            }
        }
        return false
    }

    /** Fully legal: pseudo-legal, does not capture a general, and
     *  does not leave the own general in check after the move. */
    fun isLegal(cells: List<Piece?>, from: Pos, to: Pos): Boolean {
        if (!isPseudoLegal(cells, from, to)) return false
        val piece = at(cells, from) ?: return false
        val target = at(cells, to)
        if (target != null && target.type == PieceType.GENERAL) return false
        val copy = cells.toMutableList()
        copy[to.row * 9 + to.col] = piece
        copy[from.row * 9 + from.col] = null
        return !isInCheck(copy, piece.side)
    }

    fun legalTargets(cells: List<Piece?>, from: Pos): List<Pos> {
        val out = ArrayList<Pos>()
        for (r in 0..9) {
            for (c in 0..8) {
                val to = Pos(c, r)
                if (isLegal(cells, from, to)) out.add(to)
            }
        }
        return out
    }

    fun allLegalMoves(cells: List<Piece?>, side: Side): List<Move> {
        val out = ArrayList<Move>()
        for (r in 0..9) {
            for (c in 0..8) {
                val from = Pos(c, r)
                val p = at(cells, from) ?: continue
                if (p.side != side) continue
                for (to in legalTargets(cells, from)) {
                    out.add(Move(from, to, at(cells, to)))
                }
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
    /** True when the side to move is currently in check （将军）. */
    var inCheck: Boolean by mutableStateOf(false)
        private set
    /** Non-null once the game has ended (checkmate or stalemate). */
    var result: GameResult? by mutableStateOf(null)
        private set
    val moveCount: Int
        get() = history.size
    private val history = ArrayList<Move>()

    /** Snapshot of the position this game started from (standard
     *  start, a handicap start, or an editor position). */
    var initialCells: List<Piece?> = emptyList()
        private set
    var initialTurn: Side = Side.RED
        private set

    fun historyMoves(): List<Move> = history.toList()

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
        inCheck = false
        result = null
        initialCells = cells.toList()
        initialTurn = Side.RED
    }

    /** Loads an arbitrary position (used by tests and saved games). */
    fun loadPosition(newCells: List<Piece?>, sideToMove: Side) {
        require(newCells.size == 90)
        cells.clear()
        cells.addAll(newCells)
        turn = sideToMove
        lastMove = null
        history.clear()
        initialCells = cells.toList()
        initialTurn = sideToMove
        refreshStatus()
    }

    /** Starts a fresh game from [startCells] (standard start when
     *  null), with [firstTurn] to move. Used for handicap starts and
     *  editor positions. */
    fun newGame(startCells: List<Piece?>?, firstTurn: Side) {
        if (startCells == null) {
            reset()
            if (firstTurn != Side.RED) {
                turn = firstTurn
                initialTurn = firstTurn
                refreshStatus()
            }
        } else {
            loadPosition(startCells, firstTurn)
        }
    }

    /** Ends the game from outside the rules (resignation, agreed
     *  draw, clock timeout). [winner] is null for a draw. */
    fun forceResult(winner: Side?, reason: String) {
        if (result == null) result = GameResult(winner, reason)
    }

    /** Recomputes check / game-over for the side to move. A side with
     *  no legal move loses: by checkmate when in check, else by
     *  stalemate （困毙）, per xiangqi rules. */
    private fun refreshStatus() {
        inCheck = Rules.isInCheck(cells, turn)
        result = if (Rules.allLegalMoves(cells, turn).isEmpty()) {
            GameResult(turn.other(), if (inCheck) "将死" else "困毙")
        } else {
            null
        }
    }

    /** Applies the move when it is fully legal. Returns success. */
    fun tryMove(from: Pos, to: Pos): Boolean {
        if (result != null) return false
        val piece = pieceAt(from) ?: return false
        if (piece.side != turn) return false
        if (!Rules.isLegal(cells, from, to)) return false
        val move = Move(from, to, pieceAt(to))
        set(to, piece)
        set(from, null)
        history.add(move)
        lastMove = move
        turn = turn.other()
        refreshStatus()
        return true
    }

    fun undo(): Boolean {
        val move = history.removeLastOrNull() ?: return false
        val piece = pieceAt(move.to) ?: return false
        set(move.from, piece)
        set(move.to, move.captured)
        turn = turn.other()
        lastMove = history.lastOrNull()
        result = null
        inCheck = Rules.isInCheck(cells, turn)
        return true
    }
}
