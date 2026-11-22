package com.tuxunxiangqi.android.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

/** A built-in endgame puzzle. All puzzles are "Red to move and
 *  win" positions with a decisive theoretical verdict (material or
 *  textbook wins). There is no single forced line baked in: during
 *  play the engine judges every move, so any genuinely winning
 *  continuation counts — the engine is the referee. */
data class Puzzle(
    val id: Int,
    val title: String,
    val fen: String,
    val hint: String
)

object Puzzles {
    val all: List<Puzzle> = listOf(
        Puzzle(
            1, "双车炮对士象全",
            "2bakab2/9/3R1R3/9/4C4/9/9/9/9/4K4 w - - 0 1",
            "双车加一炮打士象全，子力压倒。先占中路，再逐个敲掉守子。"
        ),
        Puzzle(
            2, "马炮胜士象全",
            "2bakab2/9/4N4/9/9/4C4/9/9/9/4K4 w - - 0 1",
            "马炮对士象全是经典例胜：马踩中士、炮借马腿，慢慢收网，急不得。"
        ),
        Puzzle(
            3, "车马胜士象全",
            "2bakab2/9/3R1N3/9/9/9/9/9/9/3K5 w - - 0 1",
            "车马配合：车守肋道，马走卧槽，逼黑将无处可躲。"
        ),
        Puzzle(
            4, "车兵对单马",
            "4k4/9/2n6/4P4/9/9/R8/9/9/4K4 w - - 0 1",
            "高兵加车打单马：兵借车力过宫，马再能跳也拦不住两条线。"
        ),
        Puzzle(
            5, "车兵对双士",
            "3aka3/9/9/4P4/9/9/8R/9/9/4K4 w - - 0 1",
            "车兵对双士：先用兵换掉一士，车再收拾残局。"
        ),
        Puzzle(
            6, "双炮兵对士象全",
            "2bakab2/9/9/4P4/2C3C2/9/9/9/9/4K4 w - - 0 1",
            "双炮加过河兵：兵是炮的炮架，也是压宫的钉子，一步步往里挤。"
        ),
        Puzzle(
            7, "马兵胜士象全",
            "2bakab2/9/9/3NP4/9/9/9/9/9/3K5 w - - 0 1",
            "马兵对士象全是例胜，但步数不短：马走八个点，兵随将转，耐心局。"
        ),
        Puzzle(
            8, "单车对马炮",
            "4k4/9/2c6/6n2/4R4/9/9/9/9/4K4 w - - 0 1",
            "单车对马炮：车先捉双、再兑炮，剩下车对马就稳了。"
        ),
        Puzzle(
            9, "重炮加车",
            "3ak4/9/4C4/4C4/9/9/9/9/R8/4K4 w - - 0 1",
            "中路重炮已经成形，车再助一臂之力，黑将的活动空间只剩一条缝。"
        ),
        Puzzle(
            11, "车马对马士象",
            "2bakab2/9/7R1/3n5/4N4/9/9/9/9/4K4 w - - 0 1",
            "车马打带马的防线：先兑掉那匹马，剩下的就是熟悉的车马收官。"
        ),
        Puzzle(
            12, "马炮兵对士象全",
            "2bakab2/9/9/3NP4/9/4C4/9/9/9/3K5 w - - 0 1",
            "马炮兵三子归边：兵占花心、马走卧槽、炮借兵架，三条线轮流施压。"
        ),
        Puzzle(
            13, "单车对炮双士",
            "3aka3/9/9/9/4c4/9/R8/9/9/4K4 w - - 0 1",
            "车打炮双士：先捉炮再破士。对方炮能远程捣乱，车要先站住中路。"
        ),
        Puzzle(
            14, "双马炮对士象全",
            "2bakab2/9/2N3N2/9/9/9/4C4/9/9/4K4 w - - 0 1",
            "双马加一炮，子力富余：双马轮番卧槽，炮在中路封口，慢慢挤死。"
        ),
        Puzzle(
            15, "车兵对单炮",
            "4k4/9/7c1/4P4/9/2R6/9/9/9/4K4 w - - 0 1",
            "车兵对单炮：兵过宫、车护兵，炮只能远远看着——别让炮换掉兵。"
        ),
        Puzzle(
            16, "双车带兵",
            "2bakab2/9/R7R/4P4/9/9/9/9/9/4K4 w - - 0 1",
            "双车加过河兵打士象全，纯粹的子力碾压：双车占肋，兵往花心一坐就收官。"
        ),
        Puzzle(
            10, "卧槽马加炮",
            "2bakab2/9/2N6/9/9/9/4C4/9/9/4K4 w - - 0 1",
            "马已卧槽，炮在中路：马后炮的架子搭好了，就看你怎么收口。"
        )
    )

