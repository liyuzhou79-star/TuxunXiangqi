package com.tuxunxiangqi.android.data

import android.content.Context
import com.tuxunxiangqi.android.xiangqi.Fen
import com.tuxunxiangqi.android.xiangqi.GameState
import com.tuxunxiangqi.android.xiangqi.OpeningBook
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

/** One saved game record. Moves are UCI strings from the initial
 *  position; [evals] holds Red-perspective centipawns keyed by the
 *  move count at which they were measured (sparse); [marks]
 *  holds whole-game-analysis verdicts keyed by 1-based move
 *  number ("?" questionable, "??" blunder). */
data class GameRecord(
    val id: Long,
    val title: String,
    val dateMs: Long,
    val modeLabel: String,
    val resultText: String,
    val initialFen: String,
    val movesUci: List<String>,
    val evals: Map<Int, Int>,
    val marks: Map<Int, String> = emptyMap(),
    /** Deep single-step analysis results (1.33.0), keyed by the
     *  review page's step index (0 = initial position); each
     *  value is the [encodeDeepStep] form of that step's engine
     *  candidates. Before 1.33.0 deep results lived only in the
     *  review page's memory and were lost on exit; now they are
     *  saved with the record and travel in exports. */
    val deep: Map<Int, String> = emptyMap()
)

/** One candidate of a stored deep-analysis step: the move, its
 *  score from the side to move (centipawns or mate distance,
 *  exactly one of the two, or neither when the engine gave no
 *  score) and the principal variation as UCI moves. */
data class DeepStepCandidate(
    val moveUci: String,
    val scoreCp: Int?,
    val mateIn: Int?,
    val pvUci: List<String>
)

/** Encodes one step's deep candidates into a single line:
 *  candidates joined by ";", each "move,score,pv" where score
 *  is "c<centipawns>", "m<mate in>" or "-" and pv is up to 12
 *  UCI moves joined by spaces (possibly empty). The alphabet
 *  deliberately excludes "|" and ":" so a whole record's steps
 *  can ride one portable-text line as "idx:encoded" pairs
 *  joined by " | ". Pure, pinned by DeepCodecTest. */
fun encodeDeepStep(candidates: List<DeepStepCandidate>): String =
    candidates.joinToString(";") { c ->
        val score = when {
            c.scoreCp != null -> "c" + c.scoreCp
            c.mateIn != null -> "m" + c.mateIn
            else -> "-"
        }
        c.moveUci + "," + score + "," +
            c.pvUci.take(12).joinToString(" ")
    }

/** Parses the [encodeDeepStep] form back; malformed candidates
 *  are skipped, so one bad entry never loses the whole step. */
fun parseDeepStep(text: String): List<DeepStepCandidate> {
    val out = ArrayList<DeepStepCandidate>()
    for (part in text.split(";")) {
        val fields = part.trim().split(",", limit = 3)
        if (fields.size < 2) continue
        val move = fields[0].trim()
        if (move.isBlank()) continue
        val token = fields[1].trim()
        val cp = if (token.startsWith("c")) token.drop(1).toIntOrNull() else null
        val mate = if (token.startsWith("m")) token.drop(1).toIntOrNull() else null
        val pv = if (fields.size == 3) {
            fields[2].split(Regex("\\s+")).filter { it.isNotBlank() }
        } else {
            emptyList()
        }
        out.add(DeepStepCandidate(move, cp, mate, pv))
    }
    return out
}

/** Plain JSON file storage for game records (no database needed at
 *  this scale). All calls are synchronous file IO on the caller's
 *  thread; records are small. */
class RecordStore(context: Context) {

    private val file = File(context.applicationContext.filesDir, "records.json")

