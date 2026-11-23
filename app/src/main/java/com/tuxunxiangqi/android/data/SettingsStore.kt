package com.tuxunxiangqi.android.data

import android.content.Context
import org.json.JSONObject

/** App preferences backed by SharedPreferences. Theme mode is
 *  "system" / "light" / "dark"; the rest are plain defaults used
 *  to pre-fill the game setup. */
class SettingsStore(context: Context) {

    private val prefs =
        context.applicationContext.getSharedPreferences("settings", Context.MODE_PRIVATE)

    var themeMode: String
        get() = prefs.getString("themeMode", "system") ?: "system"
        set(value) = prefs.edit().putString("themeMode", value).apply()

    var defaultLevelLabel: String
        get() = prefs.getString("defaultLevel", "普通") ?: "普通"
        set(value) = prefs.edit().putString("defaultLevel", value).apply()

    var defaultClockMinutes: Int
        get() = prefs.getInt("defaultClock", 0)
        set(value) = prefs.edit().putInt("defaultClock", value).apply()

    var defaultAnalysisMs: Int
        get() = prefs.getInt("analysisMs", 2000)
        set(value) = prefs.edit().putInt("analysisMs", value).apply()

    /** Sound effects on/off, everywhere. On by default. */
    var soundOn: Boolean
        get() = prefs.getBoolean("soundOn", true)
        set(value) = prefs.edit().putBoolean("soundOn", value).apply()

    /** Fine strength tuning for the AI game, 1..10 (default 5).
     *  Pikafish has no native strength option (verified against
     *  its full UCI option list), so this is simulated with
     *  thinking time and a search-depth cap — see BoardScreen. */
    var strengthLevel: Int
        get() = prefs.getInt("strengthLevel", 5).coerceIn(1, 10)
        set(value) = prefs.edit().putInt("strengthLevel", value.coerceIn(1, 10)).apply()

    /** Safety colours on the selected piece's legal targets:
     *  green = the piece survives there, red = it would be taken
     *  （送死）, green ring = a capture that cannot be answered,
     *  no marker = a capture that would be recaptured. Judged by
     *  the local rules layer only. On by default. */
    var safetyHintsOn: Boolean
        get() = prefs.getBoolean("safetyHintsOn", true)
        set(value) = prefs.edit().putBoolean("safetyHintsOn", value).apply()

    fun toJson(): JSONObject {
        val o = JSONObject()
        o.put("themeMode", themeMode)
        o.put("defaultLevel", defaultLevelLabel)
        o.put("defaultClock", defaultClockMinutes)
        o.put("analysisMs", defaultAnalysisMs)
        o.put("soundOn", soundOn)
        o.put("safetyHintsOn", safetyHintsOn)
        o.put("strengthLevel", strengthLevel)
        return o
    }

    fun applyJson(o: JSONObject) {
        themeMode = o.optString("themeMode", themeMode)
        defaultLevelLabel = o.optString("defaultLevel", defaultLevelLabel)
        defaultClockMinutes = o.optInt("defaultClock", defaultClockMinutes)
        defaultAnalysisMs = o.optInt("analysisMs", defaultAnalysisMs)
        soundOn = o.optBoolean("soundOn", soundOn)
        // Older backups have no safetyHintsOn key: keep the
        // current value then (optBoolean falls back to it).
        safetyHintsOn = o.optBoolean("safetyHintsOn", safetyHintsOn)
        strengthLevel = o.optInt("strengthLevel", strengthLevel)
    }
}

/** Full data backup: records + wrong book + settings in one JSON
 *  document. Engine files are not included (they ship in the APK). */
fun buildBackupJson(context: Context): String {
    val root = JSONObject()
    root.put("app", "jiang-xiangqi-backup")
    root.put("version", 1)
    val records = RecordStore(context).loadAll()
    val recArr = org.json.JSONArray()
    for (r in records) {
        val o = JSONObject()
        o.put("id", r.id)
        o.put("title", r.title)
        o.put("dateMs", r.dateMs)
        o.put("mode", r.modeLabel)
        o.put("result", r.resultText)
        o.put("fen", r.initialFen)
        val moves = org.json.JSONArray()
        for (m in r.movesUci) moves.put(m)
        o.put("moves", moves)
        val evals = JSONObject()
        for ((k, v) in r.evals) evals.put(k.toString(), v)
        o.put("evals", evals)
        recArr.put(o)
    }
    root.put("records", recArr)
    val wrong = WrongBookStore(context).loadAll()
    val wrongArr = org.json.JSONArray()
    for (e in wrong) {
        val o = JSONObject()
        o.put("fen", e.fen)
        o.put("uci", e.correctUci)
        o.put("title", e.title)
        o.put("dateMs", e.dateMs)
        wrongArr.put(o)
    }
    root.put("wrongbook", wrongArr)
    root.put("settings", SettingsStore(context).toJson())
    return root.toString()
}

/** Applies a backup document, replacing current data. Returns a
 *  human-readable result, or null when the text is not a backup. */
fun applyBackupJson(context: Context, text: String): String? {
    val root = try {
        JSONObject(text)
    } catch (_: Exception) {
        return null
    }
    if (root.optString("app") != "jiang-xiangqi-backup") return null
    val records = ArrayList<GameRecord>()
    val recArr = root.optJSONArray("records") ?: org.json.JSONArray()
    for (i in 0 until recArr.length()) {
        val o = recArr.getJSONObject(i)
        val evals = HashMap<Int, Int>()
        val evalsObj = o.optJSONObject("evals") ?: JSONObject()
        val keys = evalsObj.keys()
        while (keys.hasNext()) {
            val k = keys.next()
            evals[k.toInt()] = evalsObj.getInt(k)
        }
        val moves = ArrayList<String>()
        val movesArr = o.optJSONArray("moves") ?: org.json.JSONArray()
        for (j in 0 until movesArr.length()) moves.add(movesArr.getString(j))
        records.add(
            GameRecord(
                id = o.optLong("id", System.currentTimeMillis() + i),
                title = o.optString("title", "导入的棋谱"),
                dateMs = o.optLong("dateMs", 0L),
                modeLabel = o.optString("mode", ""),
                resultText = o.optString("result", ""),
                initialFen = o.optString("fen", ""),
                movesUci = moves,
                evals = evals
            )
        )
    }
    RecordStore(context).replaceAll(records)
    val wrong = ArrayList<WrongEntry>()
    val wrongArr = root.optJSONArray("wrongbook") ?: org.json.JSONArray()
    for (i in 0 until wrongArr.length()) {
        val o = wrongArr.getJSONObject(i)
        wrong.add(
            WrongEntry(
                fen = o.optString("fen", ""),
                correctUci = o.optString("uci", ""),
                title = o.optString("title", "错题"),
                dateMs = o.optLong("dateMs", 0L)
            )
        )
    }
    WrongBookStore(context).replaceAll(wrong.filter { it.fen.isNotBlank() })
    root.optJSONObject("settings")?.let { SettingsStore(context).applyJson(it) }
    return "恢复完成：棋谱 " + records.size + " 局，错题 " + wrong.size +
        " 道，设置已覆盖。主题若有变化，回到首页即可看到。"
}
