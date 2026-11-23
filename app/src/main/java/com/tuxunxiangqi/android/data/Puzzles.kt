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
        ),
        Puzzle(
            17, "车炮胜士象全",
            "2bakab2/9/9/9/4C4/9/9/3R5/9/4K4 w - - 0 1",
            "车炮配合打士象全：炮占中路，车扫边路，先破一士再收官。"
        ),
        Puzzle(
            18, "双马胜士象全",
            "2bakab2/9/2N3N2/9/9/9/9/9/9/3K5 w - - 0 1",
            "双马打士象全是出了名的耐心局：双马轮流占位，别给黑将透气的口。"
        ),
        Puzzle(
            19, "车马炮联攻",
            "2bakab2/9/3R5/4N4/4C4/9/9/9/9/4K4 w - - 0 1",
            "车马炮三件套都在：车占肋、马卧槽、炮镇中，轮着施压别贪吃。"
        ),
        Puzzle(
            20, "单车胜士象全",
            "2bakab2/9/9/9/9/9/9/3R5/9/3K5 w - - 0 1",
            "单车打士象全靠的是耐心和位置：车先占中，再逐个敲掉士象。"
        ),
        Puzzle(
            21, "单车对马双士",
            "3aka3/9/9/2n6/9/9/9/3R5/9/3K5 w - - 0 1",
            "车打马双士：先捉马，再破士。马跳得欢，车要卡住它的落点。"
        ),
        Puzzle(
            22, "双炮胜士象全",
            "2bakab2/9/9/9/2C3C2/9/9/9/9/3K5 w - - 0 1",
            "双炮打士象全：两门炮轮流借将作架，一门封口，一门打子。"
        ),
        Puzzle(
            23, "马炮兵对单马",
            "4k4/9/9/2n1N4/4P4/9/4C4/9/9/3K5 w - - 0 1",
            "马炮兵打单马：兵过河压宫，马炮在后面跟上，把黑马兑掉就稳了。"
        ),
        Puzzle(
            24, "车炮兵攻士象全",
            "2bakab2/9/9/4P4/9/4C4/9/3R5/9/4K4 w - - 0 1",
            "车炮兵三线合围：兵坐花心当炮架，车炮轮番轰，先破防线再收官。"
        ),
        Puzzle(
            25, "双车马对士象全",
            "2bakab2/9/9/3N5/R8/8R/9/9/9/3K5 w - - 0 1",
            "双车加马打士象全，子力碾压：车占要道、马踏中士，破防只是时间问题。"
        ),
        Puzzle(
            26, "车炮双兵压宫",
            "2bakab2/9/9/3P1P3/9/3R5/7C1/9/9/3K5 w - - 0 1",
            "车炮带着双兵压境：兵往宫里拱，炮借兵架，车随时准备收官。"
        ),
        Puzzle(
            27, "马双炮带兵",
            "2bakab2/9/9/4P4/4N4/7C1/1C7/9/9/4K4 w - - 0 1",
            "马、双炮加过河兵：炮靠兵和马做架子，轮番轰宫心，守方顾此失彼。"
        ),
        Puzzle(
            28, "双车对马炮",
            "4k4/9/2n3c2/9/8R/9/R8/9/9/3K5 w - - 0 1",
            "双车对马炮：先捉炮、再困马，双车成形后一路平推。"
        ),
        Puzzle(
            29, "车马对双炮双士",
            "3aka3/9/1c5c1/9/5N3/2R6/9/9/9/3K5 w - - 0 1",
            "车马打双炮：车捉炮、马踏士，兑掉一炮后局面就透了。"
        ),
        Puzzle(
            30, "车带三兵胜士象全",
            "2bakab2/9/9/4P4/1RP3P2/9/9/9/9/4K4 w - - 0 1",
            "一车带三个过河兵：兵兵相护往前拱，车在后头撑腰，士象全也挡不住。"
        ),
        Puzzle(
            31, "双车炮兵齐攻",
            "2bakab2/9/9/3P5/9/R7R/4C4/9/9/4K4 w - - 0 1",
            "双车一炮一兵全压上：先手在握，逐个敲掉守子再收官。"
        ),
        Puzzle(
            32, "车马双兵对士象全",
            "2bakab2/9/9/6N2/3P1P3/2R6/9/9/9/3K5 w - - 0 1",
            "车马双兵：兵拱宫、马跳卧槽、车占肋道，三路一起施压。"
        )
    )

    /** The daily puzzle: stable within a day, rotates over the set. */
    fun daily(dayOfYear: Int): Puzzle = all.sortedBy { it.id }[dayOfYear % all.size]

    /** Display grouping by the lead attacking material, in the
     *  order the list page shows them. */
    val categoryOrder: List<String> =
        listOf("双车类", "车马类", "车炮类", "车兵类", "马炮类", "双炮双马类")

    private val categoryById: Map<Int, String> = mapOf(
        1 to "双车类", 16 to "双车类", 25 to "双车类", 28 to "双车类", 31 to "双车类",
        3 to "车马类", 11 to "车马类", 19 to "车马类", 29 to "车马类", 32 to "车马类",
        9 to "车炮类", 17 to "车炮类", 24 to "车炮类", 26 to "车炮类",
        4 to "车兵类", 5 to "车兵类", 8 to "车兵类", 13 to "车兵类",
        15 to "车兵类", 20 to "车兵类", 21 to "车兵类", 30 to "车兵类",
        2 to "马炮类", 7 to "马炮类", 10 to "马炮类", 12 to "马炮类",
        14 to "马炮类", 23 to "马炮类",
        6 to "双炮双马类", 18 to "双炮双马类", 22 to "双炮双马类", 27 to "双炮双马类"
    )

    /** 1 = material crush, 2 = standard textbook win,
     *  3 = sparse or patience finish that is easy to throw away. */
    private val difficultyById: Map<Int, Int> = mapOf(
        1 to 1, 16 to 1, 19 to 1, 25 to 1, 28 to 1, 31 to 1,
        7 to 3, 8 to 3, 18 to 3, 20 to 3, 22 to 3
    )

    fun categoryOf(id: Int): String = categoryById[id] ?: "综合类"

    fun difficultyOf(id: Int): Int = difficultyById[id] ?: 2

    /** The list page's filter (1.19.0): keep puzzles of one
     *  difficulty tier (null = all tiers) and optionally only
     *  the ones not solved yet. Pure, so the tier distribution
     *  and the unsolved logic are pinned by unit tests. */
    fun filterPuzzles(
        list: List<Puzzle>,
        difficulty: Int?,
        unsolvedOnly: Boolean,
        solvedIds: Set<Int>
    ): List<Puzzle> = list.filter { p ->
        (difficulty == null || difficultyOf(p.id) == difficulty) &&
            (!unsolvedOnly || p.id !in solvedIds)
    }

    /** Ids lacking an explicit category entry — the unit test
     *  pins this to empty so a new puzzle can never silently land
     *  in the fallback bucket. (Difficulty defaults to 2 by
     *  design; only the extremes are listed.) */
    fun idsWithoutCategory(): Set<Int> =
        all.map { it.id }.filter { it !in categoryById }.toSet()
}

