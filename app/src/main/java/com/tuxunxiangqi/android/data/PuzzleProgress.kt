package com.tuxunxiangqi.android.data

import android.content.Context

/** A snapshot of puzzle-training progress: which puzzles are
 *  solved, which of those were solved with a hint, and the
 *  best (fewest) solving move count per puzzle. Mirrors exactly
 *  what PuzzleScreen keeps in the "puzzles" preferences file
 *  ("solved" string set, "assisted_<id>" booleans, "best_<id>"
 *  ints), so the home card, the puzzle list and the career page
 *  all keep reading the same keys. Exists so the data backup
 *  can carry training progress too (since 1.22.0 — before that,
 *  changing phones silently lost every solved puzzle). */
data class PuzzleProgress(
    val solvedIds: Set<Int> = emptySet(),
    val assistedIds: Set<Int> = emptySet(),
    val bestMoves: Map<Int, Int> = emptyMap(),
    /** Epoch days on which the daily puzzle was solved
     *  (1.28.0); drives the streak shown on the home card. */
    val dailyDays: Set<Long> = emptySet()
) {
    /** Compact portable form for the backup document:
     *  "s=1,5,9;a=5;b=1:23,9:31;d=20600,20601"
     *  (solved / assisted / best / daily-solved days). */
    fun encode(): String {
        val sb = StringBuilder()
        sb.append("s=").append(solvedIds.sorted().joinToString(","))
        if (assistedIds.isNotEmpty()) {
            sb.append(";a=").append(assistedIds.sorted().joinToString(","))
        }
        if (bestMoves.isNotEmpty()) {
            sb.append(";b=").append(
                bestMoves.entries.sortedBy { it.key }
                    .joinToString(",") { "${it.key}:${it.value}" }
            )
        }
        if (dailyDays.isNotEmpty()) {
            sb.append(";d=").append(dailyDays.sorted().joinToString(","))
        }
        return sb.toString()
    }
}

/** Current daily-puzzle streak: consecutive solved days ending
 *  today, or ending yesterday when today is not solved yet (the
 *  streak is still alive until the day ends). Pure, pinned by
 *  unit tests. */
fun dailyStreak(days: Set<Long>, todayEpochDay: Long): Int {
    var streak = 0
    var day = if (todayEpochDay in days) todayEpochDay else todayEpochDay - 1
    while (day in days) {
        streak++
        day--
    }
    return streak
}

/** The last [count] days up to and including today, oldest
 *  first, each paired with whether the daily puzzle was solved
 *  that day. Drives the career page's recent-days dot row
 *  (1.33.0). Pure, pinned by unit tests. */
fun recentDailyStatus(
    days: Set<Long>,
    todayEpochDay: Long,
    count: Int = 7
): List<Pair<Long, Boolean>> =
    (count - 1 downTo 0).map { back ->
        val day = todayEpochDay - back
        day to (day in days)
    }

/** Parses the [PuzzleProgress.encode] form back. Tolerant of
 *  unknown sections and bad entries (they are skipped); null
 *  only when the text is blank or has no recognisable section,
 *  so a corrupt backup line never wipes existing progress. */
fun parsePuzzleProgress(text: String): PuzzleProgress? {
    val t = text.trim()
    if (t.isEmpty()) return null
    var solved: Set<Int>? = null
    val assisted = mutableSetOf<Int>()
    val best = mutableMapOf<Int, Int>()
    val days = mutableSetOf<Long>()
    var sawSection = false
    for (part in t.split(';')) {
        val eq = part.indexOf('=')
        if (eq <= 0) continue
        val key = part.substring(0, eq).trim()
        val value = part.substring(eq + 1).trim()
        when (key) {
            "s" -> {
                sawSection = true
                solved = value.split(',')
                    .mapNotNull { it.trim().toIntOrNull() }.toSet()
            }
            "a" -> {
                sawSection = true
                assisted += value.split(',')
                    .mapNotNull { it.trim().toIntOrNull() }
            }
            "b" -> {
                sawSection = true
                for (entry in value.split(',')) {
                    val kv = entry.split(':')
                    if (kv.size == 2) {
                        val id = kv[0].trim().toIntOrNull()
                        val moves = kv[1].trim().toIntOrNull()
                        if (id != null && moves != null) best[id] = moves
                    }
                }
            }
            "d" -> {
                sawSection = true
                days += value.split(',')
                    .mapNotNull { it.trim().toLongOrNull() }
            }
        }
    }
    if (!sawSection) return null
    return PuzzleProgress(solved ?: emptySet(), assisted, best, days)
}

/** Reads and replaces the puzzle progress in the same
 *  preferences file PuzzleScreen uses; the two access paths
 *  share keys, never formats. */
class PuzzleProgressStore(context: Context) {

    private val prefs =
        context.applicationContext.getSharedPreferences("puzzles", Context.MODE_PRIVATE)

    fun load(): PuzzleProgress {
        val solved = prefs.getStringSet("solved", emptySet()).orEmpty()
            .mapNotNull { it.toIntOrNull() }.toSet()
        val assisted = mutableSetOf<Int>()
        val best = mutableMapOf<Int, Int>()
        for ((key, value) in prefs.all) {
            when {
                key.startsWith("assisted_") ->
                    if (value == true) {
                        key.removePrefix("assisted_").toIntOrNull()?.let { assisted += it }
                    }
                key.startsWith("best_") ->
                    (value as? Int)?.let { v ->
                        key.removePrefix("best_").toIntOrNull()?.let { best[it] = v }
                    }
            }
        }
        val days = prefs.getStringSet("daily_days", emptySet()).orEmpty()
            .mapNotNull { it.toLongOrNull() }.toSet()
        return PuzzleProgress(solved, assisted, best, days)
    }

    /** Records that the daily puzzle was solved on the given
     *  epoch day (idempotent; stored as the "daily_days" string
     *  set in the same preferences file PuzzleScreen writes). */
    fun recordDailySolved(epochDay: Long) {
        val days = prefs.getStringSet("daily_days", emptySet()).orEmpty().toMutableSet()
        if (days.add(epochDay.toString())) {
            prefs.edit().putStringSet("daily_days", days).apply()
        }
    }

    fun replaceAll(progress: PuzzleProgress) {
        val e = prefs.edit()
        // Drop every progress key first so ids solved on this
        // device but absent from the backup do not linger.
        for (key in prefs.all.keys) {
            if (key == "solved" || key.startsWith("assisted_") || key.startsWith("best_")) {
                e.remove(key)
            }
        }
        e.putStringSet("solved", progress.solvedIds.map { it.toString() }.toSet())
        for (id in progress.assistedIds) e.putBoolean("assisted_$id", true)
        for ((id, moves) in progress.bestMoves) e.putInt("best_$id", moves)
        // Daily days ride along only when the backup carries
        // them: a pre-1.28 backup has no "d" section and must not
        // wipe the streak recorded on this device (the same
        // field-missing rule the whole progress block follows).
        if (progress.dailyDays.isNotEmpty()) {
            e.putStringSet("daily_days", progress.dailyDays.map { it.toString() }.toSet())
        }
        e.apply()
    }
}
