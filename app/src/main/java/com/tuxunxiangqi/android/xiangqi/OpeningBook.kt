package com.tuxunxiangqi.android.xiangqi

/** A small built-in opening book, keyed by the exact UCI move
 *  history from the standard start (space-separated). It covers
 *  the most common first moves and replies; anything deeper is
 *  left to the engine. Entries are (UCI move, Chinese name). */
object OpeningBook {

    private val book: Map<String, List<Pair<String, String>>> = mapOf(
        "" to listOf(
            "h2e2" to "炮二平五（中炮）",
            "b2e2" to "炮八平五（中炮）",
            "g3g4" to "兵七进一（仙人指路）",
            "c0e2" to "相三进五（飞相局）",
            "h0g2" to "马二进三"
        ),
        "h2e2" to listOf(
            "h9g7" to "马8进7",
            "b9c7" to "马2进3",
            "b7e7" to "炮8平5（顺炮）",
            "c6c5" to "卒3进1"
        ),
        "b2e2" to listOf(
            "b9c7" to "马2进3",
            "h9g7" to "马8进7",
            "h7e7" to "炮2平5（顺炮）"
        ),
        "h2e2 h9g7" to listOf(
            "h0g2" to "马二进三",
            "b2b4" to "炮八进二",
            "a3a4" to "兵九进一"
        ),
        "h2e2 b9c7" to listOf(
            "h0g2" to "马二进三",
            "b2b4" to "炮八进二"
        ),
        "h2e2 h9g7 h0g2" to listOf(
            "a9a8" to "车9平8",
            "b7e7" to "炮8平5",
            "i9i8" to "车1平2"
        ),
        "g3g4" to listOf(
            "c6c5" to "卒3进1（对兵局）",
            "b7d7" to "炮2平3",
            "c9e7" to "象3进5"
        ),
        "g3g4 c6c5" to listOf(
            "g4g5" to "兵七进一（兵底炮的伏笔）",
            "h2e2" to "炮二平五",
            "h0g2" to "马二进三"
        ),
        "c0e2" to listOf(
            "c9e7" to "象3进5",
            "b9c7" to "马2进3",
            "g6g5" to "兵7进1"
        ),
        "b2e2 b9c7" to listOf(
            "h0g2" to "马二进三",
            "a3a4" to "兵九进一"
        ),
        "b2e2 h9g7" to listOf(
            "h0g2" to "马二进三",
            "g3g4" to "兵七进一"
        ),
        "h2e2 b7e7" to listOf(
            "h0g2" to "马二进三",
            "b0c2" to "马八进七",
            "b2b4" to "炮八进二"
        ),
        "h2e2 h9g7 h0g2 a9a8" to listOf(
            "b0c2" to "马八进七",
            "a3a4" to "兵九进一",
            "i0h0" to "车一平二"
        ),
        "h2e2 h9g7 h0g2 b7e7" to listOf(
            "a0a2" to "车九进二",
            "g3g4" to "兵七进一"
        ),
        "g3g4 c6c5 g4g5" to listOf(
            "h9g7" to "马8进7",
            "b7e7" to "炮8平5"
        ),
        "c0e2 c9e7" to listOf(
            "b2b4" to "炮八进二",
            "h0g2" to "马二进三",
            "g3g4" to "兵七进一"
        ),
        "h2e2 c6c5" to listOf(
            "h0g2" to "马二进三",
            "b2b4" to "炮八进二"
        ),
        "g3g4 b7d7" to listOf(
            "h0g2" to "马二进三",
            "h2e2" to "炮二平五"
        )
    )

    fun knownHistories(): Set<String> = book.keys

    /** Suggestions for the position reached after [historyUci]
     *  from the standard start; empty when out of book. */
    fun lookup(historyUci: List<String>): List<Pair<String, String>> =
        book[historyUci.joinToString(" ")] ?: emptyList()
}
