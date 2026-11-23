package com.tuxunxiangqi.android.xiangqi

import kotlin.math.exp

/** Score conversions shared by the analysis panel and the trend
 *  chart. Engine scores arrive from the side-to-move point of view;
 *  everything user-facing is converted to Red's point of view. */
object EvalUtil {

    /** Win rate in percent for the side the score favours, using
     *  the logistic curve popularised by Lichess for Stockfish-family
     *  engines. [cp] is from the point of view of the side the
     *  caller cares about. */
    fun winRatePercent(cp: Int?, mateIn: Int?): Double {
        if (mateIn != null) return if (mateIn > 0) 100.0 else 0.0
        val c = cp ?: 0
        return 50.0 + 50.0 * (2.0 / (1.0 + exp(-0.00368208 * c)) - 1.0)
    }

    /** Converts a side-to-move score into Red's point of view. */
    fun toRedPerspective(cp: Int?, mateIn: Int?, sideToMove: Side): Pair<Int?, Int?> {
        val sign = if (sideToMove == Side.RED) 1 else -1
        return (cp?.times(sign)) to (mateIn?.times(sign))
    }

    /** Score loss threshold (centipawns, mover's perspective)
     *  for calling a move questionable. */
    const val QUESTIONABLE_CP = 120

    /** Score loss threshold for calling a move a blunder. */
    const val BLUNDER_CP = 250

    /**
     * How much score move [moveNo] (1-based) gave away, from the
     * mover's point of view, in centipawns; positive means the
     * move lost ground. [evals] are Red-perspective scores keyed
     * by move count (position index: evals[k] is the position
     * after k moves) and [initialTurn] is who moves at position
     * 0. Null when either side of the move was never measured.
     */
    fun moveLossCp(evals: Map<Int, Int>, initialTurn: Side, moveNo: Int): Int? {
        val before = evals[moveNo - 1] ?: return null
        val after = evals[moveNo] ?: return null
        val moverIsRed = ((moveNo - 1) % 2 == 0) == (initialTurn == Side.RED)
        val sign = if (moverIsRed) 1 else -1
        return (before - after) * sign
    }

    /**
     * Marks the moves that gave away a lot of score: "?" for a
     * questionable move, "??" for an outright blunder, keyed by
     * 1-based move number. Moves without both neighbouring
     * measurements are left unmarked.
     */
    fun classifyMoves(
        evals: Map<Int, Int>,
        initialTurn: Side,
        moveCount: Int
    ): Map<Int, String> {
        val out = HashMap<Int, String>()
        for (k in 1..moveCount) {
            val loss = moveLossCp(evals, initialTurn, k) ?: continue
            when {
                loss >= BLUNDER_CP -> out[k] = "??"
                loss >= QUESTIONABLE_CP -> out[k] = "?"
            }
        }
        return out
    }

    /** 1-based rank of [uci] in a candidate list given as UCI
     *  moves in rank order, or null when the move is not there. */
    fun rankOfMove(candidateUcis: List<String>, uci: String): Int? {
        val i = candidateUcis.indexOf(uci)
        return if (i >= 0) i + 1 else null
    }

    /** Flattens a score to one number for comparisons: mate
     *  scores become ±1500, the same cap the trend chart uses. */
    fun flatScore(cp: Int?, mateIn: Int?): Int? {
        if (cp != null) return cp
        if (mateIn != null) return if (mateIn > 0) 1500 else -1500
        return null
    }

    /**
     * How much a candidate move gives away against the engine's
     * best line at the same position, in centipawns from the
     * mover's point of view (both scores are side-to-move scores
     * of that position). Positive means worse than the best move;
     * null when either score is missing.
     */
    fun lossVsBestCp(
        bestCp: Int?,
        bestMate: Int?,
        moveCp: Int?,
        moveMate: Int?
    ): Int? {
        val best = flatScore(bestCp, bestMate) ?: return null
        val move = flatScore(moveCp, moveMate) ?: return null
        return best - move
    }

    /** Plain-language summary of a Red-perspective score. */
    fun describeRed(cp: Int?, mateIn: Int?): String {
        if (mateIn != null) {
            return if (mateIn > 0) "红方有将杀机会，约 $mateIn 步内见分晓"
            else "黑方有将杀机会，约 ${-mateIn} 步内见分晓"
        }
        val c = cp ?: 0
        val pawns = String.format("%.1f", kotlin.math.abs(c) / 100.0)
        return when {
            c >= 300 -> "红方优势很大，约合 $pawns 个兵"
            c >= 80 -> "红方占优，约合 $pawns 个兵"
            c >= 25 -> "红方稍好"
            c <= -300 -> "黑方优势很大，约合 $pawns 个兵"
            c <= -80 -> "黑方占优，约合 $pawns 个兵"
            c <= -25 -> "黑方稍好"
            else -> "局势基本均势"
        }
    }
}
