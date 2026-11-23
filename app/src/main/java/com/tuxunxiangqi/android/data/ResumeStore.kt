package com.tuxunxiangqi.android.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

/** A game in progress, snapshotted so it can be continued later:
 *  everything needed to rebuild the board (initial position plus
 *  the moves played) and the setup it was played under (mode,
 *  level, side, handicap, strength, clock). Single slot: starting
 *  or finishing a game replaces / clears it. */
data class ResumeGame(
    val modeName: String,
    val levelLabel: String,
    val humanSideRed: Boolean,
    val handicapLabel: String,
    val strength: Int,
    val clockMinutes: Int,
    val redMs: Long,
    val blackMs: Long,
    val initialFen: String,
    val movesUci: List<String>,
    val evals: Map<Int, Int>,
    val savedAtMs: Long,
    /** When this game was continued from a library record
     *  (1.24.0), the record it came from: finishing the game
     *  overwrites that record instead of adding a second one.
     *  Null for games started fresh or snapshotted before the
     *  field existed. */
    val recordId: Long? = null
) {
    /** Short human summary for the home-screen card. */
    fun summary(): String {
        val mode = if (modeName == "AI") "人机对弈 · $levelLabel" else "人人对弈"
        return mode + " · 已下 " + movesUci.size + " 手"
    }
}

/** JSON file storage for the single in-progress game. */
class ResumeStore(context: Context) {

    private val file = File(context.applicationContext.filesDir, "inprogress.json")

    fun load(): ResumeGame? {
        if (!file.exists()) return null
        return try {
            val o = JSONObject(file.readText())
            val moves = ArrayList<String>()
            val movesArr = o.optJSONArray("moves") ?: JSONArray()
            for (j in 0 until movesArr.length()) moves.add(movesArr.getString(j))
            val evals = HashMap<Int, Int>()
            val evalsObj = o.optJSONObject("evals") ?: JSONObject()
            val keys = evalsObj.keys()
            while (keys.hasNext()) {
                val k = keys.next()
                evals[k.toInt()] = evalsObj.getInt(k)
            }
            ResumeGame(
                modeName = o.optString("mode", "PVP"),
                levelLabel = o.optString("level", "普通"),
                humanSideRed = o.optBoolean("humanRed", true),
                handicapLabel = o.optString("handicap", "不让子"),
                strength = o.optInt("strength", 5),
                clockMinutes = o.optInt("clock", 0),
                redMs = o.optLong("redMs", 0L),
                blackMs = o.optLong("blackMs", 0L),
                initialFen = o.optString("fen", ""),
                movesUci = moves,
                evals = evals,
                savedAtMs = o.optLong("savedAt", 0L),
                recordId = if (o.has("recordId")) o.getLong("recordId") else null
            )
        } catch (_: Exception) {
            null
        }
    }

    fun save(game: ResumeGame) {
        try {
            val o = JSONObject()
            o.put("mode", game.modeName)
            o.put("level", game.levelLabel)
            o.put("humanRed", game.humanSideRed)
            o.put("handicap", game.handicapLabel)
            o.put("strength", game.strength)
            o.put("clock", game.clockMinutes)
            o.put("redMs", game.redMs)
            o.put("blackMs", game.blackMs)
            o.put("fen", game.initialFen)
            val moves = JSONArray()
            for (m in game.movesUci) moves.put(m)
            o.put("moves", moves)
            val evals = JSONObject()
            for ((k, v) in game.evals) evals.put(k.toString(), v)
            o.put("evals", evals)
            o.put("savedAt", game.savedAtMs)
            if (game.recordId != null) o.put("recordId", game.recordId)
            file.writeText(o.toString())
        } catch (_: Exception) {
            // A failed snapshot must never break the game itself.
        }
    }

    fun clear() {
        try {
            if (file.exists()) file.delete()
        } catch (_: Exception) {
        }
    }
}