    fun loadAll(): List<GameRecord> {
        if (!file.exists()) return emptyList()
        return try {
            val arr = JSONArray(file.readText())
            val out = ArrayList<GameRecord>()
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                val evalsObj = o.optJSONObject("evals") ?: JSONObject()
                val evals = HashMap<Int, Int>()
                val keys = evalsObj.keys()
                while (keys.hasNext()) {
                    val k = keys.next()
                    evals[k.toInt()] = evalsObj.getInt(k)
                }
                val moves = ArrayList<String>()
                val movesArr = o.optJSONArray("moves") ?: JSONArray()
                for (j in 0 until movesArr.length()) moves.add(movesArr.getString(j))
                val marksObj = o.optJSONObject("marks") ?: JSONObject()
                val marks = HashMap<Int, String>()
                val mKeys = marksObj.keys()
                while (mKeys.hasNext()) {
                    val k = mKeys.next()
                    marks[k.toInt()] = marksObj.getString(k)
                }
                val deepObj = o.optJSONObject("deep") ?: JSONObject()
                val deep = HashMap<Int, String>()
                val dKeys = deepObj.keys()
                while (dKeys.hasNext()) {
                    val k = dKeys.next()
                    val step = k.toIntOrNull()
                    val encoded = deepObj.optString(k, "")
                    if (step != null && encoded.isNotBlank()) {
                        deep[step] = encoded
                    }
                }
                out.add(
                    GameRecord(
                        id = o.getLong("id"),
                        title = o.optString("title", "未命名对局"),
                        dateMs = o.optLong("dateMs", 0L),
                        modeLabel = o.optString("mode", ""),
                        resultText = o.optString("result", ""),
                        initialFen = o.optString("fen", ""),
                        movesUci = moves,
                        evals = evals,
                        marks = marks,
                        deep = deep
                    )
                )
            }
            out.sortedByDescending { it.dateMs }
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun save(record: GameRecord) {
        val all = loadAll().filter { it.id != record.id } + record
        writeAll(all)
    }

    fun delete(id: Long) {
        writeAll(loadAll().filter { it.id != id })
    }

    /** Batch delete in a single file rewrite (the multi-select
     *  mode in the library); deleting one by one would rewrite
     *  the whole file per record. */
    fun deleteMany(ids: Set<Long>) {
        if (ids.isEmpty()) return
        writeAll(loadAll().filter { it.id !in ids })
    }

    fun rename(id: Long, newTitle: String) {
        writeAll(loadAll().map {
            if (it.id == id) it.copy(title = newTitle) else it
        })
    }

    fun replaceAll(records: List<GameRecord>) {
        writeAll(records)
    }

    private fun writeAll(records: List<GameRecord>) {
        val arr = JSONArray()
        for (r in records) {
            val o = JSONObject()
            o.put("id", r.id)
            o.put("title", r.title)
            o.put("dateMs", r.dateMs)
            o.put("mode", r.modeLabel)
            o.put("result", r.resultText)
            o.put("fen", r.initialFen)
            val moves = JSONArray()
            for (m in r.movesUci) moves.put(m)
            o.put("moves", moves)
            val evals = JSONObject()
            for ((k, v) in r.evals) evals.put(k.toString(), v)
            o.put("evals", evals)
            val marks = JSONObject()
            for ((k, v) in r.marks) marks.put(k.toString(), v)
            o.put("marks", marks)
            val deep = JSONObject()
            for ((k, v) in r.deep) deep.put(k.toString(), v)
            o.put("deep", deep)
            arr.put(o)
        }
        file.writeText(arr.toString())
    }
}

/** Position key of a FEN: board layout plus side to move. The
 *  halfmove/fullmove counters are excluded on purpose — records
 *  always store fullmove 1 for their start position while a
 *  replayed book line counts real fullmoves, so the counters
 *  would never match even for the very same position. */
fun fenPositionKey(fen: String): String {
    val fields = fen.trim().split(Regex("\\s+"))
    return when {
        fields.size >= 2 -> fields[0] + " " + fields[1]
        fields.size == 1 -> fields[0]
        else -> ""
    }
}

/** Position key -> the book history reaching it, for every node
 *  in the opening book (the root entry is the standard start).
 *  Built once by replaying each line on the real state machine. */
private val bookPositionHistories: Map<String, List<String>> by lazy {
    val map = LinkedHashMap<String, List<String>>()
    for (node in OpeningBook.allNodes()) {
        val gs = GameState()
        var ok = true
        for (uci in node.historyUci) {
            val ft = Fen.parseUciMove(uci)
            if (ft == null || !gs.tryMove(ft.first, ft.second)) {
                ok = false
                break
            }
        }
        if (ok) map.putIfAbsent(fenPositionKey(Fen.toFen(gs)), node.historyUci)
    }
    map
}

private val standardStartKey: String by lazy {
    fenPositionKey(Fen.toFen(GameState()))
}

/** Where this record's start position came from, judged purely
 *  by the position itself: the standard start, a position the
 *  built-in opening book reaches (a game started from a book
 *  node), or anything else — a hand-set editor position, which
 *  is also where imported records with unknown positions land.
 *  Pinned by RecordSourceTest. */
fun GameRecord.sourceLabel(): String {
    val key = fenPositionKey(initialFen)
    if (key.isEmpty()) return "摆棋开局"
    if (key == standardStartKey) return "标准开局"
    val hist = bookPositionHistories[key]
    return if (hist != null && hist.isNotEmpty()) "定式开局" else "摆棋开局"
}

/** For a book start, the book line that reaches the position,
 *  in Chinese notation; empty for the other sources. */
fun GameRecord.sourceNote(): String {
    val key = fenPositionKey(initialFen)
    if (key.isEmpty() || key == standardStartKey) return ""
    val hist = bookPositionHistories[key] ?: return ""
    if (hist.isEmpty()) return ""
    return OpeningBook.historyNotation(hist).joinToString(" · ")
}

/** Whether this record is an unfinished game that can be
 *  picked up and played on (1.24.0): BoardScreen writes
 *  "对局进行中" for every mid-game save, and only human games
 *  can be continued — a spectacle was a show, not a game
 *  anyone was playing. */
fun GameRecord.canContinue(): Boolean =
    resultText == "对局进行中" &&
        (modeLabel.startsWith("人机对弈") || modeLabel.startsWith("人人对弈"))

/** Rebuilds a resume snapshot from an unfinished record so the
 *  game can be continued exactly where it stopped: same start
 *  position, same moves, same level / side / handicap (all
 *  parsed back out of the mode label BoardScreen wrote) and
 *  the record's id, so finishing overwrites this record
 *  instead of leaving a stale unfinished copy behind.
 *  Records never stored the strength slider or the clock, so
 *  the caller passes the current settings values and the
 *  clock restarts full. Null when [canContinue] is false.
 *  Pinned by RecordContinueTest. */
fun GameRecord.toResumeGame(strength: Int, clockMinutes: Int): ResumeGame? {
    if (!canContinue()) return null
    val ai = modeLabel.startsWith("人机对弈")
    val segments = modeLabel.split(" · ")
    return ResumeGame(
        modeName = if (ai) "AI" else "PVP",
        levelLabel = if (ai) segments.getOrNull(1) ?: "普通" else "普通",
        humanSideRed = !ai || modeLabel.contains("执红"),
        handicapLabel = segments.firstOrNull { it.startsWith("让") } ?: "不让子",
        strength = strength,
        clockMinutes = clockMinutes,
        redMs = clockMinutes * 60_000L,
        blackMs = clockMinutes * 60_000L,
        initialFen = initialFen,
        movesUci = movesUci,
        evals = evals,
        savedAtMs = System.currentTimeMillis(),
        recordId = id
    )
}

/** The library screen's filter: mode chip, result chip and a
 *  free-text match on title, result or start-position source.
 *  Result chips match the resultText prefixes BoardScreen
 *  writes ("红方胜 · …", "黑方胜 · …", "和棋 · …"); "未分胜负"
 *  keeps the rest (mid-game saves and result-less finishes).
 *  Pure and allocation-light so a library of hundreds of
 *  records filters instantly; pinned by RecordsPerfTest at
 *  200 records. */
fun filterRecords(
    records: List<GameRecord>,
    filter: String,
    query: String,
    result: String = "全部结果"
): List<GameRecord> {
    val q = query.trim()
    return records.filter { r ->
        val resultOk = when (result) {
            "全部结果" -> true
            "未分胜负" -> !r.resultText.startsWith("红方胜") &&
                !r.resultText.startsWith("黑方胜") &&
                !r.resultText.startsWith("和棋")
            else -> r.resultText.startsWith(result)
        }
        (filter == "全部" || r.modeLabel.startsWith(filter)) && resultOk &&
            (q.isBlank() || r.title.contains(q) || r.resultText.contains(q) ||
                r.sourceLabel().contains(q))
    }
}

/** Library sort orders (1.21.0). "最新在前" is the historical
 *  default (loadAll already returns date-descending); every
 *  other order falls back to newest-first on ties so the list
 *  never reshuffles between visits. Pure: pinned by
 *  SortRecordsTest. */
fun sortRecords(records: List<GameRecord>, sort: String): List<GameRecord> =
    when (sort) {
        "最早在前" -> records.sortedWith(
            compareBy<GameRecord> { it.dateMs }.thenBy { it.id }
        )
        "手数多到少" -> records.sortedWith(
            compareByDescending<GameRecord> { it.movesUci.size }
                .thenByDescending { it.dateMs }
        )
        "手数少到多" -> records.sortedWith(
            compareBy<GameRecord> { it.movesUci.size }
                .thenByDescending { it.dateMs }
        )
        "标题" -> records.sortedWith(
            compareBy<GameRecord> { it.title }.thenByDescending { it.dateMs }
        )
        else -> records.sortedWith(
            compareByDescending<GameRecord> { it.dateMs }.thenBy { it.id }
        )
    }

/** Serialises one record into the portable text format used for
 *  export/import: header lines plus a UCI move line, so a record
 *  can travel by clipboard, share sheet or file. */
fun GameRecord.toPortableText(): String {
    val sb = StringBuilder()
    sb.append("[将！棋谱] ").append(title).append('\n')
    sb.append("[模式] ").append(modeLabel).append('\n')
    sb.append("[结果] ").append(resultText).append('\n')
    sb.append("[FEN] ").append(initialFen).append('\n')
    sb.append("[着法UCI] ").append(movesUci.joinToString(" ")).append('\n')
    if (evals.isNotEmpty()) {
        sb.append("[分数] ")
        sb.append(evals.entries.sortedBy { it.key }
            .joinToString(" ") { "${it.key}:${it.value}" })
        sb.append('\n')
    }
    if (marks.isNotEmpty()) {
        sb.append("[标记] ")
        sb.append(marks.entries.sortedBy { it.key }
            .joinToString(" ") { "${it.key}:${it.value}" })
        sb.append('\n')
    }
    if (deep.isNotEmpty()) {
        sb.append("[深析] ")
        sb.append(deep.entries.sortedBy { it.key }
            .joinToString(" | ") { "${it.key}:${it.value}" })
        sb.append('\n')
    }
    return sb.toString()
}

/** Serialises several records into one portable text bundle:
 *  the per-record blocks back to back. The matching reader is
 *  [parsePortableRecords], so a whole library (or any selection)
 *  can travel as one text and come back with the same count. */
fun List<GameRecord>.toPortableTextBundle(): String =
    joinToString("\n") { it.toPortableText() }

/** Parses a portable text bundle back into records. A new block
 *  starts at each [将！棋谱] header line; text without any header
 *  is treated as a single block, so single-record exports from
 *  older versions import exactly as before. Blocks that fail to
 *  parse are skipped. Ids are fallbackId, fallbackId + 1, ... by
 *  block position so imported records never collide with each
 *  other in the store (which dedupes by id). */
fun parsePortableRecords(text: String, fallbackId: Long): List<GameRecord> {
    val blocks = ArrayList<List<String>>()
    var current = ArrayList<String>()
    for (raw in text.lines()) {
        if (raw.trim().startsWith("[将！棋谱]") && current.any { it.isNotBlank() }) {
            blocks.add(current)
            current = ArrayList()
        }
        current.add(raw)
    }
    if (current.any { it.isNotBlank() }) blocks.add(current)
    return blocks.mapIndexedNotNull { i, lines ->
        parsePortableBlock(lines, fallbackId + i)
    }
}

/** Parses the portable text format back; null when unusable.
 *  For a multi-record bundle this returns the first record;
 *  [parsePortableRecords] returns them all. */
fun parsePortableRecord(text: String, fallbackId: Long): GameRecord? =
    parsePortableRecords(text, fallbackId).firstOrNull()

private fun parsePortableBlock(lines: List<String>, id: Long): GameRecord? {
    var title = "导入的棋谱"
    var mode = ""
    var result = ""
    var fen = ""
    var moves: List<String> = emptyList()
    val evals = HashMap<Int, Int>()
    val marks = HashMap<Int, String>()
    val deep = HashMap<Int, String>()
    for (raw in lines) {
        val line = raw.trim()
        fun value(tag: String): String? =
            if (line.startsWith("[$tag]")) line.removePrefix("[$tag]").trim() else null
        value("将！棋谱")?.let { title = it }
        value("模式")?.let { mode = it }
        value("结果")?.let { result = it }
        value("FEN")?.let { fen = it }
        value("着法UCI")?.let {
            moves = it.split(Regex("\\s+")).filter { m -> m.isNotBlank() }
        }
        value("分数")?.let {
            for (part in it.split(Regex("\\s+"))) {
                val kv = part.split(":")
                if (kv.size == 2) {
                    val k = kv[0].toIntOrNull()
                    val v = kv[1].toIntOrNull()
                    if (k != null && v != null) evals[k] = v
                }
            }
        }
        value("标记")?.let {
            for (part in it.split(Regex("\\s+"))) {
                val kv = part.split(":")
                if (kv.size == 2) {
                    val k = kv[0].toIntOrNull()
                    if (k != null && kv[1].isNotBlank()) marks[k] = kv[1]
                }
            }
        }
        value("深析")?.let {
            for (part in it.split("|")) {
                val trimmed = part.trim()
                val colon = trimmed.indexOf(':')
                if (colon > 0) {
                    val k = trimmed.substring(0, colon).toIntOrNull()
                    val v = trimmed.substring(colon + 1).trim()
                    if (k != null && v.isNotBlank()) deep[k] = v
                }
            }
        }
    }
    if (fen.isBlank()) return null
    return GameRecord(
        id = id,
        title = title,
        dateMs = System.currentTimeMillis(),
        modeLabel = mode,
        resultText = result,
        initialFen = fen,
        movesUci = moves,
        evals = evals,
        marks = marks,
        deep = deep
    )
}
