package com.tuxunxiangqi.android.xiangqi

/** A small built-in opening book, keyed by the exact UCI move
 *  history from the standard start (space-separated). It covers
 *  the most common first moves and replies; anything deeper is
 *  left to the engine. Entries are (UCI move, Chinese name).
 *  Every history, every suggestion and every name is validated
 *  by OpeningBookTest against the rules and the notation
 *  generator — the names must match Notation.moveText exactly,
 *  which is what caught the mirrored pawn/elephant first moves
 *  and the swapped cannon files fixed in 1.3.0. */
object OpeningBook {

    private val book: Map<String, List<Pair<String, String>>> = mapOf(
        "" to listOf(
            "h2e2" to "炮二平五（中炮）",
            "b2e2" to "炮八平五（中炮）",
            "c3c4" to "兵七进一（仙人指路）",
            "g0e2" to "相三进五（飞相局）",
            "h0g2" to "马二进三",
        ),
        "h2e2" to listOf(
            "h9g7" to "马8进7",
            "b9c7" to "马2进3",
            "h7e7" to "炮8平5（顺炮）",
            "c6c5" to "卒3进1",
        ),
        "b2e2" to listOf(
            "b9c7" to "马2进3",
            "h9g7" to "马8进7",
            "b7e7" to "炮2平5（顺炮）",
        ),
        "h2e2 h9g7" to listOf(
            "h0g2" to "马二进三",
            "b2b4" to "炮八进二",
            "a3a4" to "兵九进一",
        ),
        "h2e2 b9c7" to listOf(
            "h0g2" to "马二进三",
            "b2b4" to "炮八进二",
        ),
        "h2e2 h9g7 h0g2" to listOf(
            "a9a8" to "车1进1",
            "b7e7" to "炮2平5",
            "i9i8" to "车9进1",
        ),
        "c3c4" to listOf(
            "c6c5" to "卒3进1（对兵局）",
            "b7c7" to "炮2平3",
            "c9e7" to "象3进5",
        ),
        "c3c4 c6c5" to listOf(
            "c4c5" to "兵七进一",
            "h2e2" to "炮二平五",
            "h0g2" to "马二进三",
        ),
        "g0e2" to listOf(
            "c9e7" to "象3进5",
            "b9c7" to "马2进3",
            "g6g5" to "卒7进1",
        ),
        "b2e2 b9c7" to listOf(
            "h0g2" to "马二进三",
            "a3a4" to "兵九进一",
        ),
        "b2e2 h9g7" to listOf(
            "h0g2" to "马二进三",
            "c3c4" to "兵七进一",
        ),
        "h2e2 b7e7" to listOf(
            "h0g2" to "马二进三",
            "b0c2" to "马八进七",
            "b2b4" to "炮八进二",
        ),
        "h2e2 h9g7 h0g2 a9a8" to listOf(
            "b0c2" to "马八进七",
            "a3a4" to "兵九进一",
            "i0h0" to "车一平二",
        ),
        "h2e2 h9g7 h0g2 b7e7" to listOf(
            "a0a2" to "车九进二",
            "c3c4" to "兵七进一",
        ),
        "c3c4 c6c5 c4c5" to listOf(
            "h9g7" to "马8进7",
            "h7e7" to "炮8平5",
        ),
        "g0e2 c9e7" to listOf(
            "b2b4" to "炮八进二",
            "h0g2" to "马二进三",
            "c3c4" to "兵七进一",
        ),
        "h2e2 c6c5" to listOf(
            "h0g2" to "马二进三",
            "b2b4" to "炮八进二",
        ),
        "c3c4 b7c7" to listOf(
            "h0g2" to "马二进三",
            "h2e2" to "炮二平五",
        ),
        "h2e2 h9g7 h0g2 a9a8 b0c2" to listOf(
            "b9c7" to "马2进3",
            "i9h9" to "车9平8",
            "b7e7" to "炮2平5",
        ),
        "h2e2 b9c7 h0g2" to listOf(
            "h7e7" to "炮8平5（顺炮）",
            "a9a8" to "车1进1",
            "c6c5" to "卒3进1",
        ),
        "h2e2 h7e7 h0g2" to listOf(
            "h9g7" to "马8进7",
            "b9c7" to "马2进3",
            "i9i8" to "车9进1",
        ),
        "b2e2 b9c7 h0g2" to listOf(
            "h7e7" to "炮8平5",
            "a9a8" to "车1进1",
            "c9e7" to "象3进5",
        ),
        "g0e2 c9e7 b2b4" to listOf(
            "b9c7" to "马2进3",
            "g6g5" to "卒7进1",
            "h7g7" to "炮8平7",
        ),
        "c3c4 c6c5 c4c5 h9g7" to listOf(
            "h2e2" to "炮二平五",
            "h0g2" to "马二进三",
            "b2b4" to "炮八进二",
        ),
        "h2e2 h9g7 h0g2 b7e7 a0a2" to listOf(
            "b9c7" to "马2进3",
            "i9i8" to "车9进1",
            "g6g5" to "卒7进1",
        ),
        "h2e2 c6c5 h0g2" to listOf(
            "h9g7" to "马8进7",
            "h7e7" to "炮8平5（顺炮）",
            "a9a8" to "车1进1",
        ),
        "b2e2 h9g7 h0g2" to listOf(
            "b7e7" to "炮2平5（顺炮）",
            "i9i8" to "车9进1",
            "g9e7" to "象7进5",
        ),
        "c3c4 b7c7 h0g2" to listOf(
            "c6c5" to "卒3进1",
            "h9g7" to "马8进7",
            "g9e7" to "象7进5",
        ),
        "g0e2 b9c7" to listOf(
            "h0g2" to "马二进三",
            "h2g2" to "炮二平三",
            "c3c4" to "兵七进一",
        ),
        "h2e2 b9c7 h0g2 h7e7" to listOf(
            "b0c2" to "马八进七",
            "c3c4" to "兵七进一",
            "i0h0" to "车一平二",
        )
    )

    fun knownHistories(): Set<String> = book.keys

    /** Suggestions for the position reached after [historyUci]
     *  from the standard start; empty when out of book. */
    fun lookup(historyUci: List<String>): List<Pair<String, String>> =
        book[historyUci.joinToString(" ")] ?: emptyList()
}