/** Curated positions for the guess-the-best-move trainer, grouped
 *  by difficulty (8 per tier since 1.5.0). The engine's top move
 *  is the answer; the position only needs to be interesting, not
 *  forced. The endgame entries reuse Puzzles faces verbatim, so
 *  they inherit the same FEN sanity coverage in PuzzleFenTest. */
object GuessPositions {
    val easy: List<Pair<String, String>> = listOf(
        "车兵对单马局面" to "4k4/9/2n6/4P4/9/9/R8/9/9/4K4 w - - 0 1",
        "车兵对双士局面" to "3aka3/9/9/4P4/9/9/8R/9/9/4K4 w - - 0 1",
        "马兵对士象全" to "2bakab2/9/9/3NP4/9/9/9/9/9/3K5 w - - 0 1",
        "马炮对士象全" to "2bakab2/9/4N4/9/9/4C4/9/9/9/4K4 w - - 0 1",
        "双车带兵局面" to "2bakab2/9/R7R/4P4/9/9/9/9/9/4K4 w - - 0 1",
        "双车马对士象全" to "2bakab2/9/9/3N5/R8/8R/9/9/9/3K5 w - - 0 1",
        "双车炮兵齐攻" to "2bakab2/9/9/3P5/9/R7R/4C4/9/9/4K4 w - - 0 1",
        "车马炮联攻局面" to "2bakab2/9/3R5/4N4/4C4/9/9/9/9/4K4 w - - 0 1"
    )
    val normal: List<Pair<String, String>> = listOf(
        "标准开局" to
            "rnbakabnr/9/1c5c1/p1p1p1p1p/9/9/P1P1P1P1P/1C5C1/9/RNBAKABNR w - - 0 1",
        "中炮对进马" to
            "rnbakab1r/9/1c4nc1/p1p1p1p1p/9/9/P1P1P1P1P/1C2C4/9/RNBAKABNR w - - 0 2",
        "双车炮对士象全" to "2bakab2/9/3R1R3/9/4C4/9/9/9/9/4K4 w - - 0 1",
        "车马对士象全" to "2bakab2/9/3R1N3/9/9/9/9/9/9/3K5 w - - 0 1",
        "车炮胜士象全" to "2bakab2/9/9/9/4C4/9/9/3R5/9/4K4 w - - 0 1",
        "车炮兵攻士象全" to "2bakab2/9/9/4P4/9/4C4/9/3R5/9/4K4 w - - 0 1",
        "马炮兵对士象全" to "2bakab2/9/9/3NP4/9/4C4/9/9/9/3K5 w - - 0 1",
        "双车对马炮" to "4k4/9/2n3c2/9/8R/9/R8/9/9/3K5 w - - 0 1"
    )
    val hard: List<Pair<String, String>> = listOf(
        "单车对马炮" to "4k4/9/2c6/6n2/4R4/9/9/9/9/4K4 w - - 0 1",
        "重炮加车" to "3ak4/9/4C4/4C4/9/9/9/9/R8/4K4 w - - 0 1",
        "双炮兵对士象全" to "2bakab2/9/9/4P4/2C3C2/9/9/9/9/4K4 w - - 0 1",
        "卧槽马加炮" to "2bakab2/9/2N6/9/9/9/4C4/9/9/4K4 w - - 0 1",
        "双马胜士象全" to "2bakab2/9/2N3N2/9/9/9/9/9/9/3K5 w - - 0 1",
        "单车胜士象全" to "2bakab2/9/9/9/9/9/9/3R5/9/3K5 w - - 0 1",
        "双炮胜士象全" to "2bakab2/9/9/9/2C3C2/9/9/9/9/3K5 w - - 0 1",
        "单车对炮双士" to "3aka3/9/9/9/4c4/9/R8/9/9/4K4 w - - 0 1"
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
