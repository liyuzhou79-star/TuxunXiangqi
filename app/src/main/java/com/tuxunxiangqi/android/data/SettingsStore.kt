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

    /** Remembered spectacle (AI vs AI) speed label, so the show
     *  reopens at the pace the user last picked. */
    var specSpeedLabel: String
        get() = prefs.getString("specSpeed", "中速") ?: "中速"
        set(value) = prefs.edit().putString("specSpeed", value).apply()

    /** Safety colours on the selected piece's legal targets:
     *  green = the piece survives there, red = it would be taken
     *  （送死）, green ring = a capture that cannot be answered,
     *  no marker = a capture that would be recaptured. Judged by
     *  the local rules layer only. On by default. */
    var safetyHintsOn: Boolean
        get() = prefs.getBoolean("safetyHintsOn", true)
        set(value) = prefs.edit().putBoolean("safetyHintsOn", value).apply()

    /** Wipes every stored preference. All getters carry their
     *  defaults, so clearing the file restores factory settings
     *  (theme, defaults, sound, safety hints, strength). Records
     *  and the wrong book live in other files and are untouched. */
    fun resetToDefaults() {
        prefs.edit().clear().apply()
    }

    fun toJson(): JSONObject {
        val o = JSONObject()
        o.put("themeMode", themeMode)
        o.put("defaultLevel", defaultLevelLabel)
        o.put("defaultClock", defaultClockMinutes)
        o.put("analysisMs", defaultAnalysisMs)
        o.put("soundOn", soundOn)
        o.put("safetyHintsOn", safetyHintsOn)
        o.put("strengthLevel", strengthLevel)
        o.put("specSpeed", specSpeedLabel)
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
        // Older backups have no specSpeed key: keep current then.
        specSpeedLabel = o.optString("specSpeed", specSpeedLabel)
    }
}

/** Full data backup: records + wrong book + settings + training
 *  progress (puzzle solves and guess statistics, since 1.22.0)
 *  in one JSON document. Engine files are not included (they
 *  ship in the APK). */
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
        val marks = JSONObject()
        for ((k, v) in r.marks) marks.put(k.toString(), v)
        o.put("marks", marks)
        val deep = JSONObject()
        for ((k, v) in r.deep) deep.put(k.toString(), v)
        o.put("deep", deep)
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
    root.put("puzzles", PuzzleProgressStore(context).load().encode())
    root.put("guessStats", encodeGuessStats(GuessStatsStore(context).allStats()))
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
        val marks = HashMap<Int, String>()
        val marksObj = o.optJSONObject("marks") ?: JSONObject()
        val mKeys = marksObj.keys()
        while (mKeys.hasNext()) {
            val k = mKeys.next()
            marks[k.toInt()] = marksObj.getString(k)
        }
        val deep = HashMap<Int, String>()
        val deepObj = o.optJSONObject("deep") ?: JSONObject()
        val dKeys = deepObj.keys()
        while (dKeys.hasNext()) {
            val k = dKeys.next()
            val step = k.toIntOrNull()
            val encoded = deepObj.optString(k, "")
            if (step != null && encoded.isNotBlank()) deep[step] = encoded
        }
        records.add(
            GameRecord(
                id = o.optLong("id", System.currentTimeMillis() + i),
                title = o.optString("title", "导入的棋谱"),
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
    // Training progress (1.22.0): only touched when the backup
    // actually carries it — backups from older versions have no
    // such keys and must not wipe progress made on this device.
    var progressNote = ""
    val puzzleText = root.optString("puzzles")
    if (puzzleText.isNotBlank()) {
        parsePuzzleProgress(puzzleText)?.let { p ->
            PuzzleProgressStore(context).replaceAll(p)
            progressNote = "，残局进度 " + p.solvedIds.size + " 题"
        }
    }
    val guessText = root.optString("guessStats")
    if (guessText.isNotBlank()) {
        parseGuessStats(guessText)?.let { g ->
            GuessStatsStore(context).replaceAll(g)
            progressNote += "，猜一选统计"
        }
    }
    return "恢复完成：棋谱 " + records.size + " 局，错题 " + wrong.size +
        " 道，设置已覆盖" + progressNote + "。主题若有变化，回到首页即可看到。"
}

/** Training-only portable document (1.30.0): puzzle progress
 *  and guess statistics, nothing else — for moving just the
 *  training record between devices, without touching games,
 *  the wrong book or settings. */
fun buildTrainingJson(context: Context): String {
    val root = JSONObject()
    root.put("app", "jiang-xiangqi-training")
    root.put("version", 1)
    root.put("puzzles", PuzzleProgressStore(context).load().encode())
    root.put("guessStats", encodeGuessStats(GuessStatsStore(context).allStats()))
    return root.toString()
}

/** Decodes the two training fields of a portable document.
 *  Pure and pinned by unit tests: each field applies only when
 *  present and parseable; null when neither is usable, so a
 *  document with no training data changes nothing. */
fun decodeTraining(
    puzzleText: String?,
    guessText: String?
): Pair<PuzzleProgress?, Map<String, GuessStat>?>? {
    val p = puzzleText?.takeIf { it.isNotBlank() }
        ?.let { parsePuzzleProgress(it) }
    val g = guessText?.takeIf { it.isNotBlank() }
        ?.let { parseGuessStats(it) }
    if (p == null && g == null) return null
    return p to g
}

/** Applies a training document — or the training fields of a
 *  full backup, which carry the same two keys. Records, the
 *  wrong book and settings are never touched. Returns a
 *  result line, or null when the text is not a portable
 *  document or carries no usable training data. */
fun applyTrainingJson(context: Context, text: String): String? {
    val root = try {
        JSONObject(text)
    } catch (_: Exception) {
        return null
    }
    val app = root.optString("app")
    if (app != "jiang-xiangqi-training" && app != "jiang-xiangqi-backup") {
        return null
    }
    val decoded = decodeTraining(
        root.optString("puzzles").ifBlank { null },
        root.optString("guessStats").ifBlank { null }
    ) ?: return null
    val notes = ArrayList<String>()
    decoded.first?.let {
        PuzzleProgressStore(context).replaceAll(it)
        notes.add("残局进度 " + it.solvedIds.size + " 题")
    }
    decoded.second?.let {
        GuessStatsStore(context).replaceAll(it)
        notes.add("猜一选统计")
    }
    return "训练数据已导入：" + notes.joinToString("，") +
        "。棋谱、错题本和设置都没有动。"
}
