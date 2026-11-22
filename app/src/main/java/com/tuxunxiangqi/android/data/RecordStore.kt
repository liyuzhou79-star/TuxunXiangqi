package com.tuxunxiangqi.android.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

/** One saved game record. Moves are UCI strings from the initial
 *  position; [evals] holds Red-perspective centipawns keyed by the
 *  move count at which they were measured (sparse). */
data class GameRecord(
    val id: Long,
    val title: String,
    val dateMs: Long,
    val modeLabel: String,
    val resultText: String,
    val initialFen: String,
    val movesUci: List<String>,
    val evals: Map<Int, Int>
)

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
                out.add(
                    GameRecord(
                        id = o.getLong("id"),
                        title = o.optString("title", "未命名对局"),
                        dateMs = o.optLong("dateMs", 0L),
                        modeLabel = o.optString("mode", ""),
                        resultText = o.optString("result", ""),
                        initialFen = o.optString("fen", ""),
                        movesUci = moves,
                        evals = evals
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
            arr.put(o)
        }
        file.writeText(arr.toString())
    }
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
    return sb.toString()
}

/** Parses the portable text format back; null when unusable. */
fun parsePortableRecord(text: String, fallbackId: Long): GameRecord? {
    var title = "导入的棋谱"
    var mode = ""
    var result = ""
    var fen = ""
    var moves: List<String> = emptyList()
    val evals = HashMap<Int, Int>()
    for (raw in text.lines()) {
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
    }
    if (fen.isBlank()) return null
    return GameRecord(
        id = fallbackId,
        title = title,
        dateMs = System.currentTimeMillis(),
        modeLabel = mode,
        resultText = result,
        initialFen = fen,
        movesUci = moves,
        evals = evals
    )
}
