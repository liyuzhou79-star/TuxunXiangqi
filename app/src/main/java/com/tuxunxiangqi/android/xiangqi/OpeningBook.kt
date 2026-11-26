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
            "b7e7" to "炮2平5（顺炮）",
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
        "h2e2 h7e7" to listOf(
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
        "h2e2 h7e7 h0g2 h9g7" to listOf(
            "b0c2" to "马八进七",
            "c3c4" to "兵七进一",
            "i0h0" to "车一平二",
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
        ),
        "h2e2 b9c7 h0g2 c6c5" to listOf(
            "b0c2" to "马八进七",
            "i0h0" to "车一平二",
            "g3g4" to "兵三进一",
        ),
        "h2e2 b9c7 h0g2 a9a8" to listOf(
            "b0c2" to "马八进七",
            "i0h0" to "车一平二",
            "g3g4" to "兵三进一",
        ),
        "h2e2 h9g7 h0g2 a9a8 b0c2 b9c7" to listOf(
            "i0h0" to "车一平二",
            "b2b4" to "炮八进二",
            "g3g4" to "兵三进一",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2" to listOf(
            "b9c7" to "马2进3",
            "i9i8" to "车9进1",
            "c6c5" to "卒3进1",
        ),
        "c3c4 c6c5 c4c5 h9g7 h2e2" to listOf(
            "g9e7" to "象7进5",
            "b9c7" to "马2进3",
            "g6g5" to "卒7进1",
        ),
        "g0e2 c9e7 b2b4 b9c7" to listOf(
            "h0g2" to "马二进三",
            "c3c4" to "兵七进一",
            "g3g4" to "兵三进一",
        ),
        "h2e2 h9g7 h0g2 a9a8 i0h0" to listOf(
            "b9c7" to "马2进3",
            "b7e7" to "炮2平5",
            "i9h9" to "车9平8",
        ),
        "h2e2 b9c7 h0g2 c6c5 b0c2" to listOf(
            "h9g7" to "马8进7",
            "h7e7" to "炮8平5",
            "a9a8" to "车1进1",
        ),
        "c3c4 c6c5 c4c5 h9g7 h0g2" to listOf(
            "b9c7" to "马2进3",
            "g9e7" to "象7进5",
            "g6g5" to "卒7进1",
        ),
        "g0e2 c9e7 b2b4 b9c7 h0g2" to listOf(
            "h9g7" to "马8进7",
            "c6c5" to "卒3进1",
            "a9a8" to "车1进1",
        ),
        "h2e2 h9g7 h0g2 a9a8 b0c2 b9c7 i0h0" to listOf(
            "i9h9" to "车9平8",
            "g6g5" to "卒7进1",
            "c6c5" to "卒3进1",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7" to listOf(
            "i0h0" to "车一平二",
            "g3g4" to "兵三进一",
            "b2b4" to "炮八进二",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 i0h0" to listOf(
            "i9h9" to "车9平8",
            "g6g5" to "卒7进1",
            "c6c5" to "卒3进1",
        ),
        "h2e2 h9g7 h0g2 a9a8 b0c2 b9c7 i0h0 i9h9" to listOf(
            "b2b4" to "炮八进二",
            "c3c4" to "兵七进一",
            "g3g4" to "兵三进一",
        ),
        "h2e2 b9c7 h0g2 c6c5 b0c2 h9g7" to listOf(
            "i0h0" to "车一平二",
            "c3c4" to "兵七进一",
            "b2b4" to "炮八进二",
        ),
        "c3c4 c6c5 c4c5 h9g7 h2e2 b9c7" to listOf(
            "h0g2" to "马二进三",
            "b0c2" to "马八进七",
            "b2b4" to "炮八进二",
        ),
        "g0e2 c9e7 b2b4 b9c7 h0g2 h9g7" to listOf(
            "i0h0" to "车一平二",
            "c3c4" to "兵七进一",
            "g3g4" to "兵三进一",
        ),
        "h2e2 h9g7 h0g2 a9a8 b0c2 b9c7 b2b4" to listOf(
            "i9h9" to "车9平8",
            "g6g5" to "卒7进1",
            "g9e7" to "象7进5",
        ),
        "h2e2 h9g7 h0g2 a9a8 b0c2 b9c7 i0h0 i9h9 b2b4" to listOf(
            "g9e7" to "象7进5",
            "c6c5" to "卒3进1",
            "g6g5" to "卒7进1",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 i0h0 i9h9" to listOf(
            "b2b4" to "炮八进二",
            "g3g4" to "兵三进一",
            "c3c4" to "兵七进一",
        ),
        "c3c4 c6c5 c4c5 h9g7 h2e2 b9c7 h0g2" to listOf(
            "g9e7" to "象7进5",
            "g6g5" to "卒7进1",
            "a9a8" to "车1进1",
        ),
        "g0e2 c9e7 b2b4 b9c7 h0g2 h9g7 i0h0" to listOf(
            "i9h9" to "车9平8",
            "c6c5" to "卒3进1",
            "g6g5" to "卒7进1",
        ),
        "h2e2 b9c7 h0g2 c6c5 b0c2 h9g7 i0h0" to listOf(
            "i9h9" to "车9平8",
            "a9a8" to "车1进1",
            "g9e7" to "象7进5",
        ),
        "h2e2 h9g7 h0g2 a9a8 b0c2 b9c7 b2b4 i9h9" to listOf(
            "g3g4" to "兵三进一",
            "c3c4" to "兵七进一",
            "i0h0" to "车一平二",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4" to listOf(
            "i9h9" to "车9平8",
            "c6c5" to "卒3进1",
            "a9a8" to "车1进1",
        ),
        "c3c4 c6c5 c4c5 h9g7 h2e2 b9c7 h0g2 g9e7" to listOf(
            "b2b4" to "炮八进二",
            "b0c2" to "马八进七",
            "g3g4" to "兵三进一",
        ),
        "g0e2 c9e7 b2b4 b9c7 h0g2 h9g7 i0h0 i9h9" to listOf(
            "g3g4" to "兵三进一",
            "c3c4" to "兵七进一",
            "a3a4" to "兵九进一",
        ),
        "h2e2 b9c7 h0g2 c6c5 b0c2 h9g7 i0h0 i9h9" to listOf(
            "b2b4" to "炮八进二",
            "g3g4" to "兵三进一",
            "c3c4" to "兵七进一",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 i0h0 i9h9 b2b4" to listOf(
            "a9a8" to "车1进1",
            "c6c5" to "卒3进1",
            "g6g5" to "卒7进1",
        ),
        "h2e2 h9g7 h0g2 a9a8 b0c2 b9c7 i0h0 g6g5" to listOf(
            "b2b4" to "炮八进二",
            "g3g4" to "兵三进一",
            "c3c4" to "兵七进一",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9" to listOf(
            "g3g4" to "兵三进一",
            "c3c4" to "兵七进一",
            "i0h0" to "车一平二",
        ),
        "c3c4 c6c5 c4c5 h9g7 h2e2 b9c7 h0g2 g9e7 b2b4" to listOf(
            "i9h9" to "车9平8",
            "a9a8" to "车1进1",
            "g6g5" to "卒7进1",
        ),
        "g0e2 c9e7 b2b4 b9c7 h0g2 h9g7 i0h0 i9h9 g3g4" to listOf(
            "c6c5" to "卒3进1",
            "g6g5" to "卒7进1",
            "a9a8" to "车1进1",
        ),
        "h2e2 b9c7 h0g2 c6c5 b0c2 h9g7 i0h0 i9h9 c3c4" to listOf(
            "a9a8" to "车1进1",
            "g9e7" to "象7进5",
            "g6g5" to "卒7进1",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 i0h0 i9h9 b2b4 c6c5" to listOf(
            "g3g4" to "兵三进一",
            "c3c4" to "兵七进一",
            "a3a4" to "兵九进一",
        ),
        "h2e2 h9g7 h0g2 a9a8 b0c2 b9c7 i0h0 g6g5 b2b4" to listOf(
            "i9h9" to "车9平8",
            "g9e7" to "象7进5",
            "c6c5" to "卒3进1",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 g3g4" to listOf(
            "c6c5" to "卒3进1",
            "g6g5" to "卒7进1",
            "a9a8" to "车1进1",
        ),
        "c3c4 c6c5 c4c5 h9g7 h2e2 b9c7 h0g2 g9e7 b2b4 i9h9" to listOf(
            "b0c2" to "马八进七",
            "g3g4" to "兵三进一",
            "a3a4" to "兵九进一",
        ),
        "g0e2 c9e7 b2b4 b9c7 h0g2 h9g7 i0h0 i9h9 g3g4 c6c5" to listOf(
            "c3c4" to "兵七进一",
            "a3a4" to "兵九进一",
            "b0c2" to "马八进七",
        ),
        "h2e2 b9c7 h0g2 c6c5 b0c2 h9g7 i0h0 i9h9 c3c4 a9a8" to listOf(
            "b2b4" to "炮八进二",
            "g3g4" to "兵三进一",
            "a3a4" to "兵九进一",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 i0h0 i9h9 b2b4 c6c5 g3g4" to listOf(
            "g6g5" to "卒7进1",
            "a9a8" to "车1进1",
            "a6a5" to "卒1进1",
        ),
        "h2e2 h9g7 h0g2 a9a8 b0c2 b9c7 i0h0 g6g5 b2b4 i9h9" to listOf(
            "g3g4" to "兵三进一",
            "c3c4" to "兵七进一",
            "a3a4" to "兵九进一",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 g3g4 c6c5" to listOf(
            "c3c4" to "兵七进一",
            "a3a4" to "兵九进一",
            "i0h0" to "车一平二",
        ),
        "g0e2 c9e7 b2b4 b9c7 h0g2 h9g7 i0h0 i9h9 g3g4 c6c5 a3a4" to listOf(
            "g6g5" to "卒7进1",
            "a9a8" to "车1进1",
            "h9h8" to "车8进1",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 c3c4" to listOf(
            "c6c5" to "卒3进1",
            "g6g5" to "卒7进1",
            "a9a8" to "车1进1",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 g3g4 c6c5 c3c4" to listOf(
            "g6g5" to "卒7进1",
            "a9a8" to "车1进1",
            "h9h8" to "车8进1",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 c3c4 c6c5" to listOf(
            "g3g4" to "兵三进一",
            "a3a4" to "兵九进一",
            "i0h0" to "车一平二",
        ),
        "g0e2 c9e7 b2b4 b9c7 h0g2 h9g7 i0h0 i9h9 g3g4 c6c5 a3a4 g6g5" to listOf(
            "c3c4" to "兵七进一",
            "b0c2" to "马八进七",
            "g4g5" to "兵三进一",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 g3g4 c6c5 c3c4 g6g5" to listOf(
            "a3a4" to "兵九进一",
            "i0h0" to "车一平二",
            "g4g5" to "兵三进一",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 c3c4 c6c5 g3g4" to listOf(
            "g6g5" to "卒7进1",
            "a9a8" to "车1进1",
            "h9h8" to "车8进1",
        ),
        "g0e2 c9e7 b2b4 b9c7 h0g2 h9g7 i0h0 i9h9 g3g4 c6c5 a3a4 g6g5 c3c4" to listOf(
            "a9a8" to "车1进1",
            "h9h8" to "车8进1",
            "e6e5" to "卒5进1",
        ),
        "h2e2 h9g7 h0g2 a9a8 b0c2 b9c7 i0h0 g6g5 b2b4 i9h9 g3g4" to listOf(
            "c6c5" to "卒3进1",
            "g9e7" to "象7进5",
            "h9h8" to "车8进1",
        ),
        "c3c4 c6c5 c4c5 h9g7 h2e2 b9c7 h0g2 g9e7 b2b4 i9h9 b0c2" to listOf(
            "a9a8" to "车1进1",
            "g6g5" to "卒7进1",
            "h9h8" to "车8进1",
        ),
        "h2e2 b9c7 h0g2 c6c5 b0c2 h9g7 i0h0 i9h9 c3c4 a9a8 b2b4" to listOf(
            "g9e7" to "象7进5",
            "g6g5" to "卒7进1",
            "e6e5" to "卒5进1",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 g3g4 c6c5 c3c4 g6g5 a3a4" to listOf(
            "h9h8" to "车8进1",
            "a9a8" to "车1进1",
            "e6e5" to "卒5进1",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 c3c4 c6c5 g3g4 g6g5" to listOf(
            "a3a4" to "兵九进一",
            "i0h0" to "车一平二",
            "g4g5" to "兵三进一",
        ),
        "g0e2 c9e7 b2b4 b9c7 h0g2 h9g7 i0h0 i9h9 g3g4 c6c5 a3a4 g6g5 c3c4 a9a8" to listOf(
            "b0c2" to "马八进七",
            "g4g5" to "兵三进一",
            "a4a5" to "兵九进一",
        ),
        "h2e2 h9g7 h0g2 a9a8 b0c2 b9c7 i0h0 g6g5 b2b4 i9h9 g3g4 c6c5" to listOf(
            "a3a4" to "兵九进一",
            "g4g5" to "兵三进一",
            "h0h4" to "车二进四",
        ),
        "c3c4 c6c5 c4c5 h9g7 h2e2 b9c7 h0g2 g9e7 b2b4 i9h9 b0c2 a9a8" to listOf(
            "g3g4" to "兵三进一",
            "a3a4" to "兵九进一",
            "i0h0" to "车一平二",
        ),
        "h2e2 b9c7 h0g2 c6c5 b0c2 h9g7 i0h0 i9h9 c3c4 a9a8 b2b4 g9e7" to listOf(
            "g3g4" to "兵三进一",
            "a3a4" to "兵九进一",
            "h0h4" to "车二进四",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 g3g4 c6c5 c3c4 g6g5 a3a4 h9h8" to listOf(
            "i0h0" to "车一平二",
            "g4g5" to "兵三进一",
            "i0i1" to "车一进一",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 c3c4 c6c5 g3g4 g6g5 a3a4" to listOf(
            "h9h8" to "车8进1",
            "a9a8" to "车1进1",
            "e6e5" to "卒5进1",
        ),
        "g0e2 c9e7 b2b4 b9c7 h0g2 h9g7 i0h0 i9h9 g3g4 c6c5 a3a4 g6g5 c3c4 a9a8 b0c2" to listOf(
            "h9h8" to "车8进1",
            "e6e5" to "卒5进1",
            "a6a5" to "卒1进1",
        ),
        "h2e2 h9g7 h0g2 a9a8 b0c2 b9c7 i0h0 g6g5 b2b4 i9h9 g3g4 c6c5 a3a4" to listOf(
            "g9e7" to "象7进5",
            "h9h8" to "车8进1",
            "e6e5" to "卒5进1",
        ),
        "c3c4 c6c5 c4c5 h9g7 h2e2 b9c7 h0g2 g9e7 b2b4 i9h9 b0c2 a9a8 g3g4" to listOf(
            "g6g5" to "卒7进1",
            "h9h8" to "车8进1",
            "e6e5" to "卒5进1",
        ),
        "h2e2 b9c7 h0g2 c6c5 b0c2 h9g7 i0h0 i9h9 c3c4 a9a8 b2b4 g9e7 g3g4" to listOf(
            "g6g5" to "卒7进1",
            "h9h8" to "车8进1",
            "e6e5" to "卒5进1",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 g3g4 c6c5 c3c4 g6g5 a3a4 h9h8 i0h0" to listOf(
            "a9a8" to "车1进1",
            "e6e5" to "卒5进1",
            "a6a5" to "卒1进1",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 c3c4 c6c5 g3g4 g6g5 a3a4 h9h8" to listOf(
            "i0h0" to "车一平二",
            "g4g5" to "兵三进一",
            "i0i1" to "车一进一",
        ),
        "g0e2 c9e7 b2b4 b9c7 h0g2 h9g7 i0h0 i9h9 g3g4 c6c5 a3a4 g6g5 c3c4 a9a8 b0c2 h9h8" to listOf(
            "g4g5" to "兵三进一",
            "a4a5" to "兵九进一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h9g7 h0g2 a9a8 b0c2 b9c7 i0h0 g6g5 b2b4 i9h9 g3g4 c6c5 a3a4 g9e7" to listOf(
            "c3c4" to "兵七进一",
            "g4g5" to "兵三进一",
            "h0h4" to "车二进四",
        ),
        "c3c4 c6c5 c4c5 h9g7 h2e2 b9c7 h0g2 g9e7 b2b4 i9h9 b0c2 a9a8 g3g4 g6g5" to listOf(
            "a3a4" to "兵九进一",
            "i0h0" to "车一平二",
            "g4g5" to "兵三进一",
        ),
        "h2e2 b9c7 h0g2 c6c5 b0c2 h9g7 i0h0 i9h9 c3c4 a9a8 b2b4 g9e7 g3g4 g6g5" to listOf(
            "a3a4" to "兵九进一",
            "g4g5" to "兵三进一",
            "h0h4" to "车二进四",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 g3g4 c6c5 c3c4 g6g5 a3a4 h9h8 i0h0 a9a8" to listOf(
            "g4g5" to "兵三进一",
            "h0h4" to "车二进四",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 c3c4 c6c5 g3g4 g6g5 a3a4 h9h8 i0h0" to listOf(
            "a9a8" to "车1进1",
            "e6e5" to "卒5进1",
            "a6a5" to "卒1进1",
        ),
        "g0e2 c9e7 b2b4 b9c7 h0g2 h9g7 i0h0 i9h9 g3g4 c6c5 a3a4 g6g5 c3c4 a9a8 b0c2 h9h8 g4g5" to listOf(
            "e6e5" to "卒5进1",
            "a6a5" to "卒1进1",
            "a8a7" to "车1进1",
        ),
        "h2e2 h9g7 h0g2 a9a8 b0c2 b9c7 i0h0 g6g5 b2b4 i9h9 g3g4 c6c5 a3a4 g9e7 c3c4" to listOf(
            "h9h8" to "车8进1",
            "e6e5" to "卒5进1",
            "a8a7" to "车1进1",
        ),
        "c3c4 c6c5 c4c5 h9g7 h2e2 b9c7 h0g2 g9e7 b2b4 i9h9 b0c2 a9a8 g3g4 g6g5 a3a4" to listOf(
            "h9h8" to "车8进1",
            "e6e5" to "卒5进1",
            "a8a7" to "车1进1",
        ),
        "h2e2 b9c7 h0g2 c6c5 b0c2 h9g7 i0h0 i9h9 c3c4 a9a8 b2b4 g9e7 g3g4 g6g5 a3a4" to listOf(
            "h9h8" to "车8进1",
            "e6e5" to "卒5进1",
            "a8a7" to "车1进1",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 g3g4 c6c5 c3c4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5" to listOf(
            "e6e5" to "卒5进1",
            "a6a5" to "卒1进1",
            "a8a7" to "车1进1",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 c3c4 c6c5 g3g4 g6g5 a3a4 h9h8 i0h0 a9a8" to listOf(
            "g4g5" to "兵三进一",
            "h0h4" to "车二进四",
            "b4b5" to "炮八进一",
        ),
        "g0e2 c9e7 b2b4 b9c7 h0g2 h9g7 i0h0 i9h9 g3g4 c6c5 a3a4 g6g5 c3c4 a9a8 b0c2 h9h8 a4a5" to listOf(
            "e6e5" to "卒5进1",
            "a6a5" to "卒1进1",
            "a8a7" to "车1进1",
        ),
        "h2e2 h9g7 h0g2 a9a8 b0c2 b9c7 i0h0 g6g5 b2b4 i9h9 g3g4 c6c5 a3a4 g9e7 c3c4 h9h8" to listOf(
            "g4g5" to "兵三进一",
            "a4a5" to "兵九进一",
            "h0h4" to "车二进四",
        ),
        "c3c4 c6c5 c4c5 h9g7 h2e2 b9c7 h0g2 g9e7 b2b4 i9h9 b0c2 a9a8 g3g4 g6g5 a3a4 h9h8" to listOf(
            "i0h0" to "车一平二",
            "g4g5" to "兵三进一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 b9c7 h0g2 c6c5 b0c2 h9g7 i0h0 i9h9 c3c4 a9a8 b2b4 g9e7 g3g4 g6g5 a3a4 h9h8" to listOf(
            "g4g5" to "兵三进一",
            "a4a5" to "兵九进一",
            "h0h4" to "车二进四",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 g3g4 c6c5 c3c4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5" to listOf(
            "g5g6" to "兵三进一",
            "b4b5" to "炮八进一",
            "h0h4" to "车二进四",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 c3c4 c6c5 g3g4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5" to listOf(
            "e6e5" to "卒5进1",
            "a6a5" to "卒1进1",
            "a8a7" to "车1进1",
        ),
        "g0e2 c9e7 b2b4 b9c7 h0g2 h9g7 i0h0 i9h9 g3g4 c6c5 a3a4 g6g5 c3c4 a9a8 b0c2 h9h8 a4a5 e6e5" to listOf(
            "g4g5" to "兵三进一",
            "b4b5" to "炮八进一",
            "a5a6" to "兵九进一",
        ),
        "h2e2 h9g7 h0g2 a9a8 b0c2 b9c7 i0h0 g6g5 b2b4 i9h9 g3g4 c6c5 a3a4 g9e7 c3c4 h9h8 g4g5" to listOf(
            "h8h9" to "车8退1",
            "e6e5" to "卒5进1",
            "a8a7" to "车1进1",
        ),
        "c3c4 c6c5 c4c5 h9g7 h2e2 b9c7 h0g2 g9e7 b2b4 i9h9 b0c2 a9a8 g3g4 g6g5 a3a4 h9h8 i0h0" to listOf(
            "a8a7" to "车1进1",
            "h8h9" to "车8退1",
            "e6e5" to "卒5进1",
        ),
        "h2e2 b9c7 h0g2 c6c5 b0c2 h9g7 i0h0 i9h9 c3c4 a9a8 b2b4 g9e7 g3g4 g6g5 a3a4 h9h8 g4g5" to listOf(
            "h8h9" to "车8退1",
            "e6e5" to "卒5进1",
            "a8a7" to "车1进1",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 g3g4 c6c5 c3c4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6" to listOf(
            "a8a7" to "车1进1",
            "a6a5" to "卒1进1",
            "h8h9" to "车8退1",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 c3c4 c6c5 g3g4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5" to listOf(
            "g5g6" to "兵三进一",
            "b4b5" to "炮八进一",
            "h0h4" to "车二进四",
        ),
        "g0e2 c9e7 b2b4 b9c7 h0g2 h9g7 i0h0 i9h9 g3g4 c6c5 a3a4 g6g5 c3c4 a9a8 b0c2 h9h8 a4a5 e6e5 g4g5" to listOf(
            "a8a7" to "车1进1",
            "a6a5" to "卒1进1",
            "h8h9" to "车8退1",
        ),
        "h2e2 h9g7 h0g2 a9a8 b0c2 b9c7 i0h0 g6g5 b2b4 i9h9 g3g4 c6c5 a3a4 g9e7 c3c4 h9h8 g4g5 h8h9" to listOf(
            "g5g6" to "兵三进一",
            "a4a5" to "兵九进一",
            "h0h4" to "车二进四",
        ),
        "c3c4 c6c5 c4c5 h9g7 h2e2 b9c7 h0g2 g9e7 b2b4 i9h9 b0c2 a9a8 g3g4 g6g5 a3a4 h9h8 i0h0 a8a7" to listOf(
            "g4g5" to "兵三进一",
            "h0h4" to "车二进四",
            "b4b5" to "炮八进一",
        ),
        "h2e2 b9c7 h0g2 c6c5 b0c2 h9g7 i0h0 i9h9 c3c4 a9a8 b2b4 g9e7 g3g4 g6g5 a3a4 h9h8 g4g5 h8h9" to listOf(
            "g5g6" to "兵三进一",
            "a4a5" to "兵九进一",
            "h0h4" to "车二进四",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 g3g4 c6c5 c3c4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a8a7" to listOf(
            "a4a5" to "兵九进一",
            "e3e4" to "兵五进一",
            "a0a1" to "车九进一",
        ),
        "g0e2 c9e7 b2b4 b9c7 h0g2 h9g7 i0h0 i9h9 g3g4 c6c5 a3a4 g6g5 c3c4 a9a8 b0c2 h9h8 a4a5 e6e5 g4g5 a8a7" to listOf(
            "e3e4" to "兵五进一",
            "g5g6" to "兵三进一",
            "a0a1" to "车九进一",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 c3c4 c6c5 g3g4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6" to listOf(
            "a6a5" to "卒1进1",
            "e5e4" to "卒5进1",
            "a8a7" to "车1进1",
        ),
        "h2e2 h9g7 h0g2 a9a8 b0c2 b9c7 i0h0 g6g5 b2b4 i9h9 g3g4 c6c5 a3a4 g9e7 c3c4 h9h8 g4g5 h8h9 g5g6" to listOf(
            "a6a5" to "卒1进1",
            "e6e5" to "卒5进1",
            "a8a7" to "车1进1",
        ),
        "c3c4 c6c5 c4c5 h9g7 h2e2 b9c7 h0g2 g9e7 b2b4 i9h9 b0c2 a9a8 g3g4 g6g5 a3a4 h9h8 i0h0 a8a7 g4g5" to listOf(
            "a6a5" to "卒1进1",
            "e6e5" to "卒5进1",
            "a7a8" to "车1退1",
        ),
        "h2e2 b9c7 h0g2 c6c5 b0c2 h9g7 i0h0 i9h9 c3c4 a9a8 b2b4 g9e7 g3g4 g6g5 a3a4 h9h8 g4g5 h8h9 g5g6" to listOf(
            "a6a5" to "卒1进1",
            "e6e5" to "卒5进1",
            "a8a7" to "车1进1",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 c3c4 c6c5 g3g4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a6a5" to listOf(
            "e3e4" to "兵五进一",
            "i3i4" to "兵一进一",
            "a0a1" to "车九进一",
        ),
        "h2e2 h9g7 h0g2 a9a8 b0c2 b9c7 i0h0 g6g5 b2b4 i9h9 g3g4 c6c5 a3a4 g9e7 c3c4 h9h8 g4g5 h8h9 g5g6 a6a5" to listOf(
            "e3e4" to "兵五进一",
            "i3i4" to "兵一进一",
            "a0a1" to "车九进一",
        ),
        "c3c4 c6c5 c4c5 h9g7 h2e2 b9c7 h0g2 g9e7 b2b4 i9h9 b0c2 a9a8 g3g4 g6g5 a3a4 h9h8 i0h0 a8a7 g4g5 a6a5" to listOf(
            "c5c6" to "兵七进一",
            "e3e4" to "兵五进一",
            "a0a1" to "车九进一",
        ),
        "h2e2 b9c7 h0g2 c6c5 b0c2 h9g7 i0h0 i9h9 c3c4 a9a8 b2b4 g9e7 g3g4 g6g5 a3a4 h9h8 g4g5 h8h9 g5g6 a6a5" to listOf(
            "e3e4" to "兵五进一",
            "i3i4" to "兵一进一",
            "a0a1" to "车九进一",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 g3g4 c6c5 c3c4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a8a7 a4a5" to listOf(
            "e5e4" to "卒5进1",
            "i6i5" to "卒9进1",
            "h8h7" to "车8进1",
        ),
        "g0e2 c9e7 b2b4 b9c7 h0g2 h9g7 i0h0 i9h9 g3g4 c6c5 a3a4 g6g5 c3c4 a9a8 b0c2 h9h8 a4a5 e6e5 g4g5 a8a7 e3e4" to listOf(
            "i6i5" to "卒9进1",
            "a7a8" to "车1退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 c3c4 c6c5 g3g4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a6a5 e3e4" to listOf(
            "i6i5" to "卒9进1",
            "a8a7" to "车1进1",
            "h8h9" to "车8退1",
        ),
        "h2e2 h9g7 h0g2 a9a8 b0c2 b9c7 i0h0 g6g5 b2b4 i9h9 g3g4 c6c5 a3a4 g9e7 c3c4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4" to listOf(
            "e6e5" to "卒5进1",
            "i6i5" to "卒9进1",
            "a8a7" to "车1进1",
        ),
        "c3c4 c6c5 c4c5 h9g7 h2e2 b9c7 h0g2 g9e7 b2b4 i9h9 b0c2 a9a8 g3g4 g6g5 a3a4 h9h8 i0h0 a8a7 g4g5 a6a5 c5c6" to listOf(
            "e6e5" to "卒5进1",
            "i6i5" to "卒9进1",
            "a7a6" to "车1进1",
        ),
        "h2e2 b9c7 h0g2 c6c5 b0c2 h9g7 i0h0 i9h9 c3c4 a9a8 b2b4 g9e7 g3g4 g6g5 a3a4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4" to listOf(
            "e6e5" to "卒5进1",
            "i6i5" to "卒9进1",
            "a8a7" to "车1进1",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 g3g4 c6c5 c3c4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a8a7 a4a5 e5e4" to listOf(
            "i3i4" to "兵一进一",
            "a0a1" to "车九进一",
            "b4b5" to "炮八进一",
        ),
        "g0e2 c9e7 b2b4 b9c7 h0g2 h9g7 i0h0 i9h9 g3g4 c6c5 a3a4 g6g5 c3c4 a9a8 b0c2 h9h8 a4a5 e6e5 g4g5 a8a7 e3e4 i6i5" to listOf(
            "g5g6" to "兵三进一",
            "i3i4" to "兵一进一",
            "a0a1" to "车九进一",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 c3c4 c6c5 g3g4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a6a5 e3e4 i6i5" to listOf(
            "i3i4" to "兵一进一",
            "a0a1" to "车九进一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h9g7 h0g2 a9a8 b0c2 b9c7 i0h0 g6g5 b2b4 i9h9 g3g4 c6c5 a3a4 g9e7 c3c4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5" to listOf(
            "i3i4" to "兵一进一",
            "a0a1" to "车九进一",
            "b4b5" to "炮八进一",
        ),
        "c3c4 c6c5 c4c5 h9g7 h2e2 b9c7 h0g2 g9e7 b2b4 i9h9 b0c2 a9a8 g3g4 g6g5 a3a4 h9h8 i0h0 a8a7 g4g5 a6a5 c5c6 e6e5" to listOf(
            "e3e4" to "兵五进一",
            "g5g6" to "兵三进一",
            "a0a1" to "车九进一",
        ),
        "h2e2 b9c7 h0g2 c6c5 b0c2 h9g7 i0h0 i9h9 c3c4 a9a8 b2b4 g9e7 g3g4 g6g5 a3a4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5" to listOf(
            "i3i4" to "兵一进一",
            "a0a1" to "车九进一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 g3g4 c6c5 c3c4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a8a7 a4a5 e5e4 i3i4" to listOf(
            "i6i5" to "卒9进1",
            "h8h7" to "车8进1",
            "a7a8" to "车1退1",
        ),
        "g0e2 c9e7 b2b4 b9c7 h0g2 h9g7 i0h0 i9h9 g3g4 c6c5 a3a4 g6g5 c3c4 a9a8 b0c2 h9h8 a4a5 e6e5 g4g5 a8a7 e3e4 i6i5 g5g6" to listOf(
            "i5i4" to "卒9进1",
            "a7a8" to "车1退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 c3c4 c6c5 g3g4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a6a5 e3e4 i6i5 i3i4" to listOf(
            "a8a7" to "车1进1",
            "h8h7" to "车8进1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h9g7 h0g2 a9a8 b0c2 b9c7 i0h0 g6g5 b2b4 i9h9 g3g4 c6c5 a3a4 g9e7 c3c4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4" to listOf(
            "i6i5" to "卒9进1",
            "a8a7" to "车1进1",
            "b7b6" to "炮2进1",
        ),
        "c3c4 c6c5 c4c5 h9g7 h2e2 b9c7 h0g2 g9e7 b2b4 i9h9 b0c2 a9a8 g3g4 g6g5 a3a4 h9h8 i0h0 a8a7 g4g5 a6a5 c5c6 e6e5 e3e4" to listOf(
            "i6i5" to "卒9进1",
            "a7a6" to "车1进1",
            "h8h9" to "车8退1",
        ),
        "h2e2 b9c7 h0g2 c6c5 b0c2 h9g7 i0h0 i9h9 c3c4 a9a8 b2b4 g9e7 g3g4 g6g5 a3a4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4" to listOf(
            "i6i5" to "卒9进1",
            "a8a7" to "车1进1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 g3g4 c6c5 c3c4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a8a7 a4a5 e5e4 i3i4 i6i5" to listOf(
            "a0a1" to "车九进一",
            "h0h1" to "车二进一",
            "b4b5" to "炮八进一",
        ),
        "g0e2 c9e7 b2b4 b9c7 h0g2 h9g7 i0h0 i9h9 g3g4 c6c5 a3a4 g6g5 c3c4 a9a8 b0c2 h9h8 a4a5 e6e5 g4g5 a8a7 e3e4 i6i5 g5g6 i5i4" to listOf(
            "a0a1" to "车九进一",
            "h0h1" to "车二进一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 c3c4 c6c5 g3g4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a6a5 e3e4 i6i5 i3i4 a8a7" to listOf(
            "a0a1" to "车九进一",
            "h0h1" to "车二进一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h9g7 h0g2 a9a8 b0c2 b9c7 i0h0 g6g5 b2b4 i9h9 g3g4 c6c5 a3a4 g9e7 c3c4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5" to listOf(
            "a0a1" to "车九进一",
            "h0h1" to "车二进一",
            "b4b5" to "炮八进一",
        ),
        "c3c4 c6c5 c4c5 h9g7 h2e2 b9c7 h0g2 g9e7 b2b4 i9h9 b0c2 a9a8 g3g4 g6g5 a3a4 h9h8 i0h0 a8a7 g4g5 a6a5 c5c6 e6e5 e3e4 i6i5" to listOf(
            "g5g6" to "兵三进一",
            "i3i4" to "兵一进一",
            "a0a1" to "车九进一",
        ),
        "h2e2 b9c7 h0g2 c6c5 b0c2 h9g7 i0h0 i9h9 c3c4 a9a8 b2b4 g9e7 g3g4 g6g5 a3a4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5" to listOf(
            "a0a1" to "车九进一",
            "h0h1" to "车二进一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 g3g4 c6c5 c3c4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a8a7 a4a5 e5e4 i3i4 i6i5 a0a1" to listOf(
            "h8h7" to "车8进1",
            "a7a8" to "车1退1",
            "b7b6" to "炮2进1",
        ),
        "g0e2 c9e7 b2b4 b9c7 h0g2 h9g7 i0h0 i9h9 g3g4 c6c5 a3a4 g6g5 c3c4 a9a8 b0c2 h9h8 a4a5 e6e5 g4g5 a8a7 e3e4 i6i5 g5g6 i5i4 a0a1" to listOf(
            "a7a8" to "车1退1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 c3c4 c6c5 g3g4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a6a5 e3e4 i6i5 i3i4 a8a7 a0a1" to listOf(
            "a7a6" to "车1进1",
            "h8h7" to "车8进1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h9g7 h0g2 a9a8 b0c2 b9c7 i0h0 g6g5 b2b4 i9h9 g3g4 c6c5 a3a4 g9e7 c3c4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1" to listOf(
            "a8a7" to "车1进1",
            "h9h8" to "车8进1",
            "b7b6" to "炮2进1",
        ),
        "c3c4 c6c5 c4c5 h9g7 h2e2 b9c7 h0g2 g9e7 b2b4 i9h9 b0c2 a9a8 g3g4 g6g5 a3a4 h9h8 i0h0 a8a7 g4g5 a6a5 c5c6 e6e5 e3e4 i6i5 g5g6" to listOf(
            "i5i4" to "卒9进1",
            "a7a6" to "车1进1",
            "h8h9" to "车8退1",
        ),
        "h2e2 b9c7 h0g2 c6c5 b0c2 h9g7 i0h0 i9h9 c3c4 a9a8 b2b4 g9e7 g3g4 g6g5 a3a4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1" to listOf(
            "a8a7" to "车1进1",
            "h9h8" to "车8进1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 g3g4 c6c5 c3c4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a8a7 a4a5 e5e4 i3i4 i6i5 a0a1 h8h7" to listOf(
            "a1a2" to "车九进一",
            "h0h1" to "车二进一",
            "b4b5" to "炮八进一",
        ),
        "g0e2 c9e7 b2b4 b9c7 h0g2 h9g7 i0h0 i9h9 g3g4 c6c5 a3a4 g6g5 c3c4 a9a8 b0c2 h9h8 a4a5 e6e5 g4g5 a8a7 e3e4 i6i5 g5g6 i5i4 a0a1 a7a8" to listOf(
            "a1a2" to "车九进一",
            "h0h1" to "车二进一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 c3c4 c6c5 g3g4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a6a5 e3e4 i6i5 i3i4 a8a7 a0a1 a7a6" to listOf(
            "a1a2" to "车九进一",
            "h0h1" to "车二进一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h9g7 h0g2 a9a8 b0c2 b9c7 i0h0 g6g5 b2b4 i9h9 g3g4 c6c5 a3a4 g9e7 c3c4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7" to listOf(
            "a1a2" to "车九进一",
            "h0h1" to "车二进一",
            "b4b5" to "炮八进一",
        ),
        "c3c4 c6c5 c4c5 h9g7 h2e2 b9c7 h0g2 g9e7 b2b4 i9h9 b0c2 a9a8 g3g4 g6g5 a3a4 h9h8 i0h0 a8a7 g4g5 a6a5 c5c6 e6e5 e3e4 i6i5 g5g6 i5i4" to listOf(
            "a0a1" to "车九进一",
            "h0h1" to "车二进一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 b9c7 h0g2 c6c5 b0c2 h9g7 i0h0 i9h9 c3c4 a9a8 b2b4 g9e7 g3g4 g6g5 a3a4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7" to listOf(
            "a1a2" to "车九进一",
            "h0h1" to "车二进一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 g3g4 c6c5 c3c4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a8a7 a4a5 e5e4 i3i4 i6i5 a0a1 h8h7 a1a2" to listOf(
            "h7h6" to "车8进1",
            "a7a8" to "车1退1",
            "b7b6" to "炮2进1",
        ),
        "g0e2 c9e7 b2b4 b9c7 h0g2 h9g7 i0h0 i9h9 g3g4 c6c5 a3a4 g6g5 c3c4 a9a8 b0c2 h9h8 a4a5 e6e5 g4g5 a8a7 e3e4 i6i5 g5g6 i5i4 a0a1 a7a8 a1a2" to listOf(
            "a8a7" to "车1进1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 c3c4 c6c5 g3g4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a6a5 e3e4 i6i5 i3i4 a8a7 a0a1 a7a6 a1a2" to listOf(
            "h8h7" to "车8进1",
            "a6a7" to "车1退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h9g7 h0g2 a9a8 b0c2 b9c7 i0h0 g6g5 b2b4 i9h9 g3g4 c6c5 a3a4 g9e7 c3c4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2" to listOf(
            "a7a6" to "车1进1",
            "h9h8" to "车8进1",
            "b7b6" to "炮2进1",
        ),
        "c3c4 c6c5 c4c5 h9g7 h2e2 b9c7 h0g2 g9e7 b2b4 i9h9 b0c2 a9a8 g3g4 g6g5 a3a4 h9h8 i0h0 a8a7 g4g5 a6a5 c5c6 e6e5 e3e4 i6i5 g5g6 i5i4 a0a1" to listOf(
            "a7a6" to "车1进1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 b9c7 h0g2 c6c5 b0c2 h9g7 i0h0 i9h9 c3c4 a9a8 b2b4 g9e7 g3g4 g6g5 a3a4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2" to listOf(
            "a7a6" to "车1进1",
            "h9h8" to "车8进1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 g3g4 c6c5 c3c4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a8a7 a4a5 e5e4 i3i4 i6i5 a0a1 h8h7 a1a2 h7h6" to listOf(
            "a2a3" to "车九进一",
            "h0h1" to "车二进一",
            "b4b5" to "炮八进一",
        ),
        "g0e2 c9e7 b2b4 b9c7 h0g2 h9g7 i0h0 i9h9 g3g4 c6c5 a3a4 g6g5 c3c4 a9a8 b0c2 h9h8 a4a5 e6e5 g4g5 a8a7 e3e4 i6i5 g5g6 i5i4 a0a1 a7a8 a1a2 a8a7" to listOf(
            "a2a3" to "车九进一",
            "h0h1" to "车二进一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 c3c4 c6c5 g3g4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a6a5 e3e4 i6i5 i3i4 a8a7 a0a1 a7a6 a1a2 h8h7" to listOf(
            "a2a3" to "车九进一",
            "h0h1" to "车二进一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h9g7 h0g2 a9a8 b0c2 b9c7 i0h0 g6g5 b2b4 i9h9 g3g4 c6c5 a3a4 g9e7 c3c4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6" to listOf(
            "a2a3" to "车九进一",
            "h0h1" to "车二进一",
            "b4b5" to "炮八进一",
        ),
        "c3c4 c6c5 c4c5 h9g7 h2e2 b9c7 h0g2 g9e7 b2b4 i9h9 b0c2 a9a8 g3g4 g6g5 a3a4 h9h8 i0h0 a8a7 g4g5 a6a5 c5c6 e6e5 e3e4 i6i5 g5g6 i5i4 a0a1 a7a6" to listOf(
            "a1a2" to "车九进一",
            "h0h1" to "车二进一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 b9c7 h0g2 c6c5 b0c2 h9g7 i0h0 i9h9 c3c4 a9a8 b2b4 g9e7 g3g4 g6g5 a3a4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6" to listOf(
            "a2a3" to "车九进一",
            "h0h1" to "车二进一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 g3g4 c6c5 c3c4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a8a7 a4a5 e5e4 i3i4 i6i5 a0a1 h8h7 a1a2 h7h6 a2a3" to listOf(
            "h6h5" to "车8进1",
            "a7a8" to "车1退1",
            "b7b6" to "炮2进1",
        ),
        "g0e2 c9e7 b2b4 b9c7 h0g2 h9g7 i0h0 i9h9 g3g4 c6c5 a3a4 g6g5 c3c4 a9a8 b0c2 h9h8 a4a5 e6e5 g4g5 a8a7 e3e4 i6i5 g5g6 i5i4 a0a1 a7a8 a1a2 a8a7 a2a3" to listOf(
            "a7a8" to "车1退1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 c3c4 c6c5 g3g4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a6a5 e3e4 i6i5 i3i4 a8a7 a0a1 a7a6 a1a2 h8h7 a2a3" to listOf(
            "h7h6" to "车8进1",
            "a6a7" to "车1退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h9g7 h0g2 a9a8 b0c2 b9c7 i0h0 g6g5 b2b4 i9h9 g3g4 c6c5 a3a4 g9e7 c3c4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3" to listOf(
            "h9h8" to "车8进1",
            "a6a7" to "车1退1",
            "b7b6" to "炮2进1",
        ),
        "c3c4 c6c5 c4c5 h9g7 h2e2 b9c7 h0g2 g9e7 b2b4 i9h9 b0c2 a9a8 g3g4 g6g5 a3a4 h9h8 i0h0 a8a7 g4g5 a6a5 c5c6 e6e5 e3e4 i6i5 g5g6 i5i4 a0a1 a7a6 a1a2" to listOf(
            "a6a7" to "车1退1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 b9c7 h0g2 c6c5 b0c2 h9g7 i0h0 i9h9 c3c4 a9a8 b2b4 g9e7 g3g4 g6g5 a3a4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3" to listOf(
            "h9h8" to "车8进1",
            "a6a7" to "车1退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 g3g4 c6c5 c3c4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a8a7 a4a5 e5e4 i3i4 i6i5 a0a1 h8h7 a1a2 h7h6 a2a3 h6h5" to listOf(
            "a3a4" to "车九进一",
            "h0h1" to "车二进一",
            "b4b5" to "炮八进一",
        ),
        "g0e2 c9e7 b2b4 b9c7 h0g2 h9g7 i0h0 i9h9 g3g4 c6c5 a3a4 g6g5 c3c4 a9a8 b0c2 h9h8 a4a5 e6e5 g4g5 a8a7 e3e4 i6i5 g5g6 i5i4 a0a1 a7a8 a1a2 a8a7 a2a3 a7a8" to listOf(
            "a3a4" to "车九进一",
            "h0h1" to "车二进一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 c3c4 c6c5 g3g4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a6a5 e3e4 i6i5 i3i4 a8a7 a0a1 a7a6 a1a2 h8h7 a2a3 h7h6" to listOf(
            "h0h1" to "车二进一",
            "a3a2" to "车九退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h9g7 h0g2 a9a8 b0c2 b9c7 i0h0 g6g5 b2b4 i9h9 g3g4 c6c5 a3a4 g9e7 c3c4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8" to listOf(
            "h0h1" to "车二进一",
            "a3a2" to "车九退一",
            "b4b5" to "炮八进一",
        ),
        "c3c4 c6c5 c4c5 h9g7 h2e2 b9c7 h0g2 g9e7 b2b4 i9h9 b0c2 a9a8 g3g4 g6g5 a3a4 h9h8 i0h0 a8a7 g4g5 a6a5 c5c6 e6e5 e3e4 i6i5 g5g6 i5i4 a0a1 a7a6 a1a2 a6a7" to listOf(
            "a2a3" to "车九进一",
            "h0h1" to "车二进一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 b9c7 h0g2 c6c5 b0c2 h9g7 i0h0 i9h9 c3c4 a9a8 b2b4 g9e7 g3g4 g6g5 a3a4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8" to listOf(
            "h0h1" to "车二进一",
            "a3a2" to "车九退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 g3g4 c6c5 c3c4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a8a7 a4a5 e5e4 i3i4 i6i5 a0a1 h8h7 a1a2 h7h6 a2a3 h6h5 a3a4" to listOf(
            "h5h4" to "车8进1",
            "a7a8" to "车1退1",
            "b7b6" to "炮2进1",
        ),
        "g0e2 c9e7 b2b4 b9c7 h0g2 h9g7 i0h0 i9h9 g3g4 c6c5 a3a4 g6g5 c3c4 a9a8 b0c2 h9h8 a4a5 e6e5 g4g5 a8a7 e3e4 i6i5 g5g6 i5i4 a0a1 a7a8 a1a2 a8a7 a2a3 a7a8 a3a4" to listOf(
            "a8a7" to "车1进1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 c3c4 c6c5 g3g4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a6a5 e3e4 i6i5 i3i4 a8a7 a0a1 a7a6 a1a2 h8h7 a2a3 h7h6 h0h1" to listOf(
            "h6h5" to "车8进1",
            "a6a7" to "车1退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h9g7 h0g2 a9a8 b0c2 b9c7 i0h0 g6g5 b2b4 i9h9 g3g4 c6c5 a3a4 g9e7 c3c4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1" to listOf(
            "a6a7" to "车1退1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "c3c4 c6c5 c4c5 h9g7 h2e2 b9c7 h0g2 g9e7 b2b4 i9h9 b0c2 a9a8 g3g4 g6g5 a3a4 h9h8 i0h0 a8a7 g4g5 a6a5 c5c6 e6e5 e3e4 i6i5 g5g6 i5i4 a0a1 a7a6 a1a2 a6a7 a2a3" to listOf(
            "a7a6" to "车1进1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 b9c7 h0g2 c6c5 b0c2 h9g7 i0h0 i9h9 c3c4 a9a8 b2b4 g9e7 g3g4 g6g5 a3a4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1" to listOf(
            "a6a7" to "车1退1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 g3g4 c6c5 c3c4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a8a7 a4a5 e5e4 i3i4 i6i5 a0a1 h8h7 a1a2 h7h6 a2a3 h6h5 a3a4 h5h4" to listOf(
            "h0h1" to "车二进一",
            "a4a3" to "车九退一",
            "b4b5" to "炮八进一",
        ),
        "g0e2 c9e7 b2b4 b9c7 h0g2 h9g7 i0h0 i9h9 g3g4 c6c5 a3a4 g6g5 c3c4 a9a8 b0c2 h9h8 a4a5 e6e5 g4g5 a8a7 e3e4 i6i5 g5g6 i5i4 a0a1 a7a8 a1a2 a8a7 a2a3 a7a8 a3a4 a8a7" to listOf(
            "h0h1" to "车二进一",
            "a4a3" to "车九退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 c3c4 c6c5 g3g4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a6a5 e3e4 i6i5 i3i4 a8a7 a0a1 a7a6 a1a2 h8h7 a2a3 h7h6 h0h1 h6h5" to listOf(
            "h1h2" to "车二进一",
            "a3a2" to "车九退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h9g7 h0g2 a9a8 b0c2 b9c7 i0h0 g6g5 b2b4 i9h9 g3g4 c6c5 a3a4 g9e7 c3c4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7" to listOf(
            "h1h2" to "车二进一",
            "a3a2" to "车九退一",
            "b4b5" to "炮八进一",
        ),
        "c3c4 c6c5 c4c5 h9g7 h2e2 b9c7 h0g2 g9e7 b2b4 i9h9 b0c2 a9a8 g3g4 g6g5 a3a4 h9h8 i0h0 a8a7 g4g5 a6a5 c5c6 e6e5 e3e4 i6i5 g5g6 i5i4 a0a1 a7a6 a1a2 a6a7 a2a3 a7a6" to listOf(
            "h0h1" to "车二进一",
            "a3a2" to "车九退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 b9c7 h0g2 c6c5 b0c2 h9g7 i0h0 i9h9 c3c4 a9a8 b2b4 g9e7 g3g4 g6g5 a3a4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7" to listOf(
            "h1h2" to "车二进一",
            "a3a2" to "车九退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 g3g4 c6c5 c3c4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a8a7 a4a5 e5e4 i3i4 i6i5 a0a1 h8h7 a1a2 h7h6 a2a3 h6h5 a3a4 h5h4 h0h1" to listOf(
            "h4h3" to "车8进1",
            "a7a8" to "车1退1",
            "b7b6" to "炮2进1",
        ),
        "g0e2 c9e7 b2b4 b9c7 h0g2 h9g7 i0h0 i9h9 g3g4 c6c5 a3a4 g6g5 c3c4 a9a8 b0c2 h9h8 a4a5 e6e5 g4g5 a8a7 e3e4 i6i5 g5g6 i5i4 a0a1 a7a8 a1a2 a8a7 a2a3 a7a8 a3a4 a8a7 h0h1" to listOf(
            "a7a8" to "车1退1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 c3c4 c6c5 g3g4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a6a5 e3e4 i6i5 i3i4 a8a7 a0a1 a7a6 a1a2 h8h7 a2a3 h7h6 h0h1 h6h5 h1h2" to listOf(
            "h5h4" to "车8进1",
            "a6a7" to "车1退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h9g7 h0g2 a9a8 b0c2 b9c7 i0h0 g6g5 b2b4 i9h9 g3g4 c6c5 a3a4 g9e7 c3c4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2" to listOf(
            "a7a6" to "车1进1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "c3c4 c6c5 c4c5 h9g7 h2e2 b9c7 h0g2 g9e7 b2b4 i9h9 b0c2 a9a8 g3g4 g6g5 a3a4 h9h8 i0h0 a8a7 g4g5 a6a5 c5c6 e6e5 e3e4 i6i5 g5g6 i5i4 a0a1 a7a6 a1a2 a6a7 a2a3 a7a6 h0h1" to listOf(
            "a6a7" to "车1退1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 b9c7 h0g2 c6c5 b0c2 h9g7 i0h0 i9h9 c3c4 a9a8 b2b4 g9e7 g3g4 g6g5 a3a4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2" to listOf(
            "a7a6" to "车1进1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 g3g4 c6c5 c3c4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a8a7 a4a5 e5e4 i3i4 i6i5 a0a1 h8h7 a1a2 h7h6 a2a3 h6h5 a3a4 h5h4 h0h1 h4h3" to listOf(
            "h1h2" to "车二进一",
            "a4a3" to "车九退一",
            "b4b5" to "炮八进一",
        ),
        "g0e2 c9e7 b2b4 b9c7 h0g2 h9g7 i0h0 i9h9 g3g4 c6c5 a3a4 g6g5 c3c4 a9a8 b0c2 h9h8 a4a5 e6e5 g4g5 a8a7 e3e4 i6i5 g5g6 i5i4 a0a1 a7a8 a1a2 a8a7 a2a3 a7a8 a3a4 a8a7 h0h1 a7a8" to listOf(
            "a4a3" to "车九退一",
            "h1h0" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 c3c4 c6c5 g3g4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a6a5 e3e4 i6i5 i3i4 a8a7 a0a1 a7a6 a1a2 h8h7 a2a3 h7h6 h0h1 h6h5 h1h2 h5h4" to listOf(
            "h2h3" to "车二进一",
            "a3a2" to "车九退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h9g7 h0g2 a9a8 b0c2 b9c7 i0h0 g6g5 b2b4 i9h9 g3g4 c6c5 a3a4 g9e7 c3c4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6" to listOf(
            "h2h3" to "车二进一",
            "a3a2" to "车九退一",
            "b4b5" to "炮八进一",
        ),
        "c3c4 c6c5 c4c5 h9g7 h2e2 b9c7 h0g2 g9e7 b2b4 i9h9 b0c2 a9a8 g3g4 g6g5 a3a4 h9h8 i0h0 a8a7 g4g5 a6a5 c5c6 e6e5 e3e4 i6i5 g5g6 i5i4 a0a1 a7a6 a1a2 a6a7 a2a3 a7a6 h0h1 a6a7" to listOf(
            "h1h2" to "车二进一",
            "a3a2" to "车九退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 b9c7 h0g2 c6c5 b0c2 h9g7 i0h0 i9h9 c3c4 a9a8 b2b4 g9e7 g3g4 g6g5 a3a4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6" to listOf(
            "h2h3" to "车二进一",
            "a3a2" to "车九退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 g3g4 c6c5 c3c4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a8a7 a4a5 e5e4 i3i4 i6i5 a0a1 h8h7 a1a2 h7h6 a2a3 h6h5 a3a4 h5h4 h0h1 h4h3 h1h2" to listOf(
            "a7a8" to "车1退1",
            "h3h4" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "g0e2 c9e7 b2b4 b9c7 h0g2 h9g7 i0h0 i9h9 g3g4 c6c5 a3a4 g6g5 c3c4 a9a8 b0c2 h9h8 a4a5 e6e5 g4g5 a8a7 e3e4 i6i5 g5g6 i5i4 a0a1 a7a8 a1a2 a8a7 a2a3 a7a8 a3a4 a8a7 h0h1 a7a8 a4a3" to listOf(
            "a8a7" to "车1进1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 c3c4 c6c5 g3g4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a6a5 e3e4 i6i5 i3i4 a8a7 a0a1 a7a6 a1a2 h8h7 a2a3 h7h6 h0h1 h6h5 h1h2 h5h4 h2h3" to listOf(
            "a6a7" to "车1退1",
            "h4h5" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h9g7 h0g2 a9a8 b0c2 b9c7 i0h0 g6g5 b2b4 i9h9 g3g4 c6c5 a3a4 g9e7 c3c4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3" to listOf(
            "a6a7" to "车1退1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "c3c4 c6c5 c4c5 h9g7 h2e2 b9c7 h0g2 g9e7 b2b4 i9h9 b0c2 a9a8 g3g4 g6g5 a3a4 h9h8 i0h0 a8a7 g4g5 a6a5 c5c6 e6e5 e3e4 i6i5 g5g6 i5i4 a0a1 a7a6 a1a2 a6a7 a2a3 a7a6 h0h1 a6a7 h1h2" to listOf(
            "a7a6" to "车1进1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 b9c7 h0g2 c6c5 b0c2 h9g7 i0h0 i9h9 c3c4 a9a8 b2b4 g9e7 g3g4 g6g5 a3a4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3" to listOf(
            "a6a7" to "车1退1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 g3g4 c6c5 c3c4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a8a7 a4a5 e5e4 i3i4 i6i5 a0a1 h8h7 a1a2 h7h6 a2a3 h6h5 a3a4 h5h4 h0h1 h4h3 h1h2 a7a8" to listOf(
            "a4a3" to "车九退一",
            "h2h1" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "g0e2 c9e7 b2b4 b9c7 h0g2 h9g7 i0h0 i9h9 g3g4 c6c5 a3a4 g6g5 c3c4 a9a8 b0c2 h9h8 a4a5 e6e5 g4g5 a8a7 e3e4 i6i5 g5g6 i5i4 a0a1 a7a8 a1a2 a8a7 a2a3 a7a8 a3a4 a8a7 h0h1 a7a8 a4a3 a8a7" to listOf(
            "a3a4" to "车九进一",
            "h1h0" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 c3c4 c6c5 g3g4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a6a5 e3e4 i6i5 i3i4 a8a7 a0a1 a7a6 a1a2 h8h7 a2a3 h7h6 h0h1 h6h5 h1h2 h5h4 h2h3 a6a7" to listOf(
            "a3a2" to "车九退一",
            "h3h2" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h9g7 h0g2 a9a8 b0c2 b9c7 i0h0 g6g5 b2b4 i9h9 g3g4 c6c5 a3a4 g9e7 c3c4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7" to listOf(
            "h3h4" to "车二进一",
            "a3a2" to "车九退一",
            "b4b5" to "炮八进一",
        ),
        "c3c4 c6c5 c4c5 h9g7 h2e2 b9c7 h0g2 g9e7 b2b4 i9h9 b0c2 a9a8 g3g4 g6g5 a3a4 h9h8 i0h0 a8a7 g4g5 a6a5 c5c6 e6e5 e3e4 i6i5 g5g6 i5i4 a0a1 a7a6 a1a2 a6a7 a2a3 a7a6 h0h1 a6a7 h1h2 a7a6" to listOf(
            "h2h3" to "车二进一",
            "a3a2" to "车九退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 b9c7 h0g2 c6c5 b0c2 h9g7 i0h0 i9h9 c3c4 a9a8 b2b4 g9e7 g3g4 g6g5 a3a4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7" to listOf(
            "h3h4" to "车二进一",
            "a3a2" to "车九退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 g3g4 c6c5 c3c4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a8a7 a4a5 e5e4 i3i4 i6i5 a0a1 h8h7 a1a2 h7h6 a2a3 h6h5 a3a4 h5h4 h0h1 h4h3 h1h2 a7a8 a4a3" to listOf(
            "a8a7" to "车1进1",
            "h3h4" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "g0e2 c9e7 b2b4 b9c7 h0g2 h9g7 i0h0 i9h9 g3g4 c6c5 a3a4 g6g5 c3c4 a9a8 b0c2 h9h8 a4a5 e6e5 g4g5 a8a7 e3e4 i6i5 g5g6 i5i4 a0a1 a7a8 a1a2 a8a7 a2a3 a7a8 a3a4 a8a7 h0h1 a7a8 a4a3 a8a7 a3a4" to listOf(
            "a7a8" to "车1退1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 c3c4 c6c5 g3g4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a6a5 e3e4 i6i5 i3i4 a8a7 a0a1 a7a6 a1a2 h8h7 a2a3 h7h6 h0h1 h6h5 h1h2 h5h4 h2h3 a6a7 a3a2" to listOf(
            "a7a6" to "车1进1",
            "h4h5" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h9g7 h0g2 a9a8 b0c2 b9c7 i0h0 g6g5 b2b4 i9h9 g3g4 c6c5 a3a4 g9e7 c3c4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4" to listOf(
            "a7a6" to "车1进1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "c3c4 c6c5 c4c5 h9g7 h2e2 b9c7 h0g2 g9e7 b2b4 i9h9 b0c2 a9a8 g3g4 g6g5 a3a4 h9h8 i0h0 a8a7 g4g5 a6a5 c5c6 e6e5 e3e4 i6i5 g5g6 i5i4 a0a1 a7a6 a1a2 a6a7 a2a3 a7a6 h0h1 a6a7 h1h2 a7a6 h2h3" to listOf(
            "a6a7" to "车1退1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 b9c7 h0g2 c6c5 b0c2 h9g7 i0h0 i9h9 c3c4 a9a8 b2b4 g9e7 g3g4 g6g5 a3a4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4" to listOf(
            "a7a6" to "车1进1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 g3g4 c6c5 c3c4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a8a7 a4a5 e5e4 i3i4 i6i5 a0a1 h8h7 a1a2 h7h6 a2a3 h6h5 a3a4 h5h4 h0h1 h4h3 h1h2 a7a8 a4a3 a8a7" to listOf(
            "a3a4" to "车九进一",
            "h2h1" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "g0e2 c9e7 b2b4 b9c7 h0g2 h9g7 i0h0 i9h9 g3g4 c6c5 a3a4 g6g5 c3c4 a9a8 b0c2 h9h8 a4a5 e6e5 g4g5 a8a7 e3e4 i6i5 g5g6 i5i4 a0a1 a7a8 a1a2 a8a7 a2a3 a7a8 a3a4 a8a7 h0h1 a7a8 a4a3 a8a7 a3a4 a7a8" to listOf(
            "a4a3" to "车九退一",
            "h1h0" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 c3c4 c6c5 g3g4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a6a5 e3e4 i6i5 i3i4 a8a7 a0a1 a7a6 a1a2 h8h7 a2a3 h7h6 h0h1 h6h5 h1h2 h5h4 h2h3 a6a7 a3a2 a7a6" to listOf(
            "a2a3" to "车九进一",
            "h3h2" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h9g7 h0g2 a9a8 b0c2 b9c7 i0h0 g6g5 b2b4 i9h9 g3g4 c6c5 a3a4 g9e7 c3c4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6" to listOf(
            "h4h5" to "车二进一",
            "a3a2" to "车九退一",
            "b4b5" to "炮八进一",
        ),
        "c3c4 c6c5 c4c5 h9g7 h2e2 b9c7 h0g2 g9e7 b2b4 i9h9 b0c2 a9a8 g3g4 g6g5 a3a4 h9h8 i0h0 a8a7 g4g5 a6a5 c5c6 e6e5 e3e4 i6i5 g5g6 i5i4 a0a1 a7a6 a1a2 a6a7 a2a3 a7a6 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7" to listOf(
            "h3h4" to "车二进一",
            "a3a2" to "车九退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 b9c7 h0g2 c6c5 b0c2 h9g7 i0h0 i9h9 c3c4 a9a8 b2b4 g9e7 g3g4 g6g5 a3a4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6" to listOf(
            "h4h5" to "车二进一",
            "a3a2" to "车九退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 g3g4 c6c5 c3c4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a8a7 a4a5 e5e4 i3i4 i6i5 a0a1 h8h7 a1a2 h7h6 a2a3 h6h5 a3a4 h5h4 h0h1 h4h3 h1h2 a7a8 a4a3 a8a7 a3a4" to listOf(
            "a7a8" to "车1退1",
            "h3h4" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "g0e2 c9e7 b2b4 b9c7 h0g2 h9g7 i0h0 i9h9 g3g4 c6c5 a3a4 g6g5 c3c4 a9a8 b0c2 h9h8 a4a5 e6e5 g4g5 a8a7 e3e4 i6i5 g5g6 i5i4 a0a1 a7a8 a1a2 a8a7 a2a3 a7a8 a3a4 a8a7 h0h1 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3" to listOf(
            "a8a7" to "车1进1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 c3c4 c6c5 g3g4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a6a5 e3e4 i6i5 i3i4 a8a7 a0a1 a7a6 a1a2 h8h7 a2a3 h7h6 h0h1 h6h5 h1h2 h5h4 h2h3 a6a7 a3a2 a7a6 a2a3" to listOf(
            "a6a7" to "车1退1",
            "h4h5" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h9g7 h0g2 a9a8 b0c2 b9c7 i0h0 g6g5 b2b4 i9h9 g3g4 c6c5 a3a4 g9e7 c3c4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5" to listOf(
            "a6a7" to "车1退1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "c3c4 c6c5 c4c5 h9g7 h2e2 b9c7 h0g2 g9e7 b2b4 i9h9 b0c2 a9a8 g3g4 g6g5 a3a4 h9h8 i0h0 a8a7 g4g5 a6a5 c5c6 e6e5 e3e4 i6i5 g5g6 i5i4 a0a1 a7a6 a1a2 a6a7 a2a3 a7a6 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4" to listOf(
            "a7a6" to "车1进1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 b9c7 h0g2 c6c5 b0c2 h9g7 i0h0 i9h9 c3c4 a9a8 b2b4 g9e7 g3g4 g6g5 a3a4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5" to listOf(
            "a6a7" to "车1退1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 g3g4 c6c5 c3c4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a8a7 a4a5 e5e4 i3i4 i6i5 a0a1 h8h7 a1a2 h7h6 a2a3 h6h5 a3a4 h5h4 h0h1 h4h3 h1h2 a7a8 a4a3 a8a7 a3a4 a7a8" to listOf(
            "a4a3" to "车九退一",
            "h2h1" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "g0e2 c9e7 b2b4 b9c7 h0g2 h9g7 i0h0 i9h9 g3g4 c6c5 a3a4 g6g5 c3c4 a9a8 b0c2 h9h8 a4a5 e6e5 g4g5 a8a7 e3e4 i6i5 g5g6 i5i4 a0a1 a7a8 a1a2 a8a7 a2a3 a7a8 a3a4 a8a7 h0h1 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7" to listOf(
            "a3a4" to "车九进一",
            "h1h0" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 c3c4 c6c5 g3g4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a6a5 e3e4 i6i5 i3i4 a8a7 a0a1 a7a6 a1a2 h8h7 a2a3 h7h6 h0h1 h6h5 h1h2 h5h4 h2h3 a6a7 a3a2 a7a6 a2a3 a6a7" to listOf(
            "a3a2" to "车九退一",
            "h3h2" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h9g7 h0g2 a9a8 b0c2 b9c7 i0h0 g6g5 b2b4 i9h9 g3g4 c6c5 a3a4 g9e7 c3c4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7" to listOf(
            "h5h6" to "车二进一",
            "a3a2" to "车九退一",
            "b4b5" to "炮八进一",
        ),
        "c3c4 c6c5 c4c5 h9g7 h2e2 b9c7 h0g2 g9e7 b2b4 i9h9 b0c2 a9a8 g3g4 g6g5 a3a4 h9h8 i0h0 a8a7 g4g5 a6a5 c5c6 e6e5 e3e4 i6i5 g5g6 i5i4 a0a1 a7a6 a1a2 a6a7 a2a3 a7a6 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6" to listOf(
            "h4h5" to "车二进一",
            "a3a2" to "车九退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 b9c7 h0g2 c6c5 b0c2 h9g7 i0h0 i9h9 c3c4 a9a8 b2b4 g9e7 g3g4 g6g5 a3a4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7" to listOf(
            "h5h6" to "车二进一",
            "a3a2" to "车九退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 g3g4 c6c5 c3c4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a8a7 a4a5 e5e4 i3i4 i6i5 a0a1 h8h7 a1a2 h7h6 a2a3 h6h5 a3a4 h5h4 h0h1 h4h3 h1h2 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3" to listOf(
            "a8a7" to "车1进1",
            "h3h4" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "g0e2 c9e7 b2b4 b9c7 h0g2 h9g7 i0h0 i9h9 g3g4 c6c5 a3a4 g6g5 c3c4 a9a8 b0c2 h9h8 a4a5 e6e5 g4g5 a8a7 e3e4 i6i5 g5g6 i5i4 a0a1 a7a8 a1a2 a8a7 a2a3 a7a8 a3a4 a8a7 h0h1 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4" to listOf(
            "a7a8" to "车1退1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 c3c4 c6c5 g3g4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a6a5 e3e4 i6i5 i3i4 a8a7 a0a1 a7a6 a1a2 h8h7 a2a3 h7h6 h0h1 h6h5 h1h2 h5h4 h2h3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2" to listOf(
            "a7a6" to "车1进1",
            "h4h5" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h9g7 h0g2 a9a8 b0c2 b9c7 i0h0 g6g5 b2b4 i9h9 g3g4 c6c5 a3a4 g9e7 c3c4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6" to listOf(
            "a7a6" to "车1进1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "c3c4 c6c5 c4c5 h9g7 h2e2 b9c7 h0g2 g9e7 b2b4 i9h9 b0c2 a9a8 g3g4 g6g5 a3a4 h9h8 i0h0 a8a7 g4g5 a6a5 c5c6 e6e5 e3e4 i6i5 g5g6 i5i4 a0a1 a7a6 a1a2 a6a7 a2a3 a7a6 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5" to listOf(
            "a6a7" to "车1退1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 b9c7 h0g2 c6c5 b0c2 h9g7 i0h0 i9h9 c3c4 a9a8 b2b4 g9e7 g3g4 g6g5 a3a4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6" to listOf(
            "a7a6" to "车1进1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 g3g4 c6c5 c3c4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a8a7 a4a5 e5e4 i3i4 i6i5 a0a1 h8h7 a1a2 h7h6 a2a3 h6h5 a3a4 h5h4 h0h1 h4h3 h1h2 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7" to listOf(
            "a3a4" to "车九进一",
            "h2h1" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "g0e2 c9e7 b2b4 b9c7 h0g2 h9g7 i0h0 i9h9 g3g4 c6c5 a3a4 g6g5 c3c4 a9a8 b0c2 h9h8 a4a5 e6e5 g4g5 a8a7 e3e4 i6i5 g5g6 i5i4 a0a1 a7a8 a1a2 a8a7 a2a3 a7a8 a3a4 a8a7 h0h1 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8" to listOf(
            "a4a3" to "车九退一",
            "h1h0" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 c3c4 c6c5 g3g4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a6a5 e3e4 i6i5 i3i4 a8a7 a0a1 a7a6 a1a2 h8h7 a2a3 h7h6 h0h1 h6h5 h1h2 h5h4 h2h3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6" to listOf(
            "a2a3" to "车九进一",
            "h3h2" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h9g7 h0g2 a9a8 b0c2 b9c7 i0h0 g6g5 b2b4 i9h9 g3g4 c6c5 a3a4 g9e7 c3c4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6" to listOf(
            "a3a2" to "车九退一",
            "h6h5" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "c3c4 c6c5 c4c5 h9g7 h2e2 b9c7 h0g2 g9e7 b2b4 i9h9 b0c2 a9a8 g3g4 g6g5 a3a4 h9h8 i0h0 a8a7 g4g5 a6a5 c5c6 e6e5 e3e4 i6i5 g5g6 i5i4 a0a1 a7a6 a1a2 a6a7 a2a3 a7a6 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7" to listOf(
            "h5h6" to "车二进一",
            "a3a2" to "车九退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 b9c7 h0g2 c6c5 b0c2 h9g7 i0h0 i9h9 c3c4 a9a8 b2b4 g9e7 g3g4 g6g5 a3a4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6" to listOf(
            "a3a2" to "车九退一",
            "h6h5" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 g3g4 c6c5 c3c4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a8a7 a4a5 e5e4 i3i4 i6i5 a0a1 h8h7 a1a2 h7h6 a2a3 h6h5 a3a4 h5h4 h0h1 h4h3 h1h2 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4" to listOf(
            "a7a8" to "车1退1",
            "h3h4" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "g0e2 c9e7 b2b4 b9c7 h0g2 h9g7 i0h0 i9h9 g3g4 c6c5 a3a4 g6g5 c3c4 a9a8 b0c2 h9h8 a4a5 e6e5 g4g5 a8a7 e3e4 i6i5 g5g6 i5i4 a0a1 a7a8 a1a2 a8a7 a2a3 a7a8 a3a4 a8a7 h0h1 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3" to listOf(
            "a8a7" to "车1进1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 c3c4 c6c5 g3g4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a6a5 e3e4 i6i5 i3i4 a8a7 a0a1 a7a6 a1a2 h8h7 a2a3 h7h6 h0h1 h6h5 h1h2 h5h4 h2h3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3" to listOf(
            "a6a7" to "车1退1",
            "h4h5" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h9g7 h0g2 a9a8 b0c2 b9c7 i0h0 g6g5 b2b4 i9h9 g3g4 c6c5 a3a4 g9e7 c3c4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2" to listOf(
            "a6a7" to "车1退1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "c3c4 c6c5 c4c5 h9g7 h2e2 b9c7 h0g2 g9e7 b2b4 i9h9 b0c2 a9a8 g3g4 g6g5 a3a4 h9h8 i0h0 a8a7 g4g5 a6a5 c5c6 e6e5 e3e4 i6i5 g5g6 i5i4 a0a1 a7a6 a1a2 a6a7 a2a3 a7a6 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6" to listOf(
            "a7a6" to "车1进1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 b9c7 h0g2 c6c5 b0c2 h9g7 i0h0 i9h9 c3c4 a9a8 b2b4 g9e7 g3g4 g6g5 a3a4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2" to listOf(
            "a6a7" to "车1退1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 g3g4 c6c5 c3c4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a8a7 a4a5 e5e4 i3i4 i6i5 a0a1 h8h7 a1a2 h7h6 a2a3 h6h5 a3a4 h5h4 h0h1 h4h3 h1h2 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8" to listOf(
            "a4a3" to "车九退一",
            "h2h1" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "g0e2 c9e7 b2b4 b9c7 h0g2 h9g7 i0h0 i9h9 g3g4 c6c5 a3a4 g6g5 c3c4 a9a8 b0c2 h9h8 a4a5 e6e5 g4g5 a8a7 e3e4 i6i5 g5g6 i5i4 a0a1 a7a8 a1a2 a8a7 a2a3 a7a8 a3a4 a8a7 h0h1 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7" to listOf(
            "a3a4" to "车九进一",
            "h1h0" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 c3c4 c6c5 g3g4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a6a5 e3e4 i6i5 i3i4 a8a7 a0a1 a7a6 a1a2 h8h7 a2a3 h7h6 h0h1 h6h5 h1h2 h5h4 h2h3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7" to listOf(
            "a3a2" to "车九退一",
            "h3h2" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h9g7 h0g2 a9a8 b0c2 b9c7 i0h0 g6g5 b2b4 i9h9 g3g4 c6c5 a3a4 g9e7 c3c4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7" to listOf(
            "a2a3" to "车九进一",
            "h6h5" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "c3c4 c6c5 c4c5 h9g7 h2e2 b9c7 h0g2 g9e7 b2b4 i9h9 b0c2 a9a8 g3g4 g6g5 a3a4 h9h8 i0h0 a8a7 g4g5 a6a5 c5c6 e6e5 e3e4 i6i5 g5g6 i5i4 a0a1 a7a6 a1a2 a6a7 a2a3 a7a6 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6" to listOf(
            "a3a2" to "车九退一",
            "h6h5" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 b9c7 h0g2 c6c5 b0c2 h9g7 i0h0 i9h9 c3c4 a9a8 b2b4 g9e7 g3g4 g6g5 a3a4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7" to listOf(
            "a2a3" to "车九进一",
            "h6h5" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 g3g4 c6c5 c3c4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a8a7 a4a5 e5e4 i3i4 i6i5 a0a1 h8h7 a1a2 h7h6 a2a3 h6h5 a3a4 h5h4 h0h1 h4h3 h1h2 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3" to listOf(
            "a8a7" to "车1进1",
            "h3h4" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "g0e2 c9e7 b2b4 b9c7 h0g2 h9g7 i0h0 i9h9 g3g4 c6c5 a3a4 g6g5 c3c4 a9a8 b0c2 h9h8 a4a5 e6e5 g4g5 a8a7 e3e4 i6i5 g5g6 i5i4 a0a1 a7a8 a1a2 a8a7 a2a3 a7a8 a3a4 a8a7 h0h1 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4" to listOf(
            "a7a8" to "车1退1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 c3c4 c6c5 g3g4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a6a5 e3e4 i6i5 i3i4 a8a7 a0a1 a7a6 a1a2 h8h7 a2a3 h7h6 h0h1 h6h5 h1h2 h5h4 h2h3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2" to listOf(
            "a7a6" to "车1进1",
            "h4h5" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h9g7 h0g2 a9a8 b0c2 b9c7 i0h0 g6g5 b2b4 i9h9 g3g4 c6c5 a3a4 g9e7 c3c4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3" to listOf(
            "a7a6" to "车1进1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "c3c4 c6c5 c4c5 h9g7 h2e2 b9c7 h0g2 g9e7 b2b4 i9h9 b0c2 a9a8 g3g4 g6g5 a3a4 h9h8 i0h0 a8a7 g4g5 a6a5 c5c6 e6e5 e3e4 i6i5 g5g6 i5i4 a0a1 a7a6 a1a2 a6a7 a2a3 a7a6 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2" to listOf(
            "a6a7" to "车1退1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 b9c7 h0g2 c6c5 b0c2 h9g7 i0h0 i9h9 c3c4 a9a8 b2b4 g9e7 g3g4 g6g5 a3a4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3" to listOf(
            "a7a6" to "车1进1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 g3g4 c6c5 c3c4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a8a7 a4a5 e5e4 i3i4 i6i5 a0a1 h8h7 a1a2 h7h6 a2a3 h6h5 a3a4 h5h4 h0h1 h4h3 h1h2 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7" to listOf(
            "a3a4" to "车九进一",
            "h2h1" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "g0e2 c9e7 b2b4 b9c7 h0g2 h9g7 i0h0 i9h9 g3g4 c6c5 a3a4 g6g5 c3c4 a9a8 b0c2 h9h8 a4a5 e6e5 g4g5 a8a7 e3e4 i6i5 g5g6 i5i4 a0a1 a7a8 a1a2 a8a7 a2a3 a7a8 a3a4 a8a7 h0h1 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8" to listOf(
            "a4a3" to "车九退一",
            "h1h0" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 c3c4 c6c5 g3g4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a6a5 e3e4 i6i5 i3i4 a8a7 a0a1 a7a6 a1a2 h8h7 a2a3 h7h6 h0h1 h6h5 h1h2 h5h4 h2h3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6" to listOf(
            "a2a3" to "车九进一",
            "h3h2" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h9g7 h0g2 a9a8 b0c2 b9c7 i0h0 g6g5 b2b4 i9h9 g3g4 c6c5 a3a4 g9e7 c3c4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6" to listOf(
            "a3a2" to "车九退一",
            "h6h5" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "c3c4 c6c5 c4c5 h9g7 h2e2 b9c7 h0g2 g9e7 b2b4 i9h9 b0c2 a9a8 g3g4 g6g5 a3a4 h9h8 i0h0 a8a7 g4g5 a6a5 c5c6 e6e5 e3e4 i6i5 g5g6 i5i4 a0a1 a7a6 a1a2 a6a7 a2a3 a7a6 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7" to listOf(
            "a2a3" to "车九进一",
            "h6h5" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 b9c7 h0g2 c6c5 b0c2 h9g7 i0h0 i9h9 c3c4 a9a8 b2b4 g9e7 g3g4 g6g5 a3a4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6" to listOf(
            "a3a2" to "车九退一",
            "h6h5" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 g3g4 c6c5 c3c4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a8a7 a4a5 e5e4 i3i4 i6i5 a0a1 h8h7 a1a2 h7h6 a2a3 h6h5 a3a4 h5h4 h0h1 h4h3 h1h2 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4" to listOf(
            "a7a8" to "车1退1",
            "h3h4" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "g0e2 c9e7 b2b4 b9c7 h0g2 h9g7 i0h0 i9h9 g3g4 c6c5 a3a4 g6g5 c3c4 a9a8 b0c2 h9h8 a4a5 e6e5 g4g5 a8a7 e3e4 i6i5 g5g6 i5i4 a0a1 a7a8 a1a2 a8a7 a2a3 a7a8 a3a4 a8a7 h0h1 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3" to listOf(
            "a8a7" to "车1进1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 c3c4 c6c5 g3g4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a6a5 e3e4 i6i5 i3i4 a8a7 a0a1 a7a6 a1a2 h8h7 a2a3 h7h6 h0h1 h6h5 h1h2 h5h4 h2h3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3" to listOf(
            "a6a7" to "车1退1",
            "h4h5" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h9g7 h0g2 a9a8 b0c2 b9c7 i0h0 g6g5 b2b4 i9h9 g3g4 c6c5 a3a4 g9e7 c3c4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2" to listOf(
            "a6a7" to "车1退1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "c3c4 c6c5 c4c5 h9g7 h2e2 b9c7 h0g2 g9e7 b2b4 i9h9 b0c2 a9a8 g3g4 g6g5 a3a4 h9h8 i0h0 a8a7 g4g5 a6a5 c5c6 e6e5 e3e4 i6i5 g5g6 i5i4 a0a1 a7a6 a1a2 a6a7 a2a3 a7a6 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3" to listOf(
            "a7a6" to "车1进1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 b9c7 h0g2 c6c5 b0c2 h9g7 i0h0 i9h9 c3c4 a9a8 b2b4 g9e7 g3g4 g6g5 a3a4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2" to listOf(
            "a6a7" to "车1退1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 g3g4 c6c5 c3c4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a8a7 a4a5 e5e4 i3i4 i6i5 a0a1 h8h7 a1a2 h7h6 a2a3 h6h5 a3a4 h5h4 h0h1 h4h3 h1h2 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8" to listOf(
            "a4a3" to "车九退一",
            "h2h1" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "g0e2 c9e7 b2b4 b9c7 h0g2 h9g7 i0h0 i9h9 g3g4 c6c5 a3a4 g6g5 c3c4 a9a8 b0c2 h9h8 a4a5 e6e5 g4g5 a8a7 e3e4 i6i5 g5g6 i5i4 a0a1 a7a8 a1a2 a8a7 a2a3 a7a8 a3a4 a8a7 h0h1 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7" to listOf(
            "a3a4" to "车九进一",
            "h1h0" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 c3c4 c6c5 g3g4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a6a5 e3e4 i6i5 i3i4 a8a7 a0a1 a7a6 a1a2 h8h7 a2a3 h7h6 h0h1 h6h5 h1h2 h5h4 h2h3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7" to listOf(
            "a3a2" to "车九退一",
            "h3h2" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h9g7 h0g2 a9a8 b0c2 b9c7 i0h0 g6g5 b2b4 i9h9 g3g4 c6c5 a3a4 g9e7 c3c4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7" to listOf(
            "a2a3" to "车九进一",
            "h6h5" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "c3c4 c6c5 c4c5 h9g7 h2e2 b9c7 h0g2 g9e7 b2b4 i9h9 b0c2 a9a8 g3g4 g6g5 a3a4 h9h8 i0h0 a8a7 g4g5 a6a5 c5c6 e6e5 e3e4 i6i5 g5g6 i5i4 a0a1 a7a6 a1a2 a6a7 a2a3 a7a6 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6" to listOf(
            "a3a2" to "车九退一",
            "h6h5" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 b9c7 h0g2 c6c5 b0c2 h9g7 i0h0 i9h9 c3c4 a9a8 b2b4 g9e7 g3g4 g6g5 a3a4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7" to listOf(
            "a2a3" to "车九进一",
            "h6h5" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 g3g4 c6c5 c3c4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a8a7 a4a5 e5e4 i3i4 i6i5 a0a1 h8h7 a1a2 h7h6 a2a3 h6h5 a3a4 h5h4 h0h1 h4h3 h1h2 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3" to listOf(
            "a8a7" to "车1进1",
            "h3h4" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "g0e2 c9e7 b2b4 b9c7 h0g2 h9g7 i0h0 i9h9 g3g4 c6c5 a3a4 g6g5 c3c4 a9a8 b0c2 h9h8 a4a5 e6e5 g4g5 a8a7 e3e4 i6i5 g5g6 i5i4 a0a1 a7a8 a1a2 a8a7 a2a3 a7a8 a3a4 a8a7 h0h1 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4" to listOf(
            "a7a8" to "车1退1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 c3c4 c6c5 g3g4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a6a5 e3e4 i6i5 i3i4 a8a7 a0a1 a7a6 a1a2 h8h7 a2a3 h7h6 h0h1 h6h5 h1h2 h5h4 h2h3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2" to listOf(
            "a7a6" to "车1进1",
            "h4h5" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h9g7 h0g2 a9a8 b0c2 b9c7 i0h0 g6g5 b2b4 i9h9 g3g4 c6c5 a3a4 g9e7 c3c4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3" to listOf(
            "a7a6" to "车1进1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "c3c4 c6c5 c4c5 h9g7 h2e2 b9c7 h0g2 g9e7 b2b4 i9h9 b0c2 a9a8 g3g4 g6g5 a3a4 h9h8 i0h0 a8a7 g4g5 a6a5 c5c6 e6e5 e3e4 i6i5 g5g6 i5i4 a0a1 a7a6 a1a2 a6a7 a2a3 a7a6 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2" to listOf(
            "a6a7" to "车1退1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 b9c7 h0g2 c6c5 b0c2 h9g7 i0h0 i9h9 c3c4 a9a8 b2b4 g9e7 g3g4 g6g5 a3a4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3" to listOf(
            "a7a6" to "车1进1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 g3g4 c6c5 c3c4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a8a7 a4a5 e5e4 i3i4 i6i5 a0a1 h8h7 a1a2 h7h6 a2a3 h6h5 a3a4 h5h4 h0h1 h4h3 h1h2 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7" to listOf(
            "a3a4" to "车九进一",
            "h2h1" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "g0e2 c9e7 b2b4 b9c7 h0g2 h9g7 i0h0 i9h9 g3g4 c6c5 a3a4 g6g5 c3c4 a9a8 b0c2 h9h8 a4a5 e6e5 g4g5 a8a7 e3e4 i6i5 g5g6 i5i4 a0a1 a7a8 a1a2 a8a7 a2a3 a7a8 a3a4 a8a7 h0h1 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8" to listOf(
            "a4a3" to "车九退一",
            "h1h0" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 c3c4 c6c5 g3g4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a6a5 e3e4 i6i5 i3i4 a8a7 a0a1 a7a6 a1a2 h8h7 a2a3 h7h6 h0h1 h6h5 h1h2 h5h4 h2h3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6" to listOf(
            "a2a3" to "车九进一",
            "h3h2" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h9g7 h0g2 a9a8 b0c2 b9c7 i0h0 g6g5 b2b4 i9h9 g3g4 c6c5 a3a4 g9e7 c3c4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6" to listOf(
            "a3a2" to "车九退一",
            "h6h5" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "c3c4 c6c5 c4c5 h9g7 h2e2 b9c7 h0g2 g9e7 b2b4 i9h9 b0c2 a9a8 g3g4 g6g5 a3a4 h9h8 i0h0 a8a7 g4g5 a6a5 c5c6 e6e5 e3e4 i6i5 g5g6 i5i4 a0a1 a7a6 a1a2 a6a7 a2a3 a7a6 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7" to listOf(
            "a2a3" to "车九进一",
            "h6h5" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 b9c7 h0g2 c6c5 b0c2 h9g7 i0h0 i9h9 c3c4 a9a8 b2b4 g9e7 g3g4 g6g5 a3a4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6" to listOf(
            "a3a2" to "车九退一",
            "h6h5" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 g3g4 c6c5 c3c4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a8a7 a4a5 e5e4 i3i4 i6i5 a0a1 h8h7 a1a2 h7h6 a2a3 h6h5 a3a4 h5h4 h0h1 h4h3 h1h2 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4" to listOf(
            "a7a8" to "车1退1",
            "h3h4" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "g0e2 c9e7 b2b4 b9c7 h0g2 h9g7 i0h0 i9h9 g3g4 c6c5 a3a4 g6g5 c3c4 a9a8 b0c2 h9h8 a4a5 e6e5 g4g5 a8a7 e3e4 i6i5 g5g6 i5i4 a0a1 a7a8 a1a2 a8a7 a2a3 a7a8 a3a4 a8a7 h0h1 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3" to listOf(
            "a8a7" to "车1进1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 c3c4 c6c5 g3g4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a6a5 e3e4 i6i5 i3i4 a8a7 a0a1 a7a6 a1a2 h8h7 a2a3 h7h6 h0h1 h6h5 h1h2 h5h4 h2h3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3" to listOf(
            "a6a7" to "车1退1",
            "h4h5" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h9g7 h0g2 a9a8 b0c2 b9c7 i0h0 g6g5 b2b4 i9h9 g3g4 c6c5 a3a4 g9e7 c3c4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2" to listOf(
            "a6a7" to "车1退1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "c3c4 c6c5 c4c5 h9g7 h2e2 b9c7 h0g2 g9e7 b2b4 i9h9 b0c2 a9a8 g3g4 g6g5 a3a4 h9h8 i0h0 a8a7 g4g5 a6a5 c5c6 e6e5 e3e4 i6i5 g5g6 i5i4 a0a1 a7a6 a1a2 a6a7 a2a3 a7a6 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3" to listOf(
            "a7a6" to "车1进1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 b9c7 h0g2 c6c5 b0c2 h9g7 i0h0 i9h9 c3c4 a9a8 b2b4 g9e7 g3g4 g6g5 a3a4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2" to listOf(
            "a6a7" to "车1退1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 g3g4 c6c5 c3c4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a8a7 a4a5 e5e4 i3i4 i6i5 a0a1 h8h7 a1a2 h7h6 a2a3 h6h5 a3a4 h5h4 h0h1 h4h3 h1h2 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8" to listOf(
            "a4a3" to "车九退一",
            "h2h1" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "g0e2 c9e7 b2b4 b9c7 h0g2 h9g7 i0h0 i9h9 g3g4 c6c5 a3a4 g6g5 c3c4 a9a8 b0c2 h9h8 a4a5 e6e5 g4g5 a8a7 e3e4 i6i5 g5g6 i5i4 a0a1 a7a8 a1a2 a8a7 a2a3 a7a8 a3a4 a8a7 h0h1 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7" to listOf(
            "a3a4" to "车九进一",
            "h1h0" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 c3c4 c6c5 g3g4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a6a5 e3e4 i6i5 i3i4 a8a7 a0a1 a7a6 a1a2 h8h7 a2a3 h7h6 h0h1 h6h5 h1h2 h5h4 h2h3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7" to listOf(
            "a3a2" to "车九退一",
            "h3h2" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h9g7 h0g2 a9a8 b0c2 b9c7 i0h0 g6g5 b2b4 i9h9 g3g4 c6c5 a3a4 g9e7 c3c4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7" to listOf(
            "a2a3" to "车九进一",
            "h6h5" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "c3c4 c6c5 c4c5 h9g7 h2e2 b9c7 h0g2 g9e7 b2b4 i9h9 b0c2 a9a8 g3g4 g6g5 a3a4 h9h8 i0h0 a8a7 g4g5 a6a5 c5c6 e6e5 e3e4 i6i5 g5g6 i5i4 a0a1 a7a6 a1a2 a6a7 a2a3 a7a6 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6" to listOf(
            "a3a2" to "车九退一",
            "h6h5" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 b9c7 h0g2 c6c5 b0c2 h9g7 i0h0 i9h9 c3c4 a9a8 b2b4 g9e7 g3g4 g6g5 a3a4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7" to listOf(
            "a2a3" to "车九进一",
            "h6h5" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 g3g4 c6c5 c3c4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a8a7 a4a5 e5e4 i3i4 i6i5 a0a1 h8h7 a1a2 h7h6 a2a3 h6h5 a3a4 h5h4 h0h1 h4h3 h1h2 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3" to listOf(
            "a8a7" to "车1进1",
            "h3h4" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "g0e2 c9e7 b2b4 b9c7 h0g2 h9g7 i0h0 i9h9 g3g4 c6c5 a3a4 g6g5 c3c4 a9a8 b0c2 h9h8 a4a5 e6e5 g4g5 a8a7 e3e4 i6i5 g5g6 i5i4 a0a1 a7a8 a1a2 a8a7 a2a3 a7a8 a3a4 a8a7 h0h1 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4" to listOf(
            "a7a8" to "车1退1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 c3c4 c6c5 g3g4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a6a5 e3e4 i6i5 i3i4 a8a7 a0a1 a7a6 a1a2 h8h7 a2a3 h7h6 h0h1 h6h5 h1h2 h5h4 h2h3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2" to listOf(
            "a7a6" to "车1进1",
            "h4h5" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h9g7 h0g2 a9a8 b0c2 b9c7 i0h0 g6g5 b2b4 i9h9 g3g4 c6c5 a3a4 g9e7 c3c4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3" to listOf(
            "a7a6" to "车1进1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "c3c4 c6c5 c4c5 h9g7 h2e2 b9c7 h0g2 g9e7 b2b4 i9h9 b0c2 a9a8 g3g4 g6g5 a3a4 h9h8 i0h0 a8a7 g4g5 a6a5 c5c6 e6e5 e3e4 i6i5 g5g6 i5i4 a0a1 a7a6 a1a2 a6a7 a2a3 a7a6 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2" to listOf(
            "a6a7" to "车1退1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 b9c7 h0g2 c6c5 b0c2 h9g7 i0h0 i9h9 c3c4 a9a8 b2b4 g9e7 g3g4 g6g5 a3a4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3" to listOf(
            "a7a6" to "车1进1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 g3g4 c6c5 c3c4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a8a7 a4a5 e5e4 i3i4 i6i5 a0a1 h8h7 a1a2 h7h6 a2a3 h6h5 a3a4 h5h4 h0h1 h4h3 h1h2 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7" to listOf(
            "a3a4" to "车九进一",
            "h2h1" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "g0e2 c9e7 b2b4 b9c7 h0g2 h9g7 i0h0 i9h9 g3g4 c6c5 a3a4 g6g5 c3c4 a9a8 b0c2 h9h8 a4a5 e6e5 g4g5 a8a7 e3e4 i6i5 g5g6 i5i4 a0a1 a7a8 a1a2 a8a7 a2a3 a7a8 a3a4 a8a7 h0h1 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8" to listOf(
            "a4a3" to "车九退一",
            "h1h0" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 c3c4 c6c5 g3g4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a6a5 e3e4 i6i5 i3i4 a8a7 a0a1 a7a6 a1a2 h8h7 a2a3 h7h6 h0h1 h6h5 h1h2 h5h4 h2h3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6" to listOf(
            "a2a3" to "车九进一",
            "h3h2" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h9g7 h0g2 a9a8 b0c2 b9c7 i0h0 g6g5 b2b4 i9h9 g3g4 c6c5 a3a4 g9e7 c3c4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6" to listOf(
            "a3a2" to "车九退一",
            "h6h5" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "c3c4 c6c5 c4c5 h9g7 h2e2 b9c7 h0g2 g9e7 b2b4 i9h9 b0c2 a9a8 g3g4 g6g5 a3a4 h9h8 i0h0 a8a7 g4g5 a6a5 c5c6 e6e5 e3e4 i6i5 g5g6 i5i4 a0a1 a7a6 a1a2 a6a7 a2a3 a7a6 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7" to listOf(
            "a2a3" to "车九进一",
            "h6h5" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 b9c7 h0g2 c6c5 b0c2 h9g7 i0h0 i9h9 c3c4 a9a8 b2b4 g9e7 g3g4 g6g5 a3a4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6" to listOf(
            "a3a2" to "车九退一",
            "h6h5" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 g3g4 c6c5 c3c4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a8a7 a4a5 e5e4 i3i4 i6i5 a0a1 h8h7 a1a2 h7h6 a2a3 h6h5 a3a4 h5h4 h0h1 h4h3 h1h2 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4" to listOf(
            "a7a8" to "车1退1",
            "h3h4" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "g0e2 c9e7 b2b4 b9c7 h0g2 h9g7 i0h0 i9h9 g3g4 c6c5 a3a4 g6g5 c3c4 a9a8 b0c2 h9h8 a4a5 e6e5 g4g5 a8a7 e3e4 i6i5 g5g6 i5i4 a0a1 a7a8 a1a2 a8a7 a2a3 a7a8 a3a4 a8a7 h0h1 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3" to listOf(
            "a8a7" to "车1进1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 c3c4 c6c5 g3g4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a6a5 e3e4 i6i5 i3i4 a8a7 a0a1 a7a6 a1a2 h8h7 a2a3 h7h6 h0h1 h6h5 h1h2 h5h4 h2h3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3" to listOf(
            "a6a7" to "车1退1",
            "h4h5" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h9g7 h0g2 a9a8 b0c2 b9c7 i0h0 g6g5 b2b4 i9h9 g3g4 c6c5 a3a4 g9e7 c3c4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2" to listOf(
            "a6a7" to "车1退1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "c3c4 c6c5 c4c5 h9g7 h2e2 b9c7 h0g2 g9e7 b2b4 i9h9 b0c2 a9a8 g3g4 g6g5 a3a4 h9h8 i0h0 a8a7 g4g5 a6a5 c5c6 e6e5 e3e4 i6i5 g5g6 i5i4 a0a1 a7a6 a1a2 a6a7 a2a3 a7a6 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3" to listOf(
            "a7a6" to "车1进1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 b9c7 h0g2 c6c5 b0c2 h9g7 i0h0 i9h9 c3c4 a9a8 b2b4 g9e7 g3g4 g6g5 a3a4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2" to listOf(
            "a6a7" to "车1退1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 g3g4 c6c5 c3c4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a8a7 a4a5 e5e4 i3i4 i6i5 a0a1 h8h7 a1a2 h7h6 a2a3 h6h5 a3a4 h5h4 h0h1 h4h3 h1h2 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8" to listOf(
            "a4a3" to "车九退一",
            "h2h1" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "g0e2 c9e7 b2b4 b9c7 h0g2 h9g7 i0h0 i9h9 g3g4 c6c5 a3a4 g6g5 c3c4 a9a8 b0c2 h9h8 a4a5 e6e5 g4g5 a8a7 e3e4 i6i5 g5g6 i5i4 a0a1 a7a8 a1a2 a8a7 a2a3 a7a8 a3a4 a8a7 h0h1 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7" to listOf(
            "a3a4" to "车九进一",
            "h1h0" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 c3c4 c6c5 g3g4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a6a5 e3e4 i6i5 i3i4 a8a7 a0a1 a7a6 a1a2 h8h7 a2a3 h7h6 h0h1 h6h5 h1h2 h5h4 h2h3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7" to listOf(
            "a3a2" to "车九退一",
            "h3h2" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h9g7 h0g2 a9a8 b0c2 b9c7 i0h0 g6g5 b2b4 i9h9 g3g4 c6c5 a3a4 g9e7 c3c4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7" to listOf(
            "a2a3" to "车九进一",
            "h6h5" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "c3c4 c6c5 c4c5 h9g7 h2e2 b9c7 h0g2 g9e7 b2b4 i9h9 b0c2 a9a8 g3g4 g6g5 a3a4 h9h8 i0h0 a8a7 g4g5 a6a5 c5c6 e6e5 e3e4 i6i5 g5g6 i5i4 a0a1 a7a6 a1a2 a6a7 a2a3 a7a6 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6" to listOf(
            "a3a2" to "车九退一",
            "h6h5" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 b9c7 h0g2 c6c5 b0c2 h9g7 i0h0 i9h9 c3c4 a9a8 b2b4 g9e7 g3g4 g6g5 a3a4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7" to listOf(
            "a2a3" to "车九进一",
            "h6h5" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 g3g4 c6c5 c3c4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a8a7 a4a5 e5e4 i3i4 i6i5 a0a1 h8h7 a1a2 h7h6 a2a3 h6h5 a3a4 h5h4 h0h1 h4h3 h1h2 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3" to listOf(
            "a8a7" to "车1进1",
            "h3h4" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "g0e2 c9e7 b2b4 b9c7 h0g2 h9g7 i0h0 i9h9 g3g4 c6c5 a3a4 g6g5 c3c4 a9a8 b0c2 h9h8 a4a5 e6e5 g4g5 a8a7 e3e4 i6i5 g5g6 i5i4 a0a1 a7a8 a1a2 a8a7 a2a3 a7a8 a3a4 a8a7 h0h1 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4" to listOf(
            "a7a8" to "车1退1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 c3c4 c6c5 g3g4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a6a5 e3e4 i6i5 i3i4 a8a7 a0a1 a7a6 a1a2 h8h7 a2a3 h7h6 h0h1 h6h5 h1h2 h5h4 h2h3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2" to listOf(
            "a7a6" to "车1进1",
            "h4h5" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h9g7 h0g2 a9a8 b0c2 b9c7 i0h0 g6g5 b2b4 i9h9 g3g4 c6c5 a3a4 g9e7 c3c4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3" to listOf(
            "a7a6" to "车1进1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "c3c4 c6c5 c4c5 h9g7 h2e2 b9c7 h0g2 g9e7 b2b4 i9h9 b0c2 a9a8 g3g4 g6g5 a3a4 h9h8 i0h0 a8a7 g4g5 a6a5 c5c6 e6e5 e3e4 i6i5 g5g6 i5i4 a0a1 a7a6 a1a2 a6a7 a2a3 a7a6 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2" to listOf(
            "a6a7" to "车1退1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 b9c7 h0g2 c6c5 b0c2 h9g7 i0h0 i9h9 c3c4 a9a8 b2b4 g9e7 g3g4 g6g5 a3a4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3" to listOf(
            "a7a6" to "车1进1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 g3g4 c6c5 c3c4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a8a7 a4a5 e5e4 i3i4 i6i5 a0a1 h8h7 a1a2 h7h6 a2a3 h6h5 a3a4 h5h4 h0h1 h4h3 h1h2 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7" to listOf(
            "a3a4" to "车九进一",
            "h2h1" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "g0e2 c9e7 b2b4 b9c7 h0g2 h9g7 i0h0 i9h9 g3g4 c6c5 a3a4 g6g5 c3c4 a9a8 b0c2 h9h8 a4a5 e6e5 g4g5 a8a7 e3e4 i6i5 g5g6 i5i4 a0a1 a7a8 a1a2 a8a7 a2a3 a7a8 a3a4 a8a7 h0h1 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8" to listOf(
            "a4a3" to "车九退一",
            "h1h0" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 c3c4 c6c5 g3g4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a6a5 e3e4 i6i5 i3i4 a8a7 a0a1 a7a6 a1a2 h8h7 a2a3 h7h6 h0h1 h6h5 h1h2 h5h4 h2h3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6" to listOf(
            "a2a3" to "车九进一",
            "h3h2" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h9g7 h0g2 a9a8 b0c2 b9c7 i0h0 g6g5 b2b4 i9h9 g3g4 c6c5 a3a4 g9e7 c3c4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6" to listOf(
            "a3a2" to "车九退一",
            "h6h5" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "c3c4 c6c5 c4c5 h9g7 h2e2 b9c7 h0g2 g9e7 b2b4 i9h9 b0c2 a9a8 g3g4 g6g5 a3a4 h9h8 i0h0 a8a7 g4g5 a6a5 c5c6 e6e5 e3e4 i6i5 g5g6 i5i4 a0a1 a7a6 a1a2 a6a7 a2a3 a7a6 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7" to listOf(
            "a2a3" to "车九进一",
            "h6h5" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 b9c7 h0g2 c6c5 b0c2 h9g7 i0h0 i9h9 c3c4 a9a8 b2b4 g9e7 g3g4 g6g5 a3a4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6" to listOf(
            "a3a2" to "车九退一",
            "h6h5" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 g3g4 c6c5 c3c4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a8a7 a4a5 e5e4 i3i4 i6i5 a0a1 h8h7 a1a2 h7h6 a2a3 h6h5 a3a4 h5h4 h0h1 h4h3 h1h2 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4" to listOf(
            "a7a8" to "车1退1",
            "h3h4" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "g0e2 c9e7 b2b4 b9c7 h0g2 h9g7 i0h0 i9h9 g3g4 c6c5 a3a4 g6g5 c3c4 a9a8 b0c2 h9h8 a4a5 e6e5 g4g5 a8a7 e3e4 i6i5 g5g6 i5i4 a0a1 a7a8 a1a2 a8a7 a2a3 a7a8 a3a4 a8a7 h0h1 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3" to listOf(
            "a8a7" to "车1进1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 c3c4 c6c5 g3g4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a6a5 e3e4 i6i5 i3i4 a8a7 a0a1 a7a6 a1a2 h8h7 a2a3 h7h6 h0h1 h6h5 h1h2 h5h4 h2h3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3" to listOf(
            "a6a7" to "车1退1",
            "h4h5" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h9g7 h0g2 a9a8 b0c2 b9c7 i0h0 g6g5 b2b4 i9h9 g3g4 c6c5 a3a4 g9e7 c3c4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2" to listOf(
            "a6a7" to "车1退1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "c3c4 c6c5 c4c5 h9g7 h2e2 b9c7 h0g2 g9e7 b2b4 i9h9 b0c2 a9a8 g3g4 g6g5 a3a4 h9h8 i0h0 a8a7 g4g5 a6a5 c5c6 e6e5 e3e4 i6i5 g5g6 i5i4 a0a1 a7a6 a1a2 a6a7 a2a3 a7a6 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3" to listOf(
            "a7a6" to "车1进1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 b9c7 h0g2 c6c5 b0c2 h9g7 i0h0 i9h9 c3c4 a9a8 b2b4 g9e7 g3g4 g6g5 a3a4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2" to listOf(
            "a6a7" to "车1退1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 g3g4 c6c5 c3c4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a8a7 a4a5 e5e4 i3i4 i6i5 a0a1 h8h7 a1a2 h7h6 a2a3 h6h5 a3a4 h5h4 h0h1 h4h3 h1h2 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8" to listOf(
            "a4a3" to "车九退一",
            "h2h1" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "g0e2 c9e7 b2b4 b9c7 h0g2 h9g7 i0h0 i9h9 g3g4 c6c5 a3a4 g6g5 c3c4 a9a8 b0c2 h9h8 a4a5 e6e5 g4g5 a8a7 e3e4 i6i5 g5g6 i5i4 a0a1 a7a8 a1a2 a8a7 a2a3 a7a8 a3a4 a8a7 h0h1 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7" to listOf(
            "a3a4" to "车九进一",
            "h1h0" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 c3c4 c6c5 g3g4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a6a5 e3e4 i6i5 i3i4 a8a7 a0a1 a7a6 a1a2 h8h7 a2a3 h7h6 h0h1 h6h5 h1h2 h5h4 h2h3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7" to listOf(
            "a3a2" to "车九退一",
            "h3h2" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h9g7 h0g2 a9a8 b0c2 b9c7 i0h0 g6g5 b2b4 i9h9 g3g4 c6c5 a3a4 g9e7 c3c4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7" to listOf(
            "a2a3" to "车九进一",
            "h6h5" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "c3c4 c6c5 c4c5 h9g7 h2e2 b9c7 h0g2 g9e7 b2b4 i9h9 b0c2 a9a8 g3g4 g6g5 a3a4 h9h8 i0h0 a8a7 g4g5 a6a5 c5c6 e6e5 e3e4 i6i5 g5g6 i5i4 a0a1 a7a6 a1a2 a6a7 a2a3 a7a6 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6" to listOf(
            "a3a2" to "车九退一",
            "h6h5" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 b9c7 h0g2 c6c5 b0c2 h9g7 i0h0 i9h9 c3c4 a9a8 b2b4 g9e7 g3g4 g6g5 a3a4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7" to listOf(
            "a2a3" to "车九进一",
            "h6h5" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 g3g4 c6c5 c3c4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a8a7 a4a5 e5e4 i3i4 i6i5 a0a1 h8h7 a1a2 h7h6 a2a3 h6h5 a3a4 h5h4 h0h1 h4h3 h1h2 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3" to listOf(
            "a8a7" to "车1进1",
            "h3h4" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "g0e2 c9e7 b2b4 b9c7 h0g2 h9g7 i0h0 i9h9 g3g4 c6c5 a3a4 g6g5 c3c4 a9a8 b0c2 h9h8 a4a5 e6e5 g4g5 a8a7 e3e4 i6i5 g5g6 i5i4 a0a1 a7a8 a1a2 a8a7 a2a3 a7a8 a3a4 a8a7 h0h1 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4" to listOf(
            "a7a8" to "车1退1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 c3c4 c6c5 g3g4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a6a5 e3e4 i6i5 i3i4 a8a7 a0a1 a7a6 a1a2 h8h7 a2a3 h7h6 h0h1 h6h5 h1h2 h5h4 h2h3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2" to listOf(
            "a7a6" to "车1进1",
            "h4h5" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h9g7 h0g2 a9a8 b0c2 b9c7 i0h0 g6g5 b2b4 i9h9 g3g4 c6c5 a3a4 g9e7 c3c4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3" to listOf(
            "a7a6" to "车1进1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "c3c4 c6c5 c4c5 h9g7 h2e2 b9c7 h0g2 g9e7 b2b4 i9h9 b0c2 a9a8 g3g4 g6g5 a3a4 h9h8 i0h0 a8a7 g4g5 a6a5 c5c6 e6e5 e3e4 i6i5 g5g6 i5i4 a0a1 a7a6 a1a2 a6a7 a2a3 a7a6 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2" to listOf(
            "a6a7" to "车1退1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 b9c7 h0g2 c6c5 b0c2 h9g7 i0h0 i9h9 c3c4 a9a8 b2b4 g9e7 g3g4 g6g5 a3a4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3" to listOf(
            "a7a6" to "车1进1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 g3g4 c6c5 c3c4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a8a7 a4a5 e5e4 i3i4 i6i5 a0a1 h8h7 a1a2 h7h6 a2a3 h6h5 a3a4 h5h4 h0h1 h4h3 h1h2 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7" to listOf(
            "a3a4" to "车九进一",
            "h2h1" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "g0e2 c9e7 b2b4 b9c7 h0g2 h9g7 i0h0 i9h9 g3g4 c6c5 a3a4 g6g5 c3c4 a9a8 b0c2 h9h8 a4a5 e6e5 g4g5 a8a7 e3e4 i6i5 g5g6 i5i4 a0a1 a7a8 a1a2 a8a7 a2a3 a7a8 a3a4 a8a7 h0h1 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8" to listOf(
            "a4a3" to "车九退一",
            "h1h0" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 c3c4 c6c5 g3g4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a6a5 e3e4 i6i5 i3i4 a8a7 a0a1 a7a6 a1a2 h8h7 a2a3 h7h6 h0h1 h6h5 h1h2 h5h4 h2h3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6" to listOf(
            "a2a3" to "车九进一",
            "h3h2" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h9g7 h0g2 a9a8 b0c2 b9c7 i0h0 g6g5 b2b4 i9h9 g3g4 c6c5 a3a4 g9e7 c3c4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6" to listOf(
            "a3a2" to "车九退一",
            "h6h5" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "c3c4 c6c5 c4c5 h9g7 h2e2 b9c7 h0g2 g9e7 b2b4 i9h9 b0c2 a9a8 g3g4 g6g5 a3a4 h9h8 i0h0 a8a7 g4g5 a6a5 c5c6 e6e5 e3e4 i6i5 g5g6 i5i4 a0a1 a7a6 a1a2 a6a7 a2a3 a7a6 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7" to listOf(
            "a2a3" to "车九进一",
            "h6h5" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 b9c7 h0g2 c6c5 b0c2 h9g7 i0h0 i9h9 c3c4 a9a8 b2b4 g9e7 g3g4 g6g5 a3a4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6" to listOf(
            "a3a2" to "车九退一",
            "h6h5" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 g3g4 c6c5 c3c4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a8a7 a4a5 e5e4 i3i4 i6i5 a0a1 h8h7 a1a2 h7h6 a2a3 h6h5 a3a4 h5h4 h0h1 h4h3 h1h2 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4" to listOf(
            "a7a8" to "车1退1",
            "h3h4" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "g0e2 c9e7 b2b4 b9c7 h0g2 h9g7 i0h0 i9h9 g3g4 c6c5 a3a4 g6g5 c3c4 a9a8 b0c2 h9h8 a4a5 e6e5 g4g5 a8a7 e3e4 i6i5 g5g6 i5i4 a0a1 a7a8 a1a2 a8a7 a2a3 a7a8 a3a4 a8a7 h0h1 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3" to listOf(
            "a8a7" to "车1进1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 c3c4 c6c5 g3g4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a6a5 e3e4 i6i5 i3i4 a8a7 a0a1 a7a6 a1a2 h8h7 a2a3 h7h6 h0h1 h6h5 h1h2 h5h4 h2h3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3" to listOf(
            "a6a7" to "车1退1",
            "h4h5" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h9g7 h0g2 a9a8 b0c2 b9c7 i0h0 g6g5 b2b4 i9h9 g3g4 c6c5 a3a4 g9e7 c3c4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2" to listOf(
            "a6a7" to "车1退1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "c3c4 c6c5 c4c5 h9g7 h2e2 b9c7 h0g2 g9e7 b2b4 i9h9 b0c2 a9a8 g3g4 g6g5 a3a4 h9h8 i0h0 a8a7 g4g5 a6a5 c5c6 e6e5 e3e4 i6i5 g5g6 i5i4 a0a1 a7a6 a1a2 a6a7 a2a3 a7a6 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3" to listOf(
            "a7a6" to "车1进1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 b9c7 h0g2 c6c5 b0c2 h9g7 i0h0 i9h9 c3c4 a9a8 b2b4 g9e7 g3g4 g6g5 a3a4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2" to listOf(
            "a6a7" to "车1退1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 g3g4 c6c5 c3c4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a8a7 a4a5 e5e4 i3i4 i6i5 a0a1 h8h7 a1a2 h7h6 a2a3 h6h5 a3a4 h5h4 h0h1 h4h3 h1h2 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8" to listOf(
            "a4a3" to "车九退一",
            "h2h1" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "g0e2 c9e7 b2b4 b9c7 h0g2 h9g7 i0h0 i9h9 g3g4 c6c5 a3a4 g6g5 c3c4 a9a8 b0c2 h9h8 a4a5 e6e5 g4g5 a8a7 e3e4 i6i5 g5g6 i5i4 a0a1 a7a8 a1a2 a8a7 a2a3 a7a8 a3a4 a8a7 h0h1 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7" to listOf(
            "a3a4" to "车九进一",
            "h1h0" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 c3c4 c6c5 g3g4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a6a5 e3e4 i6i5 i3i4 a8a7 a0a1 a7a6 a1a2 h8h7 a2a3 h7h6 h0h1 h6h5 h1h2 h5h4 h2h3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7" to listOf(
            "a3a2" to "车九退一",
            "h3h2" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h9g7 h0g2 a9a8 b0c2 b9c7 i0h0 g6g5 b2b4 i9h9 g3g4 c6c5 a3a4 g9e7 c3c4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7" to listOf(
            "a2a3" to "车九进一",
            "h6h5" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "c3c4 c6c5 c4c5 h9g7 h2e2 b9c7 h0g2 g9e7 b2b4 i9h9 b0c2 a9a8 g3g4 g6g5 a3a4 h9h8 i0h0 a8a7 g4g5 a6a5 c5c6 e6e5 e3e4 i6i5 g5g6 i5i4 a0a1 a7a6 a1a2 a6a7 a2a3 a7a6 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6" to listOf(
            "a3a2" to "车九退一",
            "h6h5" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 b9c7 h0g2 c6c5 b0c2 h9g7 i0h0 i9h9 c3c4 a9a8 b2b4 g9e7 g3g4 g6g5 a3a4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7" to listOf(
            "a2a3" to "车九进一",
            "h6h5" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 g3g4 c6c5 c3c4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a8a7 a4a5 e5e4 i3i4 i6i5 a0a1 h8h7 a1a2 h7h6 a2a3 h6h5 a3a4 h5h4 h0h1 h4h3 h1h2 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3" to listOf(
            "a8a7" to "车1进1",
            "h3h4" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "g0e2 c9e7 b2b4 b9c7 h0g2 h9g7 i0h0 i9h9 g3g4 c6c5 a3a4 g6g5 c3c4 a9a8 b0c2 h9h8 a4a5 e6e5 g4g5 a8a7 e3e4 i6i5 g5g6 i5i4 a0a1 a7a8 a1a2 a8a7 a2a3 a7a8 a3a4 a8a7 h0h1 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4" to listOf(
            "a7a8" to "车1退1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 c3c4 c6c5 g3g4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a6a5 e3e4 i6i5 i3i4 a8a7 a0a1 a7a6 a1a2 h8h7 a2a3 h7h6 h0h1 h6h5 h1h2 h5h4 h2h3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2" to listOf(
            "a7a6" to "车1进1",
            "h4h5" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h9g7 h0g2 a9a8 b0c2 b9c7 i0h0 g6g5 b2b4 i9h9 g3g4 c6c5 a3a4 g9e7 c3c4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3" to listOf(
            "a7a6" to "车1进1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "c3c4 c6c5 c4c5 h9g7 h2e2 b9c7 h0g2 g9e7 b2b4 i9h9 b0c2 a9a8 g3g4 g6g5 a3a4 h9h8 i0h0 a8a7 g4g5 a6a5 c5c6 e6e5 e3e4 i6i5 g5g6 i5i4 a0a1 a7a6 a1a2 a6a7 a2a3 a7a6 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2" to listOf(
            "a6a7" to "车1退1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 b9c7 h0g2 c6c5 b0c2 h9g7 i0h0 i9h9 c3c4 a9a8 b2b4 g9e7 g3g4 g6g5 a3a4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3" to listOf(
            "a7a6" to "车1进1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 g3g4 c6c5 c3c4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a8a7 a4a5 e5e4 i3i4 i6i5 a0a1 h8h7 a1a2 h7h6 a2a3 h6h5 a3a4 h5h4 h0h1 h4h3 h1h2 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7" to listOf(
            "a3a4" to "车九进一",
            "h2h1" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "g0e2 c9e7 b2b4 b9c7 h0g2 h9g7 i0h0 i9h9 g3g4 c6c5 a3a4 g6g5 c3c4 a9a8 b0c2 h9h8 a4a5 e6e5 g4g5 a8a7 e3e4 i6i5 g5g6 i5i4 a0a1 a7a8 a1a2 a8a7 a2a3 a7a8 a3a4 a8a7 h0h1 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8" to listOf(
            "a4a3" to "车九退一",
            "h1h0" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 c3c4 c6c5 g3g4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a6a5 e3e4 i6i5 i3i4 a8a7 a0a1 a7a6 a1a2 h8h7 a2a3 h7h6 h0h1 h6h5 h1h2 h5h4 h2h3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6" to listOf(
            "a2a3" to "车九进一",
            "h3h2" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h9g7 h0g2 a9a8 b0c2 b9c7 i0h0 g6g5 b2b4 i9h9 g3g4 c6c5 a3a4 g9e7 c3c4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6" to listOf(
            "a3a2" to "车九退一",
            "h6h5" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "c3c4 c6c5 c4c5 h9g7 h2e2 b9c7 h0g2 g9e7 b2b4 i9h9 b0c2 a9a8 g3g4 g6g5 a3a4 h9h8 i0h0 a8a7 g4g5 a6a5 c5c6 e6e5 e3e4 i6i5 g5g6 i5i4 a0a1 a7a6 a1a2 a6a7 a2a3 a7a6 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7" to listOf(
            "a2a3" to "车九进一",
            "h6h5" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 b9c7 h0g2 c6c5 b0c2 h9g7 i0h0 i9h9 c3c4 a9a8 b2b4 g9e7 g3g4 g6g5 a3a4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6" to listOf(
            "a3a2" to "车九退一",
            "h6h5" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 g3g4 c6c5 c3c4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a8a7 a4a5 e5e4 i3i4 i6i5 a0a1 h8h7 a1a2 h7h6 a2a3 h6h5 a3a4 h5h4 h0h1 h4h3 h1h2 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4" to listOf(
            "a7a8" to "车1退1",
            "h3h4" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "g0e2 c9e7 b2b4 b9c7 h0g2 h9g7 i0h0 i9h9 g3g4 c6c5 a3a4 g6g5 c3c4 a9a8 b0c2 h9h8 a4a5 e6e5 g4g5 a8a7 e3e4 i6i5 g5g6 i5i4 a0a1 a7a8 a1a2 a8a7 a2a3 a7a8 a3a4 a8a7 h0h1 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3" to listOf(
            "a8a7" to "车1进1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 c3c4 c6c5 g3g4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a6a5 e3e4 i6i5 i3i4 a8a7 a0a1 a7a6 a1a2 h8h7 a2a3 h7h6 h0h1 h6h5 h1h2 h5h4 h2h3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3" to listOf(
            "a6a7" to "车1退1",
            "h4h5" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h9g7 h0g2 a9a8 b0c2 b9c7 i0h0 g6g5 b2b4 i9h9 g3g4 c6c5 a3a4 g9e7 c3c4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2" to listOf(
            "a6a7" to "车1退1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "c3c4 c6c5 c4c5 h9g7 h2e2 b9c7 h0g2 g9e7 b2b4 i9h9 b0c2 a9a8 g3g4 g6g5 a3a4 h9h8 i0h0 a8a7 g4g5 a6a5 c5c6 e6e5 e3e4 i6i5 g5g6 i5i4 a0a1 a7a6 a1a2 a6a7 a2a3 a7a6 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3" to listOf(
            "a7a6" to "车1进1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 b9c7 h0g2 c6c5 b0c2 h9g7 i0h0 i9h9 c3c4 a9a8 b2b4 g9e7 g3g4 g6g5 a3a4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2" to listOf(
            "a6a7" to "车1退1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 g3g4 c6c5 c3c4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a8a7 a4a5 e5e4 i3i4 i6i5 a0a1 h8h7 a1a2 h7h6 a2a3 h6h5 a3a4 h5h4 h0h1 h4h3 h1h2 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8" to listOf(
            "a4a3" to "车九退一",
            "h2h1" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "g0e2 c9e7 b2b4 b9c7 h0g2 h9g7 i0h0 i9h9 g3g4 c6c5 a3a4 g6g5 c3c4 a9a8 b0c2 h9h8 a4a5 e6e5 g4g5 a8a7 e3e4 i6i5 g5g6 i5i4 a0a1 a7a8 a1a2 a8a7 a2a3 a7a8 a3a4 a8a7 h0h1 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7" to listOf(
            "a3a4" to "车九进一",
            "h1h0" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 c3c4 c6c5 g3g4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a6a5 e3e4 i6i5 i3i4 a8a7 a0a1 a7a6 a1a2 h8h7 a2a3 h7h6 h0h1 h6h5 h1h2 h5h4 h2h3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7" to listOf(
            "a3a2" to "车九退一",
            "h3h2" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h9g7 h0g2 a9a8 b0c2 b9c7 i0h0 g6g5 b2b4 i9h9 g3g4 c6c5 a3a4 g9e7 c3c4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7" to listOf(
            "a2a3" to "车九进一",
            "h6h5" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "c3c4 c6c5 c4c5 h9g7 h2e2 b9c7 h0g2 g9e7 b2b4 i9h9 b0c2 a9a8 g3g4 g6g5 a3a4 h9h8 i0h0 a8a7 g4g5 a6a5 c5c6 e6e5 e3e4 i6i5 g5g6 i5i4 a0a1 a7a6 a1a2 a6a7 a2a3 a7a6 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6" to listOf(
            "a3a2" to "车九退一",
            "h6h5" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 b9c7 h0g2 c6c5 b0c2 h9g7 i0h0 i9h9 c3c4 a9a8 b2b4 g9e7 g3g4 g6g5 a3a4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7" to listOf(
            "a2a3" to "车九进一",
            "h6h5" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 g3g4 c6c5 c3c4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a8a7 a4a5 e5e4 i3i4 i6i5 a0a1 h8h7 a1a2 h7h6 a2a3 h6h5 a3a4 h5h4 h0h1 h4h3 h1h2 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3" to listOf(
            "a8a7" to "车1进1",
            "h3h4" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "g0e2 c9e7 b2b4 b9c7 h0g2 h9g7 i0h0 i9h9 g3g4 c6c5 a3a4 g6g5 c3c4 a9a8 b0c2 h9h8 a4a5 e6e5 g4g5 a8a7 e3e4 i6i5 g5g6 i5i4 a0a1 a7a8 a1a2 a8a7 a2a3 a7a8 a3a4 a8a7 h0h1 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4" to listOf(
            "a7a8" to "车1退1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 c3c4 c6c5 g3g4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a6a5 e3e4 i6i5 i3i4 a8a7 a0a1 a7a6 a1a2 h8h7 a2a3 h7h6 h0h1 h6h5 h1h2 h5h4 h2h3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2" to listOf(
            "a7a6" to "车1进1",
            "h4h5" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h9g7 h0g2 a9a8 b0c2 b9c7 i0h0 g6g5 b2b4 i9h9 g3g4 c6c5 a3a4 g9e7 c3c4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3" to listOf(
            "a7a6" to "车1进1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "c3c4 c6c5 c4c5 h9g7 h2e2 b9c7 h0g2 g9e7 b2b4 i9h9 b0c2 a9a8 g3g4 g6g5 a3a4 h9h8 i0h0 a8a7 g4g5 a6a5 c5c6 e6e5 e3e4 i6i5 g5g6 i5i4 a0a1 a7a6 a1a2 a6a7 a2a3 a7a6 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2" to listOf(
            "a6a7" to "车1退1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 b9c7 h0g2 c6c5 b0c2 h9g7 i0h0 i9h9 c3c4 a9a8 b2b4 g9e7 g3g4 g6g5 a3a4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3" to listOf(
            "a7a6" to "车1进1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 g3g4 c6c5 c3c4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a8a7 a4a5 e5e4 i3i4 i6i5 a0a1 h8h7 a1a2 h7h6 a2a3 h6h5 a3a4 h5h4 h0h1 h4h3 h1h2 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7" to listOf(
            "a3a4" to "车九进一",
            "h2h1" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "g0e2 c9e7 b2b4 b9c7 h0g2 h9g7 i0h0 i9h9 g3g4 c6c5 a3a4 g6g5 c3c4 a9a8 b0c2 h9h8 a4a5 e6e5 g4g5 a8a7 e3e4 i6i5 g5g6 i5i4 a0a1 a7a8 a1a2 a8a7 a2a3 a7a8 a3a4 a8a7 h0h1 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8" to listOf(
            "a4a3" to "车九退一",
            "h1h0" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 c3c4 c6c5 g3g4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a6a5 e3e4 i6i5 i3i4 a8a7 a0a1 a7a6 a1a2 h8h7 a2a3 h7h6 h0h1 h6h5 h1h2 h5h4 h2h3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6" to listOf(
            "a2a3" to "车九进一",
            "h3h2" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h9g7 h0g2 a9a8 b0c2 b9c7 i0h0 g6g5 b2b4 i9h9 g3g4 c6c5 a3a4 g9e7 c3c4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6" to listOf(
            "a3a2" to "车九退一",
            "h6h5" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "c3c4 c6c5 c4c5 h9g7 h2e2 b9c7 h0g2 g9e7 b2b4 i9h9 b0c2 a9a8 g3g4 g6g5 a3a4 h9h8 i0h0 a8a7 g4g5 a6a5 c5c6 e6e5 e3e4 i6i5 g5g6 i5i4 a0a1 a7a6 a1a2 a6a7 a2a3 a7a6 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7" to listOf(
            "a2a3" to "车九进一",
            "h6h5" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 b9c7 h0g2 c6c5 b0c2 h9g7 i0h0 i9h9 c3c4 a9a8 b2b4 g9e7 g3g4 g6g5 a3a4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6" to listOf(
            "a3a2" to "车九退一",
            "h6h5" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 g3g4 c6c5 c3c4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a8a7 a4a5 e5e4 i3i4 i6i5 a0a1 h8h7 a1a2 h7h6 a2a3 h6h5 a3a4 h5h4 h0h1 h4h3 h1h2 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4" to listOf(
            "a7a8" to "车1退1",
            "h3h4" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "g0e2 c9e7 b2b4 b9c7 h0g2 h9g7 i0h0 i9h9 g3g4 c6c5 a3a4 g6g5 c3c4 a9a8 b0c2 h9h8 a4a5 e6e5 g4g5 a8a7 e3e4 i6i5 g5g6 i5i4 a0a1 a7a8 a1a2 a8a7 a2a3 a7a8 a3a4 a8a7 h0h1 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3" to listOf(
            "a8a7" to "车1进1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 c3c4 c6c5 g3g4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a6a5 e3e4 i6i5 i3i4 a8a7 a0a1 a7a6 a1a2 h8h7 a2a3 h7h6 h0h1 h6h5 h1h2 h5h4 h2h3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3" to listOf(
            "a6a7" to "车1退1",
            "h4h5" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h9g7 h0g2 a9a8 b0c2 b9c7 i0h0 g6g5 b2b4 i9h9 g3g4 c6c5 a3a4 g9e7 c3c4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2" to listOf(
            "a6a7" to "车1退1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "c3c4 c6c5 c4c5 h9g7 h2e2 b9c7 h0g2 g9e7 b2b4 i9h9 b0c2 a9a8 g3g4 g6g5 a3a4 h9h8 i0h0 a8a7 g4g5 a6a5 c5c6 e6e5 e3e4 i6i5 g5g6 i5i4 a0a1 a7a6 a1a2 a6a7 a2a3 a7a6 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3" to listOf(
            "a7a6" to "车1进1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 b9c7 h0g2 c6c5 b0c2 h9g7 i0h0 i9h9 c3c4 a9a8 b2b4 g9e7 g3g4 g6g5 a3a4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2" to listOf(
            "a6a7" to "车1退1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 g3g4 c6c5 c3c4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a8a7 a4a5 e5e4 i3i4 i6i5 a0a1 h8h7 a1a2 h7h6 a2a3 h6h5 a3a4 h5h4 h0h1 h4h3 h1h2 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8" to listOf(
            "a4a3" to "车九退一",
            "h2h1" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "g0e2 c9e7 b2b4 b9c7 h0g2 h9g7 i0h0 i9h9 g3g4 c6c5 a3a4 g6g5 c3c4 a9a8 b0c2 h9h8 a4a5 e6e5 g4g5 a8a7 e3e4 i6i5 g5g6 i5i4 a0a1 a7a8 a1a2 a8a7 a2a3 a7a8 a3a4 a8a7 h0h1 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7" to listOf(
            "a3a4" to "车九进一",
            "h1h0" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 c3c4 c6c5 g3g4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a6a5 e3e4 i6i5 i3i4 a8a7 a0a1 a7a6 a1a2 h8h7 a2a3 h7h6 h0h1 h6h5 h1h2 h5h4 h2h3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7" to listOf(
            "a3a2" to "车九退一",
            "h3h2" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h9g7 h0g2 a9a8 b0c2 b9c7 i0h0 g6g5 b2b4 i9h9 g3g4 c6c5 a3a4 g9e7 c3c4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7" to listOf(
            "a2a3" to "车九进一",
            "h6h5" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "c3c4 c6c5 c4c5 h9g7 h2e2 b9c7 h0g2 g9e7 b2b4 i9h9 b0c2 a9a8 g3g4 g6g5 a3a4 h9h8 i0h0 a8a7 g4g5 a6a5 c5c6 e6e5 e3e4 i6i5 g5g6 i5i4 a0a1 a7a6 a1a2 a6a7 a2a3 a7a6 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6" to listOf(
            "a3a2" to "车九退一",
            "h6h5" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 b9c7 h0g2 c6c5 b0c2 h9g7 i0h0 i9h9 c3c4 a9a8 b2b4 g9e7 g3g4 g6g5 a3a4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7" to listOf(
            "a2a3" to "车九进一",
            "h6h5" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 g3g4 c6c5 c3c4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a8a7 a4a5 e5e4 i3i4 i6i5 a0a1 h8h7 a1a2 h7h6 a2a3 h6h5 a3a4 h5h4 h0h1 h4h3 h1h2 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3" to listOf(
            "a8a7" to "车1进1",
            "h3h4" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "g0e2 c9e7 b2b4 b9c7 h0g2 h9g7 i0h0 i9h9 g3g4 c6c5 a3a4 g6g5 c3c4 a9a8 b0c2 h9h8 a4a5 e6e5 g4g5 a8a7 e3e4 i6i5 g5g6 i5i4 a0a1 a7a8 a1a2 a8a7 a2a3 a7a8 a3a4 a8a7 h0h1 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4" to listOf(
            "a7a8" to "车1退1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 c3c4 c6c5 g3g4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a6a5 e3e4 i6i5 i3i4 a8a7 a0a1 a7a6 a1a2 h8h7 a2a3 h7h6 h0h1 h6h5 h1h2 h5h4 h2h3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2" to listOf(
            "a7a6" to "车1进1",
            "h4h5" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h9g7 h0g2 a9a8 b0c2 b9c7 i0h0 g6g5 b2b4 i9h9 g3g4 c6c5 a3a4 g9e7 c3c4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3" to listOf(
            "a7a6" to "车1进1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "c3c4 c6c5 c4c5 h9g7 h2e2 b9c7 h0g2 g9e7 b2b4 i9h9 b0c2 a9a8 g3g4 g6g5 a3a4 h9h8 i0h0 a8a7 g4g5 a6a5 c5c6 e6e5 e3e4 i6i5 g5g6 i5i4 a0a1 a7a6 a1a2 a6a7 a2a3 a7a6 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2" to listOf(
            "a6a7" to "车1退1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 b9c7 h0g2 c6c5 b0c2 h9g7 i0h0 i9h9 c3c4 a9a8 b2b4 g9e7 g3g4 g6g5 a3a4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3" to listOf(
            "a7a6" to "车1进1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 g3g4 c6c5 c3c4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a8a7 a4a5 e5e4 i3i4 i6i5 a0a1 h8h7 a1a2 h7h6 a2a3 h6h5 a3a4 h5h4 h0h1 h4h3 h1h2 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7" to listOf(
            "a3a4" to "车九进一",
            "h2h1" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "g0e2 c9e7 b2b4 b9c7 h0g2 h9g7 i0h0 i9h9 g3g4 c6c5 a3a4 g6g5 c3c4 a9a8 b0c2 h9h8 a4a5 e6e5 g4g5 a8a7 e3e4 i6i5 g5g6 i5i4 a0a1 a7a8 a1a2 a8a7 a2a3 a7a8 a3a4 a8a7 h0h1 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8" to listOf(
            "a4a3" to "车九退一",
            "h1h0" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 c3c4 c6c5 g3g4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a6a5 e3e4 i6i5 i3i4 a8a7 a0a1 a7a6 a1a2 h8h7 a2a3 h7h6 h0h1 h6h5 h1h2 h5h4 h2h3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6" to listOf(
            "a2a3" to "车九进一",
            "h3h2" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h9g7 h0g2 a9a8 b0c2 b9c7 i0h0 g6g5 b2b4 i9h9 g3g4 c6c5 a3a4 g9e7 c3c4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6" to listOf(
            "a3a2" to "车九退一",
            "h6h5" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "c3c4 c6c5 c4c5 h9g7 h2e2 b9c7 h0g2 g9e7 b2b4 i9h9 b0c2 a9a8 g3g4 g6g5 a3a4 h9h8 i0h0 a8a7 g4g5 a6a5 c5c6 e6e5 e3e4 i6i5 g5g6 i5i4 a0a1 a7a6 a1a2 a6a7 a2a3 a7a6 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7" to listOf(
            "a2a3" to "车九进一",
            "h6h5" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 b9c7 h0g2 c6c5 b0c2 h9g7 i0h0 i9h9 c3c4 a9a8 b2b4 g9e7 g3g4 g6g5 a3a4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6" to listOf(
            "a3a2" to "车九退一",
            "h6h5" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 g3g4 c6c5 c3c4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a8a7 a4a5 e5e4 i3i4 i6i5 a0a1 h8h7 a1a2 h7h6 a2a3 h6h5 a3a4 h5h4 h0h1 h4h3 h1h2 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4" to listOf(
            "a7a8" to "车1退1",
            "h3h4" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "g0e2 c9e7 b2b4 b9c7 h0g2 h9g7 i0h0 i9h9 g3g4 c6c5 a3a4 g6g5 c3c4 a9a8 b0c2 h9h8 a4a5 e6e5 g4g5 a8a7 e3e4 i6i5 g5g6 i5i4 a0a1 a7a8 a1a2 a8a7 a2a3 a7a8 a3a4 a8a7 h0h1 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3" to listOf(
            "a8a7" to "车1进1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 c3c4 c6c5 g3g4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a6a5 e3e4 i6i5 i3i4 a8a7 a0a1 a7a6 a1a2 h8h7 a2a3 h7h6 h0h1 h6h5 h1h2 h5h4 h2h3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3" to listOf(
            "a6a7" to "车1退1",
            "h4h5" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h9g7 h0g2 a9a8 b0c2 b9c7 i0h0 g6g5 b2b4 i9h9 g3g4 c6c5 a3a4 g9e7 c3c4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2" to listOf(
            "a6a7" to "车1退1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "c3c4 c6c5 c4c5 h9g7 h2e2 b9c7 h0g2 g9e7 b2b4 i9h9 b0c2 a9a8 g3g4 g6g5 a3a4 h9h8 i0h0 a8a7 g4g5 a6a5 c5c6 e6e5 e3e4 i6i5 g5g6 i5i4 a0a1 a7a6 a1a2 a6a7 a2a3 a7a6 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3" to listOf(
            "a7a6" to "车1进1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 b9c7 h0g2 c6c5 b0c2 h9g7 i0h0 i9h9 c3c4 a9a8 b2b4 g9e7 g3g4 g6g5 a3a4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2" to listOf(
            "a6a7" to "车1退1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 g3g4 c6c5 c3c4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a8a7 a4a5 e5e4 i3i4 i6i5 a0a1 h8h7 a1a2 h7h6 a2a3 h6h5 a3a4 h5h4 h0h1 h4h3 h1h2 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8" to listOf(
            "a4a3" to "车九退一",
            "h2h1" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "g0e2 c9e7 b2b4 b9c7 h0g2 h9g7 i0h0 i9h9 g3g4 c6c5 a3a4 g6g5 c3c4 a9a8 b0c2 h9h8 a4a5 e6e5 g4g5 a8a7 e3e4 i6i5 g5g6 i5i4 a0a1 a7a8 a1a2 a8a7 a2a3 a7a8 a3a4 a8a7 h0h1 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7" to listOf(
            "a3a4" to "车九进一",
            "h1h0" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 c3c4 c6c5 g3g4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a6a5 e3e4 i6i5 i3i4 a8a7 a0a1 a7a6 a1a2 h8h7 a2a3 h7h6 h0h1 h6h5 h1h2 h5h4 h2h3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7" to listOf(
            "a3a2" to "车九退一",
            "h3h2" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h9g7 h0g2 a9a8 b0c2 b9c7 i0h0 g6g5 b2b4 i9h9 g3g4 c6c5 a3a4 g9e7 c3c4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7" to listOf(
            "a2a3" to "车九进一",
            "h6h5" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "c3c4 c6c5 c4c5 h9g7 h2e2 b9c7 h0g2 g9e7 b2b4 i9h9 b0c2 a9a8 g3g4 g6g5 a3a4 h9h8 i0h0 a8a7 g4g5 a6a5 c5c6 e6e5 e3e4 i6i5 g5g6 i5i4 a0a1 a7a6 a1a2 a6a7 a2a3 a7a6 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6" to listOf(
            "a3a2" to "车九退一",
            "h6h5" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 b9c7 h0g2 c6c5 b0c2 h9g7 i0h0 i9h9 c3c4 a9a8 b2b4 g9e7 g3g4 g6g5 a3a4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7" to listOf(
            "a2a3" to "车九进一",
            "h6h5" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 g3g4 c6c5 c3c4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a8a7 a4a5 e5e4 i3i4 i6i5 a0a1 h8h7 a1a2 h7h6 a2a3 h6h5 a3a4 h5h4 h0h1 h4h3 h1h2 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3" to listOf(
            "a8a7" to "车1进1",
            "h3h4" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "g0e2 c9e7 b2b4 b9c7 h0g2 h9g7 i0h0 i9h9 g3g4 c6c5 a3a4 g6g5 c3c4 a9a8 b0c2 h9h8 a4a5 e6e5 g4g5 a8a7 e3e4 i6i5 g5g6 i5i4 a0a1 a7a8 a1a2 a8a7 a2a3 a7a8 a3a4 a8a7 h0h1 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4" to listOf(
            "a7a8" to "车1退1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 c3c4 c6c5 g3g4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a6a5 e3e4 i6i5 i3i4 a8a7 a0a1 a7a6 a1a2 h8h7 a2a3 h7h6 h0h1 h6h5 h1h2 h5h4 h2h3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2" to listOf(
            "a7a6" to "车1进1",
            "h4h5" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h9g7 h0g2 a9a8 b0c2 b9c7 i0h0 g6g5 b2b4 i9h9 g3g4 c6c5 a3a4 g9e7 c3c4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3" to listOf(
            "a7a6" to "车1进1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "c3c4 c6c5 c4c5 h9g7 h2e2 b9c7 h0g2 g9e7 b2b4 i9h9 b0c2 a9a8 g3g4 g6g5 a3a4 h9h8 i0h0 a8a7 g4g5 a6a5 c5c6 e6e5 e3e4 i6i5 g5g6 i5i4 a0a1 a7a6 a1a2 a6a7 a2a3 a7a6 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2" to listOf(
            "a6a7" to "车1退1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 b9c7 h0g2 c6c5 b0c2 h9g7 i0h0 i9h9 c3c4 a9a8 b2b4 g9e7 g3g4 g6g5 a3a4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3" to listOf(
            "a7a6" to "车1进1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 g3g4 c6c5 c3c4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a8a7 a4a5 e5e4 i3i4 i6i5 a0a1 h8h7 a1a2 h7h6 a2a3 h6h5 a3a4 h5h4 h0h1 h4h3 h1h2 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7" to listOf(
            "a3a4" to "车九进一",
            "h2h1" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "g0e2 c9e7 b2b4 b9c7 h0g2 h9g7 i0h0 i9h9 g3g4 c6c5 a3a4 g6g5 c3c4 a9a8 b0c2 h9h8 a4a5 e6e5 g4g5 a8a7 e3e4 i6i5 g5g6 i5i4 a0a1 a7a8 a1a2 a8a7 a2a3 a7a8 a3a4 a8a7 h0h1 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8" to listOf(
            "a4a3" to "车九退一",
            "h1h0" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 c3c4 c6c5 g3g4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a6a5 e3e4 i6i5 i3i4 a8a7 a0a1 a7a6 a1a2 h8h7 a2a3 h7h6 h0h1 h6h5 h1h2 h5h4 h2h3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6" to listOf(
            "a2a3" to "车九进一",
            "h3h2" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h9g7 h0g2 a9a8 b0c2 b9c7 i0h0 g6g5 b2b4 i9h9 g3g4 c6c5 a3a4 g9e7 c3c4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6" to listOf(
            "a3a2" to "车九退一",
            "h6h5" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "c3c4 c6c5 c4c5 h9g7 h2e2 b9c7 h0g2 g9e7 b2b4 i9h9 b0c2 a9a8 g3g4 g6g5 a3a4 h9h8 i0h0 a8a7 g4g5 a6a5 c5c6 e6e5 e3e4 i6i5 g5g6 i5i4 a0a1 a7a6 a1a2 a6a7 a2a3 a7a6 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7" to listOf(
            "a2a3" to "车九进一",
            "h6h5" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 b9c7 h0g2 c6c5 b0c2 h9g7 i0h0 i9h9 c3c4 a9a8 b2b4 g9e7 g3g4 g6g5 a3a4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6" to listOf(
            "a3a2" to "车九退一",
            "h6h5" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 g3g4 c6c5 c3c4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a8a7 a4a5 e5e4 i3i4 i6i5 a0a1 h8h7 a1a2 h7h6 a2a3 h6h5 a3a4 h5h4 h0h1 h4h3 h1h2 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4" to listOf(
            "a7a8" to "车1退1",
            "h3h4" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "g0e2 c9e7 b2b4 b9c7 h0g2 h9g7 i0h0 i9h9 g3g4 c6c5 a3a4 g6g5 c3c4 a9a8 b0c2 h9h8 a4a5 e6e5 g4g5 a8a7 e3e4 i6i5 g5g6 i5i4 a0a1 a7a8 a1a2 a8a7 a2a3 a7a8 a3a4 a8a7 h0h1 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3" to listOf(
            "a8a7" to "车1进1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 c3c4 c6c5 g3g4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a6a5 e3e4 i6i5 i3i4 a8a7 a0a1 a7a6 a1a2 h8h7 a2a3 h7h6 h0h1 h6h5 h1h2 h5h4 h2h3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3" to listOf(
            "a6a7" to "车1退1",
            "h4h5" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h9g7 h0g2 a9a8 b0c2 b9c7 i0h0 g6g5 b2b4 i9h9 g3g4 c6c5 a3a4 g9e7 c3c4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2" to listOf(
            "a6a7" to "车1退1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "c3c4 c6c5 c4c5 h9g7 h2e2 b9c7 h0g2 g9e7 b2b4 i9h9 b0c2 a9a8 g3g4 g6g5 a3a4 h9h8 i0h0 a8a7 g4g5 a6a5 c5c6 e6e5 e3e4 i6i5 g5g6 i5i4 a0a1 a7a6 a1a2 a6a7 a2a3 a7a6 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3" to listOf(
            "a7a6" to "车1进1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 b9c7 h0g2 c6c5 b0c2 h9g7 i0h0 i9h9 c3c4 a9a8 b2b4 g9e7 g3g4 g6g5 a3a4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2" to listOf(
            "a6a7" to "车1退1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 g3g4 c6c5 c3c4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a8a7 a4a5 e5e4 i3i4 i6i5 a0a1 h8h7 a1a2 h7h6 a2a3 h6h5 a3a4 h5h4 h0h1 h4h3 h1h2 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8" to listOf(
            "a4a3" to "车九退一",
            "h2h1" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "g0e2 c9e7 b2b4 b9c7 h0g2 h9g7 i0h0 i9h9 g3g4 c6c5 a3a4 g6g5 c3c4 a9a8 b0c2 h9h8 a4a5 e6e5 g4g5 a8a7 e3e4 i6i5 g5g6 i5i4 a0a1 a7a8 a1a2 a8a7 a2a3 a7a8 a3a4 a8a7 h0h1 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7" to listOf(
            "a3a4" to "车九进一",
            "h1h0" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 c3c4 c6c5 g3g4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a6a5 e3e4 i6i5 i3i4 a8a7 a0a1 a7a6 a1a2 h8h7 a2a3 h7h6 h0h1 h6h5 h1h2 h5h4 h2h3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7" to listOf(
            "a3a2" to "车九退一",
            "h3h2" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h9g7 h0g2 a9a8 b0c2 b9c7 i0h0 g6g5 b2b4 i9h9 g3g4 c6c5 a3a4 g9e7 c3c4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7" to listOf(
            "a2a3" to "车九进一",
            "h6h5" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "c3c4 c6c5 c4c5 h9g7 h2e2 b9c7 h0g2 g9e7 b2b4 i9h9 b0c2 a9a8 g3g4 g6g5 a3a4 h9h8 i0h0 a8a7 g4g5 a6a5 c5c6 e6e5 e3e4 i6i5 g5g6 i5i4 a0a1 a7a6 a1a2 a6a7 a2a3 a7a6 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6" to listOf(
            "a3a2" to "车九退一",
            "h6h5" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 b9c7 h0g2 c6c5 b0c2 h9g7 i0h0 i9h9 c3c4 a9a8 b2b4 g9e7 g3g4 g6g5 a3a4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7" to listOf(
            "a2a3" to "车九进一",
            "h6h5" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 g3g4 c6c5 c3c4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a8a7 a4a5 e5e4 i3i4 i6i5 a0a1 h8h7 a1a2 h7h6 a2a3 h6h5 a3a4 h5h4 h0h1 h4h3 h1h2 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3" to listOf(
            "a8a7" to "车1进1",
            "h3h4" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "g0e2 c9e7 b2b4 b9c7 h0g2 h9g7 i0h0 i9h9 g3g4 c6c5 a3a4 g6g5 c3c4 a9a8 b0c2 h9h8 a4a5 e6e5 g4g5 a8a7 e3e4 i6i5 g5g6 i5i4 a0a1 a7a8 a1a2 a8a7 a2a3 a7a8 a3a4 a8a7 h0h1 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4" to listOf(
            "a7a8" to "车1退1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 c3c4 c6c5 g3g4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a6a5 e3e4 i6i5 i3i4 a8a7 a0a1 a7a6 a1a2 h8h7 a2a3 h7h6 h0h1 h6h5 h1h2 h5h4 h2h3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2" to listOf(
            "a7a6" to "车1进1",
            "h4h5" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h9g7 h0g2 a9a8 b0c2 b9c7 i0h0 g6g5 b2b4 i9h9 g3g4 c6c5 a3a4 g9e7 c3c4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3" to listOf(
            "a7a6" to "车1进1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "c3c4 c6c5 c4c5 h9g7 h2e2 b9c7 h0g2 g9e7 b2b4 i9h9 b0c2 a9a8 g3g4 g6g5 a3a4 h9h8 i0h0 a8a7 g4g5 a6a5 c5c6 e6e5 e3e4 i6i5 g5g6 i5i4 a0a1 a7a6 a1a2 a6a7 a2a3 a7a6 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2" to listOf(
            "a6a7" to "车1退1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 b9c7 h0g2 c6c5 b0c2 h9g7 i0h0 i9h9 c3c4 a9a8 b2b4 g9e7 g3g4 g6g5 a3a4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3" to listOf(
            "a7a6" to "车1进1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 g3g4 c6c5 c3c4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a8a7 a4a5 e5e4 i3i4 i6i5 a0a1 h8h7 a1a2 h7h6 a2a3 h6h5 a3a4 h5h4 h0h1 h4h3 h1h2 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7" to listOf(
            "a3a4" to "车九进一",
            "h2h1" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "g0e2 c9e7 b2b4 b9c7 h0g2 h9g7 i0h0 i9h9 g3g4 c6c5 a3a4 g6g5 c3c4 a9a8 b0c2 h9h8 a4a5 e6e5 g4g5 a8a7 e3e4 i6i5 g5g6 i5i4 a0a1 a7a8 a1a2 a8a7 a2a3 a7a8 a3a4 a8a7 h0h1 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8" to listOf(
            "a4a3" to "车九退一",
            "h1h0" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 c3c4 c6c5 g3g4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a6a5 e3e4 i6i5 i3i4 a8a7 a0a1 a7a6 a1a2 h8h7 a2a3 h7h6 h0h1 h6h5 h1h2 h5h4 h2h3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6" to listOf(
            "a2a3" to "车九进一",
            "h3h2" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h9g7 h0g2 a9a8 b0c2 b9c7 i0h0 g6g5 b2b4 i9h9 g3g4 c6c5 a3a4 g9e7 c3c4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6" to listOf(
            "a3a2" to "车九退一",
            "h6h5" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "c3c4 c6c5 c4c5 h9g7 h2e2 b9c7 h0g2 g9e7 b2b4 i9h9 b0c2 a9a8 g3g4 g6g5 a3a4 h9h8 i0h0 a8a7 g4g5 a6a5 c5c6 e6e5 e3e4 i6i5 g5g6 i5i4 a0a1 a7a6 a1a2 a6a7 a2a3 a7a6 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7" to listOf(
            "a2a3" to "车九进一",
            "h6h5" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 b9c7 h0g2 c6c5 b0c2 h9g7 i0h0 i9h9 c3c4 a9a8 b2b4 g9e7 g3g4 g6g5 a3a4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6" to listOf(
            "a3a2" to "车九退一",
            "h6h5" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 g3g4 c6c5 c3c4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a8a7 a4a5 e5e4 i3i4 i6i5 a0a1 h8h7 a1a2 h7h6 a2a3 h6h5 a3a4 h5h4 h0h1 h4h3 h1h2 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4" to listOf(
            "a7a8" to "车1退1",
            "h3h4" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "g0e2 c9e7 b2b4 b9c7 h0g2 h9g7 i0h0 i9h9 g3g4 c6c5 a3a4 g6g5 c3c4 a9a8 b0c2 h9h8 a4a5 e6e5 g4g5 a8a7 e3e4 i6i5 g5g6 i5i4 a0a1 a7a8 a1a2 a8a7 a2a3 a7a8 a3a4 a8a7 h0h1 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3" to listOf(
            "a8a7" to "车1进1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 c3c4 c6c5 g3g4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a6a5 e3e4 i6i5 i3i4 a8a7 a0a1 a7a6 a1a2 h8h7 a2a3 h7h6 h0h1 h6h5 h1h2 h5h4 h2h3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3" to listOf(
            "a6a7" to "车1退1",
            "h4h5" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h9g7 h0g2 a9a8 b0c2 b9c7 i0h0 g6g5 b2b4 i9h9 g3g4 c6c5 a3a4 g9e7 c3c4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2" to listOf(
            "a6a7" to "车1退1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "c3c4 c6c5 c4c5 h9g7 h2e2 b9c7 h0g2 g9e7 b2b4 i9h9 b0c2 a9a8 g3g4 g6g5 a3a4 h9h8 i0h0 a8a7 g4g5 a6a5 c5c6 e6e5 e3e4 i6i5 g5g6 i5i4 a0a1 a7a6 a1a2 a6a7 a2a3 a7a6 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3" to listOf(
            "a7a6" to "车1进1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 b9c7 h0g2 c6c5 b0c2 h9g7 i0h0 i9h9 c3c4 a9a8 b2b4 g9e7 g3g4 g6g5 a3a4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2" to listOf(
            "a6a7" to "车1退1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 g3g4 c6c5 c3c4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a8a7 a4a5 e5e4 i3i4 i6i5 a0a1 h8h7 a1a2 h7h6 a2a3 h6h5 a3a4 h5h4 h0h1 h4h3 h1h2 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8" to listOf(
            "a4a3" to "车九退一",
            "h2h1" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "g0e2 c9e7 b2b4 b9c7 h0g2 h9g7 i0h0 i9h9 g3g4 c6c5 a3a4 g6g5 c3c4 a9a8 b0c2 h9h8 a4a5 e6e5 g4g5 a8a7 e3e4 i6i5 g5g6 i5i4 a0a1 a7a8 a1a2 a8a7 a2a3 a7a8 a3a4 a8a7 h0h1 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7" to listOf(
            "a3a4" to "车九进一",
            "h1h0" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 c3c4 c6c5 g3g4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a6a5 e3e4 i6i5 i3i4 a8a7 a0a1 a7a6 a1a2 h8h7 a2a3 h7h6 h0h1 h6h5 h1h2 h5h4 h2h3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7" to listOf(
            "a3a2" to "车九退一",
            "h3h2" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h9g7 h0g2 a9a8 b0c2 b9c7 i0h0 g6g5 b2b4 i9h9 g3g4 c6c5 a3a4 g9e7 c3c4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7" to listOf(
            "a2a3" to "车九进一",
            "h6h5" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "c3c4 c6c5 c4c5 h9g7 h2e2 b9c7 h0g2 g9e7 b2b4 i9h9 b0c2 a9a8 g3g4 g6g5 a3a4 h9h8 i0h0 a8a7 g4g5 a6a5 c5c6 e6e5 e3e4 i6i5 g5g6 i5i4 a0a1 a7a6 a1a2 a6a7 a2a3 a7a6 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6" to listOf(
            "a3a2" to "车九退一",
            "h6h5" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 b9c7 h0g2 c6c5 b0c2 h9g7 i0h0 i9h9 c3c4 a9a8 b2b4 g9e7 g3g4 g6g5 a3a4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7" to listOf(
            "a2a3" to "车九进一",
            "h6h5" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 g3g4 c6c5 c3c4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a8a7 a4a5 e5e4 i3i4 i6i5 a0a1 h8h7 a1a2 h7h6 a2a3 h6h5 a3a4 h5h4 h0h1 h4h3 h1h2 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3" to listOf(
            "a8a7" to "车1进1",
            "h3h4" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "g0e2 c9e7 b2b4 b9c7 h0g2 h9g7 i0h0 i9h9 g3g4 c6c5 a3a4 g6g5 c3c4 a9a8 b0c2 h9h8 a4a5 e6e5 g4g5 a8a7 e3e4 i6i5 g5g6 i5i4 a0a1 a7a8 a1a2 a8a7 a2a3 a7a8 a3a4 a8a7 h0h1 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4" to listOf(
            "a7a8" to "车1退1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 c3c4 c6c5 g3g4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a6a5 e3e4 i6i5 i3i4 a8a7 a0a1 a7a6 a1a2 h8h7 a2a3 h7h6 h0h1 h6h5 h1h2 h5h4 h2h3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2" to listOf(
            "a7a6" to "车1进1",
            "h4h5" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h9g7 h0g2 a9a8 b0c2 b9c7 i0h0 g6g5 b2b4 i9h9 g3g4 c6c5 a3a4 g9e7 c3c4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3" to listOf(
            "a7a6" to "车1进1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "c3c4 c6c5 c4c5 h9g7 h2e2 b9c7 h0g2 g9e7 b2b4 i9h9 b0c2 a9a8 g3g4 g6g5 a3a4 h9h8 i0h0 a8a7 g4g5 a6a5 c5c6 e6e5 e3e4 i6i5 g5g6 i5i4 a0a1 a7a6 a1a2 a6a7 a2a3 a7a6 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2" to listOf(
            "a6a7" to "车1退1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 b9c7 h0g2 c6c5 b0c2 h9g7 i0h0 i9h9 c3c4 a9a8 b2b4 g9e7 g3g4 g6g5 a3a4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3" to listOf(
            "a7a6" to "车1进1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 g3g4 c6c5 c3c4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a8a7 a4a5 e5e4 i3i4 i6i5 a0a1 h8h7 a1a2 h7h6 a2a3 h6h5 a3a4 h5h4 h0h1 h4h3 h1h2 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7" to listOf(
            "a3a4" to "车九进一",
            "h2h1" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "g0e2 c9e7 b2b4 b9c7 h0g2 h9g7 i0h0 i9h9 g3g4 c6c5 a3a4 g6g5 c3c4 a9a8 b0c2 h9h8 a4a5 e6e5 g4g5 a8a7 e3e4 i6i5 g5g6 i5i4 a0a1 a7a8 a1a2 a8a7 a2a3 a7a8 a3a4 a8a7 h0h1 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8" to listOf(
            "a4a3" to "车九退一",
            "h1h0" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 c3c4 c6c5 g3g4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a6a5 e3e4 i6i5 i3i4 a8a7 a0a1 a7a6 a1a2 h8h7 a2a3 h7h6 h0h1 h6h5 h1h2 h5h4 h2h3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6" to listOf(
            "a2a3" to "车九进一",
            "h3h2" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h9g7 h0g2 a9a8 b0c2 b9c7 i0h0 g6g5 b2b4 i9h9 g3g4 c6c5 a3a4 g9e7 c3c4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6" to listOf(
            "a3a2" to "车九退一",
            "h6h5" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "c3c4 c6c5 c4c5 h9g7 h2e2 b9c7 h0g2 g9e7 b2b4 i9h9 b0c2 a9a8 g3g4 g6g5 a3a4 h9h8 i0h0 a8a7 g4g5 a6a5 c5c6 e6e5 e3e4 i6i5 g5g6 i5i4 a0a1 a7a6 a1a2 a6a7 a2a3 a7a6 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7" to listOf(
            "a2a3" to "车九进一",
            "h6h5" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 b9c7 h0g2 c6c5 b0c2 h9g7 i0h0 i9h9 c3c4 a9a8 b2b4 g9e7 g3g4 g6g5 a3a4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6" to listOf(
            "a3a2" to "车九退一",
            "h6h5" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 g3g4 c6c5 c3c4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a8a7 a4a5 e5e4 i3i4 i6i5 a0a1 h8h7 a1a2 h7h6 a2a3 h6h5 a3a4 h5h4 h0h1 h4h3 h1h2 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4" to listOf(
            "a7a8" to "车1退1",
            "h3h4" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "g0e2 c9e7 b2b4 b9c7 h0g2 h9g7 i0h0 i9h9 g3g4 c6c5 a3a4 g6g5 c3c4 a9a8 b0c2 h9h8 a4a5 e6e5 g4g5 a8a7 e3e4 i6i5 g5g6 i5i4 a0a1 a7a8 a1a2 a8a7 a2a3 a7a8 a3a4 a8a7 h0h1 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3" to listOf(
            "a8a7" to "车1进1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 c3c4 c6c5 g3g4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a6a5 e3e4 i6i5 i3i4 a8a7 a0a1 a7a6 a1a2 h8h7 a2a3 h7h6 h0h1 h6h5 h1h2 h5h4 h2h3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3" to listOf(
            "a6a7" to "车1退1",
            "h4h5" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h9g7 h0g2 a9a8 b0c2 b9c7 i0h0 g6g5 b2b4 i9h9 g3g4 c6c5 a3a4 g9e7 c3c4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2" to listOf(
            "a6a7" to "车1退1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "c3c4 c6c5 c4c5 h9g7 h2e2 b9c7 h0g2 g9e7 b2b4 i9h9 b0c2 a9a8 g3g4 g6g5 a3a4 h9h8 i0h0 a8a7 g4g5 a6a5 c5c6 e6e5 e3e4 i6i5 g5g6 i5i4 a0a1 a7a6 a1a2 a6a7 a2a3 a7a6 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3" to listOf(
            "a7a6" to "车1进1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 b9c7 h0g2 c6c5 b0c2 h9g7 i0h0 i9h9 c3c4 a9a8 b2b4 g9e7 g3g4 g6g5 a3a4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2" to listOf(
            "a6a7" to "车1退1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 g3g4 c6c5 c3c4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a8a7 a4a5 e5e4 i3i4 i6i5 a0a1 h8h7 a1a2 h7h6 a2a3 h6h5 a3a4 h5h4 h0h1 h4h3 h1h2 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8" to listOf(
            "a4a3" to "车九退一",
            "h2h1" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "g0e2 c9e7 b2b4 b9c7 h0g2 h9g7 i0h0 i9h9 g3g4 c6c5 a3a4 g6g5 c3c4 a9a8 b0c2 h9h8 a4a5 e6e5 g4g5 a8a7 e3e4 i6i5 g5g6 i5i4 a0a1 a7a8 a1a2 a8a7 a2a3 a7a8 a3a4 a8a7 h0h1 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7" to listOf(
            "a3a4" to "车九进一",
            "h1h0" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 c3c4 c6c5 g3g4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a6a5 e3e4 i6i5 i3i4 a8a7 a0a1 a7a6 a1a2 h8h7 a2a3 h7h6 h0h1 h6h5 h1h2 h5h4 h2h3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7" to listOf(
            "a3a2" to "车九退一",
            "h3h2" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h9g7 h0g2 a9a8 b0c2 b9c7 i0h0 g6g5 b2b4 i9h9 g3g4 c6c5 a3a4 g9e7 c3c4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7" to listOf(
            "a2a3" to "车九进一",
            "h6h5" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "c3c4 c6c5 c4c5 h9g7 h2e2 b9c7 h0g2 g9e7 b2b4 i9h9 b0c2 a9a8 g3g4 g6g5 a3a4 h9h8 i0h0 a8a7 g4g5 a6a5 c5c6 e6e5 e3e4 i6i5 g5g6 i5i4 a0a1 a7a6 a1a2 a6a7 a2a3 a7a6 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6" to listOf(
            "a3a2" to "车九退一",
            "h6h5" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 b9c7 h0g2 c6c5 b0c2 h9g7 i0h0 i9h9 c3c4 a9a8 b2b4 g9e7 g3g4 g6g5 a3a4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7" to listOf(
            "a2a3" to "车九进一",
            "h6h5" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 g3g4 c6c5 c3c4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a8a7 a4a5 e5e4 i3i4 i6i5 a0a1 h8h7 a1a2 h7h6 a2a3 h6h5 a3a4 h5h4 h0h1 h4h3 h1h2 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3" to listOf(
            "a8a7" to "车1进1",
            "h3h4" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "g0e2 c9e7 b2b4 b9c7 h0g2 h9g7 i0h0 i9h9 g3g4 c6c5 a3a4 g6g5 c3c4 a9a8 b0c2 h9h8 a4a5 e6e5 g4g5 a8a7 e3e4 i6i5 g5g6 i5i4 a0a1 a7a8 a1a2 a8a7 a2a3 a7a8 a3a4 a8a7 h0h1 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4" to listOf(
            "a7a8" to "车1退1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 c3c4 c6c5 g3g4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a6a5 e3e4 i6i5 i3i4 a8a7 a0a1 a7a6 a1a2 h8h7 a2a3 h7h6 h0h1 h6h5 h1h2 h5h4 h2h3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2" to listOf(
            "a7a6" to "车1进1",
            "h4h5" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h9g7 h0g2 a9a8 b0c2 b9c7 i0h0 g6g5 b2b4 i9h9 g3g4 c6c5 a3a4 g9e7 c3c4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3" to listOf(
            "a7a6" to "车1进1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "c3c4 c6c5 c4c5 h9g7 h2e2 b9c7 h0g2 g9e7 b2b4 i9h9 b0c2 a9a8 g3g4 g6g5 a3a4 h9h8 i0h0 a8a7 g4g5 a6a5 c5c6 e6e5 e3e4 i6i5 g5g6 i5i4 a0a1 a7a6 a1a2 a6a7 a2a3 a7a6 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2" to listOf(
            "a6a7" to "车1退1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 b9c7 h0g2 c6c5 b0c2 h9g7 i0h0 i9h9 c3c4 a9a8 b2b4 g9e7 g3g4 g6g5 a3a4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3" to listOf(
            "a7a6" to "车1进1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 g3g4 c6c5 c3c4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a8a7 a4a5 e5e4 i3i4 i6i5 a0a1 h8h7 a1a2 h7h6 a2a3 h6h5 a3a4 h5h4 h0h1 h4h3 h1h2 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7" to listOf(
            "a3a4" to "车九进一",
            "h2h1" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "g0e2 c9e7 b2b4 b9c7 h0g2 h9g7 i0h0 i9h9 g3g4 c6c5 a3a4 g6g5 c3c4 a9a8 b0c2 h9h8 a4a5 e6e5 g4g5 a8a7 e3e4 i6i5 g5g6 i5i4 a0a1 a7a8 a1a2 a8a7 a2a3 a7a8 a3a4 a8a7 h0h1 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8" to listOf(
            "a4a3" to "车九退一",
            "h1h0" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 c3c4 c6c5 g3g4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a6a5 e3e4 i6i5 i3i4 a8a7 a0a1 a7a6 a1a2 h8h7 a2a3 h7h6 h0h1 h6h5 h1h2 h5h4 h2h3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6" to listOf(
            "a2a3" to "车九进一",
            "h3h2" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h9g7 h0g2 a9a8 b0c2 b9c7 i0h0 g6g5 b2b4 i9h9 g3g4 c6c5 a3a4 g9e7 c3c4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6" to listOf(
            "a3a2" to "车九退一",
            "h6h5" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "c3c4 c6c5 c4c5 h9g7 h2e2 b9c7 h0g2 g9e7 b2b4 i9h9 b0c2 a9a8 g3g4 g6g5 a3a4 h9h8 i0h0 a8a7 g4g5 a6a5 c5c6 e6e5 e3e4 i6i5 g5g6 i5i4 a0a1 a7a6 a1a2 a6a7 a2a3 a7a6 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7" to listOf(
            "a2a3" to "车九进一",
            "h6h5" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 b9c7 h0g2 c6c5 b0c2 h9g7 i0h0 i9h9 c3c4 a9a8 b2b4 g9e7 g3g4 g6g5 a3a4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6" to listOf(
            "a3a2" to "车九退一",
            "h6h5" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 g3g4 c6c5 c3c4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a8a7 a4a5 e5e4 i3i4 i6i5 a0a1 h8h7 a1a2 h7h6 a2a3 h6h5 a3a4 h5h4 h0h1 h4h3 h1h2 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4" to listOf(
            "a7a8" to "车1退1",
            "h3h4" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "g0e2 c9e7 b2b4 b9c7 h0g2 h9g7 i0h0 i9h9 g3g4 c6c5 a3a4 g6g5 c3c4 a9a8 b0c2 h9h8 a4a5 e6e5 g4g5 a8a7 e3e4 i6i5 g5g6 i5i4 a0a1 a7a8 a1a2 a8a7 a2a3 a7a8 a3a4 a8a7 h0h1 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3" to listOf(
            "a8a7" to "车1进1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 c3c4 c6c5 g3g4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a6a5 e3e4 i6i5 i3i4 a8a7 a0a1 a7a6 a1a2 h8h7 a2a3 h7h6 h0h1 h6h5 h1h2 h5h4 h2h3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3" to listOf(
            "a6a7" to "车1退1",
            "h4h5" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h9g7 h0g2 a9a8 b0c2 b9c7 i0h0 g6g5 b2b4 i9h9 g3g4 c6c5 a3a4 g9e7 c3c4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2" to listOf(
            "a6a7" to "车1退1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "c3c4 c6c5 c4c5 h9g7 h2e2 b9c7 h0g2 g9e7 b2b4 i9h9 b0c2 a9a8 g3g4 g6g5 a3a4 h9h8 i0h0 a8a7 g4g5 a6a5 c5c6 e6e5 e3e4 i6i5 g5g6 i5i4 a0a1 a7a6 a1a2 a6a7 a2a3 a7a6 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3" to listOf(
            "a7a6" to "车1进1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 b9c7 h0g2 c6c5 b0c2 h9g7 i0h0 i9h9 c3c4 a9a8 b2b4 g9e7 g3g4 g6g5 a3a4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2" to listOf(
            "a6a7" to "车1退1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 g3g4 c6c5 c3c4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a8a7 a4a5 e5e4 i3i4 i6i5 a0a1 h8h7 a1a2 h7h6 a2a3 h6h5 a3a4 h5h4 h0h1 h4h3 h1h2 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8" to listOf(
            "a4a3" to "车九退一",
            "h2h1" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "g0e2 c9e7 b2b4 b9c7 h0g2 h9g7 i0h0 i9h9 g3g4 c6c5 a3a4 g6g5 c3c4 a9a8 b0c2 h9h8 a4a5 e6e5 g4g5 a8a7 e3e4 i6i5 g5g6 i5i4 a0a1 a7a8 a1a2 a8a7 a2a3 a7a8 a3a4 a8a7 h0h1 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7" to listOf(
            "a3a4" to "车九进一",
            "h1h0" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 c3c4 c6c5 g3g4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a6a5 e3e4 i6i5 i3i4 a8a7 a0a1 a7a6 a1a2 h8h7 a2a3 h7h6 h0h1 h6h5 h1h2 h5h4 h2h3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7" to listOf(
            "a3a2" to "车九退一",
            "h3h2" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h9g7 h0g2 a9a8 b0c2 b9c7 i0h0 g6g5 b2b4 i9h9 g3g4 c6c5 a3a4 g9e7 c3c4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7" to listOf(
            "a2a3" to "车九进一",
            "h6h5" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "c3c4 c6c5 c4c5 h9g7 h2e2 b9c7 h0g2 g9e7 b2b4 i9h9 b0c2 a9a8 g3g4 g6g5 a3a4 h9h8 i0h0 a8a7 g4g5 a6a5 c5c6 e6e5 e3e4 i6i5 g5g6 i5i4 a0a1 a7a6 a1a2 a6a7 a2a3 a7a6 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6" to listOf(
            "a3a2" to "车九退一",
            "h6h5" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 b9c7 h0g2 c6c5 b0c2 h9g7 i0h0 i9h9 c3c4 a9a8 b2b4 g9e7 g3g4 g6g5 a3a4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7" to listOf(
            "a2a3" to "车九进一",
            "h6h5" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 g3g4 c6c5 c3c4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a8a7 a4a5 e5e4 i3i4 i6i5 a0a1 h8h7 a1a2 h7h6 a2a3 h6h5 a3a4 h5h4 h0h1 h4h3 h1h2 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3" to listOf(
            "a8a7" to "车1进1",
            "h3h4" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "g0e2 c9e7 b2b4 b9c7 h0g2 h9g7 i0h0 i9h9 g3g4 c6c5 a3a4 g6g5 c3c4 a9a8 b0c2 h9h8 a4a5 e6e5 g4g5 a8a7 e3e4 i6i5 g5g6 i5i4 a0a1 a7a8 a1a2 a8a7 a2a3 a7a8 a3a4 a8a7 h0h1 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4" to listOf(
            "a7a8" to "车1退1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 c3c4 c6c5 g3g4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a6a5 e3e4 i6i5 i3i4 a8a7 a0a1 a7a6 a1a2 h8h7 a2a3 h7h6 h0h1 h6h5 h1h2 h5h4 h2h3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2" to listOf(
            "a7a6" to "车1进1",
            "h4h5" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h9g7 h0g2 a9a8 b0c2 b9c7 i0h0 g6g5 b2b4 i9h9 g3g4 c6c5 a3a4 g9e7 c3c4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3" to listOf(
            "a7a6" to "车1进1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "c3c4 c6c5 c4c5 h9g7 h2e2 b9c7 h0g2 g9e7 b2b4 i9h9 b0c2 a9a8 g3g4 g6g5 a3a4 h9h8 i0h0 a8a7 g4g5 a6a5 c5c6 e6e5 e3e4 i6i5 g5g6 i5i4 a0a1 a7a6 a1a2 a6a7 a2a3 a7a6 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2" to listOf(
            "a6a7" to "车1退1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 b9c7 h0g2 c6c5 b0c2 h9g7 i0h0 i9h9 c3c4 a9a8 b2b4 g9e7 g3g4 g6g5 a3a4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3" to listOf(
            "a7a6" to "车1进1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 g3g4 c6c5 c3c4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a8a7 a4a5 e5e4 i3i4 i6i5 a0a1 h8h7 a1a2 h7h6 a2a3 h6h5 a3a4 h5h4 h0h1 h4h3 h1h2 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7" to listOf(
            "a3a4" to "车九进一",
            "h2h1" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "g0e2 c9e7 b2b4 b9c7 h0g2 h9g7 i0h0 i9h9 g3g4 c6c5 a3a4 g6g5 c3c4 a9a8 b0c2 h9h8 a4a5 e6e5 g4g5 a8a7 e3e4 i6i5 g5g6 i5i4 a0a1 a7a8 a1a2 a8a7 a2a3 a7a8 a3a4 a8a7 h0h1 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8" to listOf(
            "a4a3" to "车九退一",
            "h1h0" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 c3c4 c6c5 g3g4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a6a5 e3e4 i6i5 i3i4 a8a7 a0a1 a7a6 a1a2 h8h7 a2a3 h7h6 h0h1 h6h5 h1h2 h5h4 h2h3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6" to listOf(
            "a2a3" to "车九进一",
            "h3h2" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h9g7 h0g2 a9a8 b0c2 b9c7 i0h0 g6g5 b2b4 i9h9 g3g4 c6c5 a3a4 g9e7 c3c4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6" to listOf(
            "a3a2" to "车九退一",
            "h6h5" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "c3c4 c6c5 c4c5 h9g7 h2e2 b9c7 h0g2 g9e7 b2b4 i9h9 b0c2 a9a8 g3g4 g6g5 a3a4 h9h8 i0h0 a8a7 g4g5 a6a5 c5c6 e6e5 e3e4 i6i5 g5g6 i5i4 a0a1 a7a6 a1a2 a6a7 a2a3 a7a6 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7" to listOf(
            "a2a3" to "车九进一",
            "h6h5" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 b9c7 h0g2 c6c5 b0c2 h9g7 i0h0 i9h9 c3c4 a9a8 b2b4 g9e7 g3g4 g6g5 a3a4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6" to listOf(
            "a3a2" to "车九退一",
            "h6h5" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 g3g4 c6c5 c3c4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a8a7 a4a5 e5e4 i3i4 i6i5 a0a1 h8h7 a1a2 h7h6 a2a3 h6h5 a3a4 h5h4 h0h1 h4h3 h1h2 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4" to listOf(
            "a7a8" to "车1退1",
            "h3h4" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "g0e2 c9e7 b2b4 b9c7 h0g2 h9g7 i0h0 i9h9 g3g4 c6c5 a3a4 g6g5 c3c4 a9a8 b0c2 h9h8 a4a5 e6e5 g4g5 a8a7 e3e4 i6i5 g5g6 i5i4 a0a1 a7a8 a1a2 a8a7 a2a3 a7a8 a3a4 a8a7 h0h1 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3" to listOf(
            "a8a7" to "车1进1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 c3c4 c6c5 g3g4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a6a5 e3e4 i6i5 i3i4 a8a7 a0a1 a7a6 a1a2 h8h7 a2a3 h7h6 h0h1 h6h5 h1h2 h5h4 h2h3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3" to listOf(
            "a6a7" to "车1退1",
            "h4h5" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h9g7 h0g2 a9a8 b0c2 b9c7 i0h0 g6g5 b2b4 i9h9 g3g4 c6c5 a3a4 g9e7 c3c4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2" to listOf(
            "a6a7" to "车1退1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "c3c4 c6c5 c4c5 h9g7 h2e2 b9c7 h0g2 g9e7 b2b4 i9h9 b0c2 a9a8 g3g4 g6g5 a3a4 h9h8 i0h0 a8a7 g4g5 a6a5 c5c6 e6e5 e3e4 i6i5 g5g6 i5i4 a0a1 a7a6 a1a2 a6a7 a2a3 a7a6 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3" to listOf(
            "a7a6" to "车1进1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 b9c7 h0g2 c6c5 b0c2 h9g7 i0h0 i9h9 c3c4 a9a8 b2b4 g9e7 g3g4 g6g5 a3a4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2" to listOf(
            "a6a7" to "车1退1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 g3g4 c6c5 c3c4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a8a7 a4a5 e5e4 i3i4 i6i5 a0a1 h8h7 a1a2 h7h6 a2a3 h6h5 a3a4 h5h4 h0h1 h4h3 h1h2 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8" to listOf(
            "a4a3" to "车九退一",
            "h2h1" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "g0e2 c9e7 b2b4 b9c7 h0g2 h9g7 i0h0 i9h9 g3g4 c6c5 a3a4 g6g5 c3c4 a9a8 b0c2 h9h8 a4a5 e6e5 g4g5 a8a7 e3e4 i6i5 g5g6 i5i4 a0a1 a7a8 a1a2 a8a7 a2a3 a7a8 a3a4 a8a7 h0h1 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7" to listOf(
            "a3a4" to "车九进一",
            "h1h0" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 c3c4 c6c5 g3g4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a6a5 e3e4 i6i5 i3i4 a8a7 a0a1 a7a6 a1a2 h8h7 a2a3 h7h6 h0h1 h6h5 h1h2 h5h4 h2h3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7" to listOf(
            "a3a2" to "车九退一",
            "h3h2" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h9g7 h0g2 a9a8 b0c2 b9c7 i0h0 g6g5 b2b4 i9h9 g3g4 c6c5 a3a4 g9e7 c3c4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7" to listOf(
            "a2a3" to "车九进一",
            "h6h5" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "c3c4 c6c5 c4c5 h9g7 h2e2 b9c7 h0g2 g9e7 b2b4 i9h9 b0c2 a9a8 g3g4 g6g5 a3a4 h9h8 i0h0 a8a7 g4g5 a6a5 c5c6 e6e5 e3e4 i6i5 g5g6 i5i4 a0a1 a7a6 a1a2 a6a7 a2a3 a7a6 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6" to listOf(
            "a3a2" to "车九退一",
            "h6h5" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 b9c7 h0g2 c6c5 b0c2 h9g7 i0h0 i9h9 c3c4 a9a8 b2b4 g9e7 g3g4 g6g5 a3a4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7" to listOf(
            "a2a3" to "车九进一",
            "h6h5" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 g3g4 c6c5 c3c4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a8a7 a4a5 e5e4 i3i4 i6i5 a0a1 h8h7 a1a2 h7h6 a2a3 h6h5 a3a4 h5h4 h0h1 h4h3 h1h2 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3" to listOf(
            "a8a7" to "车1进1",
            "h3h4" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "g0e2 c9e7 b2b4 b9c7 h0g2 h9g7 i0h0 i9h9 g3g4 c6c5 a3a4 g6g5 c3c4 a9a8 b0c2 h9h8 a4a5 e6e5 g4g5 a8a7 e3e4 i6i5 g5g6 i5i4 a0a1 a7a8 a1a2 a8a7 a2a3 a7a8 a3a4 a8a7 h0h1 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4" to listOf(
            "a7a8" to "车1退1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 c3c4 c6c5 g3g4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a6a5 e3e4 i6i5 i3i4 a8a7 a0a1 a7a6 a1a2 h8h7 a2a3 h7h6 h0h1 h6h5 h1h2 h5h4 h2h3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2" to listOf(
            "a7a6" to "车1进1",
            "h4h5" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h9g7 h0g2 a9a8 b0c2 b9c7 i0h0 g6g5 b2b4 i9h9 g3g4 c6c5 a3a4 g9e7 c3c4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3" to listOf(
            "a7a6" to "车1进1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "c3c4 c6c5 c4c5 h9g7 h2e2 b9c7 h0g2 g9e7 b2b4 i9h9 b0c2 a9a8 g3g4 g6g5 a3a4 h9h8 i0h0 a8a7 g4g5 a6a5 c5c6 e6e5 e3e4 i6i5 g5g6 i5i4 a0a1 a7a6 a1a2 a6a7 a2a3 a7a6 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2" to listOf(
            "a6a7" to "车1退1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 b9c7 h0g2 c6c5 b0c2 h9g7 i0h0 i9h9 c3c4 a9a8 b2b4 g9e7 g3g4 g6g5 a3a4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3" to listOf(
            "a7a6" to "车1进1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 g3g4 c6c5 c3c4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a8a7 a4a5 e5e4 i3i4 i6i5 a0a1 h8h7 a1a2 h7h6 a2a3 h6h5 a3a4 h5h4 h0h1 h4h3 h1h2 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7" to listOf(
            "a3a4" to "车九进一",
            "h2h1" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "g0e2 c9e7 b2b4 b9c7 h0g2 h9g7 i0h0 i9h9 g3g4 c6c5 a3a4 g6g5 c3c4 a9a8 b0c2 h9h8 a4a5 e6e5 g4g5 a8a7 e3e4 i6i5 g5g6 i5i4 a0a1 a7a8 a1a2 a8a7 a2a3 a7a8 a3a4 a8a7 h0h1 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8" to listOf(
            "a4a3" to "车九退一",
            "h1h0" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 c3c4 c6c5 g3g4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a6a5 e3e4 i6i5 i3i4 a8a7 a0a1 a7a6 a1a2 h8h7 a2a3 h7h6 h0h1 h6h5 h1h2 h5h4 h2h3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6" to listOf(
            "a2a3" to "车九进一",
            "h3h2" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h9g7 h0g2 a9a8 b0c2 b9c7 i0h0 g6g5 b2b4 i9h9 g3g4 c6c5 a3a4 g9e7 c3c4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6" to listOf(
            "a3a2" to "车九退一",
            "h6h5" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "c3c4 c6c5 c4c5 h9g7 h2e2 b9c7 h0g2 g9e7 b2b4 i9h9 b0c2 a9a8 g3g4 g6g5 a3a4 h9h8 i0h0 a8a7 g4g5 a6a5 c5c6 e6e5 e3e4 i6i5 g5g6 i5i4 a0a1 a7a6 a1a2 a6a7 a2a3 a7a6 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7" to listOf(
            "a2a3" to "车九进一",
            "h6h5" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 b9c7 h0g2 c6c5 b0c2 h9g7 i0h0 i9h9 c3c4 a9a8 b2b4 g9e7 g3g4 g6g5 a3a4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6" to listOf(
            "a3a2" to "车九退一",
            "h6h5" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 g3g4 c6c5 c3c4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a8a7 a4a5 e5e4 i3i4 i6i5 a0a1 h8h7 a1a2 h7h6 a2a3 h6h5 a3a4 h5h4 h0h1 h4h3 h1h2 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4" to listOf(
            "a7a8" to "车1退1",
            "h3h4" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "g0e2 c9e7 b2b4 b9c7 h0g2 h9g7 i0h0 i9h9 g3g4 c6c5 a3a4 g6g5 c3c4 a9a8 b0c2 h9h8 a4a5 e6e5 g4g5 a8a7 e3e4 i6i5 g5g6 i5i4 a0a1 a7a8 a1a2 a8a7 a2a3 a7a8 a3a4 a8a7 h0h1 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3" to listOf(
            "a8a7" to "车1进1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 c3c4 c6c5 g3g4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a6a5 e3e4 i6i5 i3i4 a8a7 a0a1 a7a6 a1a2 h8h7 a2a3 h7h6 h0h1 h6h5 h1h2 h5h4 h2h3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3" to listOf(
            "a6a7" to "车1退1",
            "h4h5" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h9g7 h0g2 a9a8 b0c2 b9c7 i0h0 g6g5 b2b4 i9h9 g3g4 c6c5 a3a4 g9e7 c3c4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2" to listOf(
            "a6a7" to "车1退1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "c3c4 c6c5 c4c5 h9g7 h2e2 b9c7 h0g2 g9e7 b2b4 i9h9 b0c2 a9a8 g3g4 g6g5 a3a4 h9h8 i0h0 a8a7 g4g5 a6a5 c5c6 e6e5 e3e4 i6i5 g5g6 i5i4 a0a1 a7a6 a1a2 a6a7 a2a3 a7a6 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3" to listOf(
            "a7a6" to "车1进1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 b9c7 h0g2 c6c5 b0c2 h9g7 i0h0 i9h9 c3c4 a9a8 b2b4 g9e7 g3g4 g6g5 a3a4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2" to listOf(
            "a6a7" to "车1退1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 g3g4 c6c5 c3c4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a8a7 a4a5 e5e4 i3i4 i6i5 a0a1 h8h7 a1a2 h7h6 a2a3 h6h5 a3a4 h5h4 h0h1 h4h3 h1h2 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8" to listOf(
            "a4a3" to "车九退一",
            "h2h1" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "g0e2 c9e7 b2b4 b9c7 h0g2 h9g7 i0h0 i9h9 g3g4 c6c5 a3a4 g6g5 c3c4 a9a8 b0c2 h9h8 a4a5 e6e5 g4g5 a8a7 e3e4 i6i5 g5g6 i5i4 a0a1 a7a8 a1a2 a8a7 a2a3 a7a8 a3a4 a8a7 h0h1 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7" to listOf(
            "a3a4" to "车九进一",
            "h1h0" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 c3c4 c6c5 g3g4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a6a5 e3e4 i6i5 i3i4 a8a7 a0a1 a7a6 a1a2 h8h7 a2a3 h7h6 h0h1 h6h5 h1h2 h5h4 h2h3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7" to listOf(
            "a3a2" to "车九退一",
            "h3h2" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h9g7 h0g2 a9a8 b0c2 b9c7 i0h0 g6g5 b2b4 i9h9 g3g4 c6c5 a3a4 g9e7 c3c4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7" to listOf(
            "a2a3" to "车九进一",
            "h6h5" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "c3c4 c6c5 c4c5 h9g7 h2e2 b9c7 h0g2 g9e7 b2b4 i9h9 b0c2 a9a8 g3g4 g6g5 a3a4 h9h8 i0h0 a8a7 g4g5 a6a5 c5c6 e6e5 e3e4 i6i5 g5g6 i5i4 a0a1 a7a6 a1a2 a6a7 a2a3 a7a6 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6" to listOf(
            "a3a2" to "车九退一",
            "h6h5" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 b9c7 h0g2 c6c5 b0c2 h9g7 i0h0 i9h9 c3c4 a9a8 b2b4 g9e7 g3g4 g6g5 a3a4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7" to listOf(
            "a2a3" to "车九进一",
            "h6h5" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 g3g4 c6c5 c3c4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a8a7 a4a5 e5e4 i3i4 i6i5 a0a1 h8h7 a1a2 h7h6 a2a3 h6h5 a3a4 h5h4 h0h1 h4h3 h1h2 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3" to listOf(
            "a8a7" to "车1进1",
            "h3h4" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "g0e2 c9e7 b2b4 b9c7 h0g2 h9g7 i0h0 i9h9 g3g4 c6c5 a3a4 g6g5 c3c4 a9a8 b0c2 h9h8 a4a5 e6e5 g4g5 a8a7 e3e4 i6i5 g5g6 i5i4 a0a1 a7a8 a1a2 a8a7 a2a3 a7a8 a3a4 a8a7 h0h1 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4" to listOf(
            "a7a8" to "车1退1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 c3c4 c6c5 g3g4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a6a5 e3e4 i6i5 i3i4 a8a7 a0a1 a7a6 a1a2 h8h7 a2a3 h7h6 h0h1 h6h5 h1h2 h5h4 h2h3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2" to listOf(
            "a7a6" to "车1进1",
            "h4h5" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h9g7 h0g2 a9a8 b0c2 b9c7 i0h0 g6g5 b2b4 i9h9 g3g4 c6c5 a3a4 g9e7 c3c4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3" to listOf(
            "a7a6" to "车1进1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "c3c4 c6c5 c4c5 h9g7 h2e2 b9c7 h0g2 g9e7 b2b4 i9h9 b0c2 a9a8 g3g4 g6g5 a3a4 h9h8 i0h0 a8a7 g4g5 a6a5 c5c6 e6e5 e3e4 i6i5 g5g6 i5i4 a0a1 a7a6 a1a2 a6a7 a2a3 a7a6 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2" to listOf(
            "a6a7" to "车1退1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 b9c7 h0g2 c6c5 b0c2 h9g7 i0h0 i9h9 c3c4 a9a8 b2b4 g9e7 g3g4 g6g5 a3a4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3" to listOf(
            "a7a6" to "车1进1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 g3g4 c6c5 c3c4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a8a7 a4a5 e5e4 i3i4 i6i5 a0a1 h8h7 a1a2 h7h6 a2a3 h6h5 a3a4 h5h4 h0h1 h4h3 h1h2 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7" to listOf(
            "a3a4" to "车九进一",
            "h2h1" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "g0e2 c9e7 b2b4 b9c7 h0g2 h9g7 i0h0 i9h9 g3g4 c6c5 a3a4 g6g5 c3c4 a9a8 b0c2 h9h8 a4a5 e6e5 g4g5 a8a7 e3e4 i6i5 g5g6 i5i4 a0a1 a7a8 a1a2 a8a7 a2a3 a7a8 a3a4 a8a7 h0h1 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8" to listOf(
            "a4a3" to "车九退一",
            "h1h0" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 c3c4 c6c5 g3g4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a6a5 e3e4 i6i5 i3i4 a8a7 a0a1 a7a6 a1a2 h8h7 a2a3 h7h6 h0h1 h6h5 h1h2 h5h4 h2h3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6" to listOf(
            "a2a3" to "车九进一",
            "h3h2" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h9g7 h0g2 a9a8 b0c2 b9c7 i0h0 g6g5 b2b4 i9h9 g3g4 c6c5 a3a4 g9e7 c3c4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6" to listOf(
            "a3a2" to "车九退一",
            "h6h5" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "c3c4 c6c5 c4c5 h9g7 h2e2 b9c7 h0g2 g9e7 b2b4 i9h9 b0c2 a9a8 g3g4 g6g5 a3a4 h9h8 i0h0 a8a7 g4g5 a6a5 c5c6 e6e5 e3e4 i6i5 g5g6 i5i4 a0a1 a7a6 a1a2 a6a7 a2a3 a7a6 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7" to listOf(
            "a2a3" to "车九进一",
            "h6h5" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 b9c7 h0g2 c6c5 b0c2 h9g7 i0h0 i9h9 c3c4 a9a8 b2b4 g9e7 g3g4 g6g5 a3a4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6" to listOf(
            "a3a2" to "车九退一",
            "h6h5" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 g3g4 c6c5 c3c4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a8a7 a4a5 e5e4 i3i4 i6i5 a0a1 h8h7 a1a2 h7h6 a2a3 h6h5 a3a4 h5h4 h0h1 h4h3 h1h2 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4" to listOf(
            "a7a8" to "车1退1",
            "h3h4" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "g0e2 c9e7 b2b4 b9c7 h0g2 h9g7 i0h0 i9h9 g3g4 c6c5 a3a4 g6g5 c3c4 a9a8 b0c2 h9h8 a4a5 e6e5 g4g5 a8a7 e3e4 i6i5 g5g6 i5i4 a0a1 a7a8 a1a2 a8a7 a2a3 a7a8 a3a4 a8a7 h0h1 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3" to listOf(
            "a8a7" to "车1进1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 c3c4 c6c5 g3g4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a6a5 e3e4 i6i5 i3i4 a8a7 a0a1 a7a6 a1a2 h8h7 a2a3 h7h6 h0h1 h6h5 h1h2 h5h4 h2h3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3" to listOf(
            "a6a7" to "车1退1",
            "h4h5" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h9g7 h0g2 a9a8 b0c2 b9c7 i0h0 g6g5 b2b4 i9h9 g3g4 c6c5 a3a4 g9e7 c3c4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2" to listOf(
            "a6a7" to "车1退1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "c3c4 c6c5 c4c5 h9g7 h2e2 b9c7 h0g2 g9e7 b2b4 i9h9 b0c2 a9a8 g3g4 g6g5 a3a4 h9h8 i0h0 a8a7 g4g5 a6a5 c5c6 e6e5 e3e4 i6i5 g5g6 i5i4 a0a1 a7a6 a1a2 a6a7 a2a3 a7a6 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3" to listOf(
            "a7a6" to "车1进1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 b9c7 h0g2 c6c5 b0c2 h9g7 i0h0 i9h9 c3c4 a9a8 b2b4 g9e7 g3g4 g6g5 a3a4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2" to listOf(
            "a6a7" to "车1退1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 g3g4 c6c5 c3c4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a8a7 a4a5 e5e4 i3i4 i6i5 a0a1 h8h7 a1a2 h7h6 a2a3 h6h5 a3a4 h5h4 h0h1 h4h3 h1h2 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8" to listOf(
            "a4a3" to "车九退一",
            "h2h1" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "g0e2 c9e7 b2b4 b9c7 h0g2 h9g7 i0h0 i9h9 g3g4 c6c5 a3a4 g6g5 c3c4 a9a8 b0c2 h9h8 a4a5 e6e5 g4g5 a8a7 e3e4 i6i5 g5g6 i5i4 a0a1 a7a8 a1a2 a8a7 a2a3 a7a8 a3a4 a8a7 h0h1 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7" to listOf(
            "a3a4" to "车九进一",
            "h1h0" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 c3c4 c6c5 g3g4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a6a5 e3e4 i6i5 i3i4 a8a7 a0a1 a7a6 a1a2 h8h7 a2a3 h7h6 h0h1 h6h5 h1h2 h5h4 h2h3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7" to listOf(
            "a3a2" to "车九退一",
            "h3h2" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h9g7 h0g2 a9a8 b0c2 b9c7 i0h0 g6g5 b2b4 i9h9 g3g4 c6c5 a3a4 g9e7 c3c4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7" to listOf(
            "a2a3" to "车九进一",
            "h6h5" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "c3c4 c6c5 c4c5 h9g7 h2e2 b9c7 h0g2 g9e7 b2b4 i9h9 b0c2 a9a8 g3g4 g6g5 a3a4 h9h8 i0h0 a8a7 g4g5 a6a5 c5c6 e6e5 e3e4 i6i5 g5g6 i5i4 a0a1 a7a6 a1a2 a6a7 a2a3 a7a6 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6" to listOf(
            "a3a2" to "车九退一",
            "h6h5" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 b9c7 h0g2 c6c5 b0c2 h9g7 i0h0 i9h9 c3c4 a9a8 b2b4 g9e7 g3g4 g6g5 a3a4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7" to listOf(
            "a2a3" to "车九进一",
            "h6h5" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 g3g4 c6c5 c3c4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a8a7 a4a5 e5e4 i3i4 i6i5 a0a1 h8h7 a1a2 h7h6 a2a3 h6h5 a3a4 h5h4 h0h1 h4h3 h1h2 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3" to listOf(
            "a8a7" to "车1进1",
            "h3h4" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "g0e2 c9e7 b2b4 b9c7 h0g2 h9g7 i0h0 i9h9 g3g4 c6c5 a3a4 g6g5 c3c4 a9a8 b0c2 h9h8 a4a5 e6e5 g4g5 a8a7 e3e4 i6i5 g5g6 i5i4 a0a1 a7a8 a1a2 a8a7 a2a3 a7a8 a3a4 a8a7 h0h1 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4" to listOf(
            "a7a8" to "车1退1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 c3c4 c6c5 g3g4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a6a5 e3e4 i6i5 i3i4 a8a7 a0a1 a7a6 a1a2 h8h7 a2a3 h7h6 h0h1 h6h5 h1h2 h5h4 h2h3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2" to listOf(
            "a7a6" to "车1进1",
            "h4h5" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h9g7 h0g2 a9a8 b0c2 b9c7 i0h0 g6g5 b2b4 i9h9 g3g4 c6c5 a3a4 g9e7 c3c4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3" to listOf(
            "a7a6" to "车1进1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "c3c4 c6c5 c4c5 h9g7 h2e2 b9c7 h0g2 g9e7 b2b4 i9h9 b0c2 a9a8 g3g4 g6g5 a3a4 h9h8 i0h0 a8a7 g4g5 a6a5 c5c6 e6e5 e3e4 i6i5 g5g6 i5i4 a0a1 a7a6 a1a2 a6a7 a2a3 a7a6 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2" to listOf(
            "a6a7" to "车1退1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 b9c7 h0g2 c6c5 b0c2 h9g7 i0h0 i9h9 c3c4 a9a8 b2b4 g9e7 g3g4 g6g5 a3a4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3" to listOf(
            "a7a6" to "车1进1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 g3g4 c6c5 c3c4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a8a7 a4a5 e5e4 i3i4 i6i5 a0a1 h8h7 a1a2 h7h6 a2a3 h6h5 a3a4 h5h4 h0h1 h4h3 h1h2 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7" to listOf(
            "a3a4" to "车九进一",
            "h2h1" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "g0e2 c9e7 b2b4 b9c7 h0g2 h9g7 i0h0 i9h9 g3g4 c6c5 a3a4 g6g5 c3c4 a9a8 b0c2 h9h8 a4a5 e6e5 g4g5 a8a7 e3e4 i6i5 g5g6 i5i4 a0a1 a7a8 a1a2 a8a7 a2a3 a7a8 a3a4 a8a7 h0h1 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8" to listOf(
            "a4a3" to "车九退一",
            "h1h0" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 c3c4 c6c5 g3g4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a6a5 e3e4 i6i5 i3i4 a8a7 a0a1 a7a6 a1a2 h8h7 a2a3 h7h6 h0h1 h6h5 h1h2 h5h4 h2h3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6" to listOf(
            "a2a3" to "车九进一",
            "h3h2" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h9g7 h0g2 a9a8 b0c2 b9c7 i0h0 g6g5 b2b4 i9h9 g3g4 c6c5 a3a4 g9e7 c3c4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6" to listOf(
            "a3a2" to "车九退一",
            "h6h5" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "c3c4 c6c5 c4c5 h9g7 h2e2 b9c7 h0g2 g9e7 b2b4 i9h9 b0c2 a9a8 g3g4 g6g5 a3a4 h9h8 i0h0 a8a7 g4g5 a6a5 c5c6 e6e5 e3e4 i6i5 g5g6 i5i4 a0a1 a7a6 a1a2 a6a7 a2a3 a7a6 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7" to listOf(
            "a2a3" to "车九进一",
            "h6h5" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 b9c7 h0g2 c6c5 b0c2 h9g7 i0h0 i9h9 c3c4 a9a8 b2b4 g9e7 g3g4 g6g5 a3a4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6" to listOf(
            "a3a2" to "车九退一",
            "h6h5" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 g3g4 c6c5 c3c4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a8a7 a4a5 e5e4 i3i4 i6i5 a0a1 h8h7 a1a2 h7h6 a2a3 h6h5 a3a4 h5h4 h0h1 h4h3 h1h2 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4" to listOf(
            "a7a8" to "车1退1",
            "h3h4" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "g0e2 c9e7 b2b4 b9c7 h0g2 h9g7 i0h0 i9h9 g3g4 c6c5 a3a4 g6g5 c3c4 a9a8 b0c2 h9h8 a4a5 e6e5 g4g5 a8a7 e3e4 i6i5 g5g6 i5i4 a0a1 a7a8 a1a2 a8a7 a2a3 a7a8 a3a4 a8a7 h0h1 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3" to listOf(
            "a8a7" to "车1进1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 c3c4 c6c5 g3g4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a6a5 e3e4 i6i5 i3i4 a8a7 a0a1 a7a6 a1a2 h8h7 a2a3 h7h6 h0h1 h6h5 h1h2 h5h4 h2h3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3" to listOf(
            "a6a7" to "车1退1",
            "h4h5" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h9g7 h0g2 a9a8 b0c2 b9c7 i0h0 g6g5 b2b4 i9h9 g3g4 c6c5 a3a4 g9e7 c3c4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2" to listOf(
            "a6a7" to "车1退1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "c3c4 c6c5 c4c5 h9g7 h2e2 b9c7 h0g2 g9e7 b2b4 i9h9 b0c2 a9a8 g3g4 g6g5 a3a4 h9h8 i0h0 a8a7 g4g5 a6a5 c5c6 e6e5 e3e4 i6i5 g5g6 i5i4 a0a1 a7a6 a1a2 a6a7 a2a3 a7a6 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3" to listOf(
            "a7a6" to "车1进1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 b9c7 h0g2 c6c5 b0c2 h9g7 i0h0 i9h9 c3c4 a9a8 b2b4 g9e7 g3g4 g6g5 a3a4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2" to listOf(
            "a6a7" to "车1退1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 g3g4 c6c5 c3c4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a8a7 a4a5 e5e4 i3i4 i6i5 a0a1 h8h7 a1a2 h7h6 a2a3 h6h5 a3a4 h5h4 h0h1 h4h3 h1h2 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8" to listOf(
            "a4a3" to "车九退一",
            "h2h1" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "g0e2 c9e7 b2b4 b9c7 h0g2 h9g7 i0h0 i9h9 g3g4 c6c5 a3a4 g6g5 c3c4 a9a8 b0c2 h9h8 a4a5 e6e5 g4g5 a8a7 e3e4 i6i5 g5g6 i5i4 a0a1 a7a8 a1a2 a8a7 a2a3 a7a8 a3a4 a8a7 h0h1 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7" to listOf(
            "a3a4" to "车九进一",
            "h1h0" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 c3c4 c6c5 g3g4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a6a5 e3e4 i6i5 i3i4 a8a7 a0a1 a7a6 a1a2 h8h7 a2a3 h7h6 h0h1 h6h5 h1h2 h5h4 h2h3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7" to listOf(
            "a3a2" to "车九退一",
            "h3h2" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h9g7 h0g2 a9a8 b0c2 b9c7 i0h0 g6g5 b2b4 i9h9 g3g4 c6c5 a3a4 g9e7 c3c4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7" to listOf(
            "a2a3" to "车九进一",
            "h6h5" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "c3c4 c6c5 c4c5 h9g7 h2e2 b9c7 h0g2 g9e7 b2b4 i9h9 b0c2 a9a8 g3g4 g6g5 a3a4 h9h8 i0h0 a8a7 g4g5 a6a5 c5c6 e6e5 e3e4 i6i5 g5g6 i5i4 a0a1 a7a6 a1a2 a6a7 a2a3 a7a6 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6" to listOf(
            "a3a2" to "车九退一",
            "h6h5" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 b9c7 h0g2 c6c5 b0c2 h9g7 i0h0 i9h9 c3c4 a9a8 b2b4 g9e7 g3g4 g6g5 a3a4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7" to listOf(
            "a2a3" to "车九进一",
            "h6h5" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 g3g4 c6c5 c3c4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a8a7 a4a5 e5e4 i3i4 i6i5 a0a1 h8h7 a1a2 h7h6 a2a3 h6h5 a3a4 h5h4 h0h1 h4h3 h1h2 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3" to listOf(
            "a8a7" to "车1进1",
            "h3h4" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "g0e2 c9e7 b2b4 b9c7 h0g2 h9g7 i0h0 i9h9 g3g4 c6c5 a3a4 g6g5 c3c4 a9a8 b0c2 h9h8 a4a5 e6e5 g4g5 a8a7 e3e4 i6i5 g5g6 i5i4 a0a1 a7a8 a1a2 a8a7 a2a3 a7a8 a3a4 a8a7 h0h1 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4" to listOf(
            "a7a8" to "车1退1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 c3c4 c6c5 g3g4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a6a5 e3e4 i6i5 i3i4 a8a7 a0a1 a7a6 a1a2 h8h7 a2a3 h7h6 h0h1 h6h5 h1h2 h5h4 h2h3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2" to listOf(
            "a7a6" to "车1进1",
            "h4h5" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h9g7 h0g2 a9a8 b0c2 b9c7 i0h0 g6g5 b2b4 i9h9 g3g4 c6c5 a3a4 g9e7 c3c4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3" to listOf(
            "a7a6" to "车1进1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "c3c4 c6c5 c4c5 h9g7 h2e2 b9c7 h0g2 g9e7 b2b4 i9h9 b0c2 a9a8 g3g4 g6g5 a3a4 h9h8 i0h0 a8a7 g4g5 a6a5 c5c6 e6e5 e3e4 i6i5 g5g6 i5i4 a0a1 a7a6 a1a2 a6a7 a2a3 a7a6 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2" to listOf(
            "a6a7" to "车1退1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 b9c7 h0g2 c6c5 b0c2 h9g7 i0h0 i9h9 c3c4 a9a8 b2b4 g9e7 g3g4 g6g5 a3a4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3" to listOf(
            "a7a6" to "车1进1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 g3g4 c6c5 c3c4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a8a7 a4a5 e5e4 i3i4 i6i5 a0a1 h8h7 a1a2 h7h6 a2a3 h6h5 a3a4 h5h4 h0h1 h4h3 h1h2 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7" to listOf(
            "a3a4" to "车九进一",
            "h2h1" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "g0e2 c9e7 b2b4 b9c7 h0g2 h9g7 i0h0 i9h9 g3g4 c6c5 a3a4 g6g5 c3c4 a9a8 b0c2 h9h8 a4a5 e6e5 g4g5 a8a7 e3e4 i6i5 g5g6 i5i4 a0a1 a7a8 a1a2 a8a7 a2a3 a7a8 a3a4 a8a7 h0h1 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8" to listOf(
            "a4a3" to "车九退一",
            "h1h0" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 c3c4 c6c5 g3g4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a6a5 e3e4 i6i5 i3i4 a8a7 a0a1 a7a6 a1a2 h8h7 a2a3 h7h6 h0h1 h6h5 h1h2 h5h4 h2h3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6" to listOf(
            "a2a3" to "车九进一",
            "h3h2" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h9g7 h0g2 a9a8 b0c2 b9c7 i0h0 g6g5 b2b4 i9h9 g3g4 c6c5 a3a4 g9e7 c3c4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6" to listOf(
            "a3a2" to "车九退一",
            "h6h5" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "c3c4 c6c5 c4c5 h9g7 h2e2 b9c7 h0g2 g9e7 b2b4 i9h9 b0c2 a9a8 g3g4 g6g5 a3a4 h9h8 i0h0 a8a7 g4g5 a6a5 c5c6 e6e5 e3e4 i6i5 g5g6 i5i4 a0a1 a7a6 a1a2 a6a7 a2a3 a7a6 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7" to listOf(
            "a2a3" to "车九进一",
            "h6h5" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 b9c7 h0g2 c6c5 b0c2 h9g7 i0h0 i9h9 c3c4 a9a8 b2b4 g9e7 g3g4 g6g5 a3a4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6" to listOf(
            "a3a2" to "车九退一",
            "h6h5" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 g3g4 c6c5 c3c4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a8a7 a4a5 e5e4 i3i4 i6i5 a0a1 h8h7 a1a2 h7h6 a2a3 h6h5 a3a4 h5h4 h0h1 h4h3 h1h2 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4" to listOf(
            "a7a8" to "车1退1",
            "h3h4" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "g0e2 c9e7 b2b4 b9c7 h0g2 h9g7 i0h0 i9h9 g3g4 c6c5 a3a4 g6g5 c3c4 a9a8 b0c2 h9h8 a4a5 e6e5 g4g5 a8a7 e3e4 i6i5 g5g6 i5i4 a0a1 a7a8 a1a2 a8a7 a2a3 a7a8 a3a4 a8a7 h0h1 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3" to listOf(
            "a8a7" to "车1进1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 c3c4 c6c5 g3g4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a6a5 e3e4 i6i5 i3i4 a8a7 a0a1 a7a6 a1a2 h8h7 a2a3 h7h6 h0h1 h6h5 h1h2 h5h4 h2h3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3 a6a7 a3a2 a7a6 a2a3" to listOf(
            "a6a7" to "车1退1",
            "h4h5" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h9g7 h0g2 a9a8 b0c2 b9c7 i0h0 g6g5 b2b4 i9h9 g3g4 c6c5 a3a4 g9e7 c3c4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2" to listOf(
            "a6a7" to "车1退1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "c3c4 c6c5 c4c5 h9g7 h2e2 b9c7 h0g2 g9e7 b2b4 i9h9 b0c2 a9a8 g3g4 g6g5 a3a4 h9h8 i0h0 a8a7 g4g5 a6a5 c5c6 e6e5 e3e4 i6i5 g5g6 i5i4 a0a1 a7a6 a1a2 a6a7 a2a3 a7a6 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3" to listOf(
            "a7a6" to "车1进1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 b9c7 h0g2 c6c5 b0c2 h9g7 i0h0 i9h9 c3c4 a9a8 b2b4 g9e7 g3g4 g6g5 a3a4 h9h8 g4g5 h8h9 g5g6 a6a5 e3e4 e6e5 i3i4 i6i5 a0a1 a8a7 a1a2 a7a6 a2a3 h9h8 h0h1 a6a7 h1h2 a7a6 h2h3 a6a7 h3h4 a7a6 h4h5 a6a7 h5h6 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2 a6a7 a2a3 a7a6 a3a2" to listOf(
            "a6a7" to "车1退1",
            "h8h9" to "车8退1",
            "b7b6" to "炮2进1",
        ),
        "h2e2 h7e7 h0g2 h9g7 b0c2 b9c7 b2b4 i9h9 g3g4 c6c5 c3c4 g6g5 a3a4 h9h8 i0h0 a9a8 g4g5 e6e5 g5g6 a8a7 a4a5 e5e4 i3i4 i6i5 a0a1 h8h7 a1a2 h7h6 a2a3 h6h5 a3a4 h5h4 h0h1 h4h3 h1h2 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8" to listOf(
            "a4a3" to "车九退一",
            "h2h1" to "车二退一",
            "b4b5" to "炮八进一",
        ),
        "g0e2 c9e7 b2b4 b9c7 h0g2 h9g7 i0h0 i9h9 g3g4 c6c5 a3a4 g6g5 c3c4 a9a8 b0c2 h9h8 a4a5 e6e5 g4g5 a8a7 e3e4 i6i5 g5g6 i5i4 a0a1 a7a8 a1a2 a8a7 a2a3 a7a8 a3a4 a8a7 h0h1 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7 a3a4 a7a8 a4a3 a8a7" to listOf(
            "a3a4" to "车九进一",
            "h1h0" to "车二退一",
            "b4b5" to "炮八进一",
        ),
    )

    fun knownHistories(): Set<String> = book.keys

    /** Suggestions for the position reached after [historyUci]
     *  from the standard start; empty when out of book. */
    fun lookup(historyUci: List<String>): List<Pair<String, String>> =
        book[historyUci.joinToString(" ")] ?: emptyList()

    /** One browsable book node: the move history that reaches
     *  the position, and the suggested replies there. */
    data class BookNode(
        val historyUci: List<String>,
        val suggestions: List<Pair<String, String>>
    )

    /** Every node in the book, root (empty history) first, in
     *  the order the lines were written. */
    fun allNodes(): List<BookNode> = book.entries.map { (key, suggestions) ->
        BookNode(
            if (key.isBlank()) emptyList() else key.split(" "),
            suggestions
        )
    }

    /** The Chinese notation of a move history from the standard
     *  start, one entry per move; stops early if a move cannot
     *  be replayed (cannot happen for book histories). */
    fun historyNotation(historyUci: List<String>): List<String> =
        Notation.pvText(GameState().cells.toList(), historyUci, historyUci.size)

    /** The named 定式 line a history belongs to, or null when
     *  no move along the way carries a name. Book names tag the
     *  defining moves in brackets — 炮二平五（中炮）, the reply
     *  炮8平5（顺炮）, 卒3进1（对兵局） — and the LAST tag on
     *  the path is the most specific line the position belongs
     *  to （中炮 lines answered by a central cannon are 顺炮；
     *  仙人指路 answered by a pawn is 对兵局）. Pure: derived
     *  only from the book itself, pinned by OpeningBookTest. */
    fun lineName(historyUci: List<String>): String? {
        var name: String? = null
        var prefix = emptyList<String>()
        for (uci in historyUci) {
            val suggestion = lookup(prefix).firstOrNull { it.first == uci }
                ?: return name
            val printed = suggestion.second
            val open = printed.indexOf('（')
            val close = printed.indexOf('）')
            if (open >= 0 && close > open + 1) {
                name = printed.substring(open + 1, close)
            }
            prefix = prefix + uci
        }
        return name
    }

    /** Whose turn it is after a move history from the standard
     *  start: Red moves first and the sides alternate, so an
     *  even-length history leaves Red to move. (Validated for
     *  every book node against the real state machine by
     *  OpeningBookTest.) */
    fun turnAfter(historyUci: List<String>): Side =
        if (historyUci.size % 2 == 0) Side.RED else Side.BLACK

    /** Book browsing search (1.15.0). A blank query returns the
     *  whole book. Otherwise a node matches when the query text
     *  appears in a suggestion's printed name (which carries the
     *  定式 names, e.g. 中炮／顺炮／仙人指路）, in the notation of
     *  its move history, or in the notation of one of its
     *  suggested moves in the reached position. */
    fun search(query: String): List<BookNode> {
        val q = query.trim()
        val nodes = allNodes()
        if (q.isEmpty()) return nodes
        return nodes.filter { node ->
            if (node.suggestions.any { it.second.contains(q) }) {
                return@filter true
            }
            // The accumulated line name (1.37.0): a deep node in
            // the 顺炮 line is findable by 顺炮 even when its own
            // suggestions carry no label.
            if (lineName(node.historyUci)?.contains(q) == true) {
                return@filter true
            }
            if (historyNotation(node.historyUci).any { it.contains(q) }) {
                return@filter true
            }
            val scratch = GameState()
            var reachable = true
            for (uci in node.historyUci) {
                val mv = Fen.parseUciMove(uci)
                if (mv == null || !scratch.tryMove(mv.first, mv.second)) {
                    reachable = false
                    break
                }
            }
            reachable && node.suggestions.any { (uci, _) ->
                val mv = Fen.parseUciMove(uci)
                mv != null &&
                    Notation.moveText(scratch.cells, mv.first, mv.second)
                        .contains(q)
            }
        }
    }
}
