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