    /** The daily puzzle: stable within a day, rotates over the set. */
    fun daily(dayOfYear: Int): Puzzle = all.sortedBy { it.id }[dayOfYear % all.size]
}

/** Curated positions for the guess-the-best-move trainer, grouped
 *  by difficulty. The engine's top move is the answer; the position
 *  only needs to be interesting, not forced. */
object GuessPositions {
    val easy: List<Pair<String, String>> = listOf(
        "车兵对单马局面" to "4k4/9/2n6/4P4/9/9/R8/9/9/4K4 w - - 0 1",
        "车兵对双士局面" to "3aka3/9/9/4P4/9/9/8R/9/9/4K4 w - - 0 1",
        "马兵对士象全" to "2bakab2/9/9/3NP4/9/9/9/9/9/3K5 w - - 0 1",
        "马炮对士象全" to "2bakab2/9/4N4/9/9/4C4/9/9/9/4K4 w - - 0 1"
    )
    val normal: List<Pair<String, String>> = listOf(
        "标准开局" to
            "rnbakabnr/9/1c5c1/p1p1p1p1p/9/9/P1P1P1P1P/1C5C1/9/RNBAKABNR w - - 0 1",
        "中炮对进马" to
            "rnbakab1r/9/1c4nc1/p1p1p1p1p/9/9/P1P1P1P1P/1C2C4/9/RNBAKABNR w - - 0 2",
        "双车炮对士象全" to "2bakab2/9/3R1R3/9/4C4/9/9/9/9/4K4 w - - 0 1",
        "车马对士象全" to "2bakab2/9/3R1N3/9/9/9/9/9/9/3K5 w - - 0 1"
    )
    val hard: List<Pair<String, String>> = listOf(
        "单车对马炮" to "4k4/9/2c6/6n2/4R4/9/9/9/9/4K4 w - - 0 1",
        "重炮加车" to "3ak4/9/4C4/4C4/9/9/9/9/R8/4K4 w - - 0 1",
        "双炮兵对士象全" to "2bakab2/9/9/4P4/2C3C2/9/9/9/9/4K4 w - - 0 1",
        "卧槽马加炮" to "2bakab2/9/2N6/9/9/9/4C4/9/9/4K4 w - - 0 1"
    )
}

/** One wrong-book entry: a position the user guessed wrong on,
 *  with the engine's best move at that moment. */
data class WrongEntry(
    val fen: String,
    val correctUci: String,
    val title: String,
    val dateMs: Long
)

class WrongBookStore(context: Context) {
    private val file = File(context.applicationContext.filesDir, "wrongbook.json")

    fun loadAll(): List<WrongEntry> {
        if (!file.exists()) return emptyList()
        return try {
            val arr = JSONArray(file.readText())
            val out = ArrayList<WrongEntry>()
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                out.add(
                    WrongEntry(
                        fen = o.getString("fen"),
                        correctUci = o.optString("uci", ""),
                        title = o.optString("title", "错题"),
                        dateMs = o.optLong("dateMs", 0L)
                    )
                )
            }
            out.sortedByDescending { it.dateMs }
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun add(entry: WrongEntry) {
        val all = loadAll().filter { it.fen != entry.fen } + entry
        writeAll(all)
    }

    fun remove(fen: String) {
        writeAll(loadAll().filter { it.fen != fen })
    }

    fun replaceAll(entries: List<WrongEntry>) {
        writeAll(entries)
    }

    private fun writeAll(entries: List<WrongEntry>) {
        val arr = JSONArray()
        for (e in entries) {
            val o = JSONObject()
            o.put("fen", e.fen)
            o.put("uci", e.correctUci)
            o.put("title", e.title)
            o.put("dateMs", e.dateMs)
            arr.put(o)
        }
        file.writeText(arr.toString())
    }
}
