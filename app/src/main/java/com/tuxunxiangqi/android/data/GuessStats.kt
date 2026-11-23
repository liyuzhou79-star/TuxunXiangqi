package com.tuxunxiangqi.android.data

import android.content.Context

/** Outcome of one judged guess, in the trainer's own terms. */
enum class GuessOutcome { BEST, TOP4, MISS }

/** Lifetime counters for one guess source bucket. */
data class GuessStat(val asked: Int = 0, val bestHits: Int = 0, val top4: Int = 0) {
    val bestRatePercent: Int
        get() = if (asked == 0) 0 else bestHits * 100 / asked
}

/** Persistent guess-the-best-move statistics, bucketed by the
 *  trainer's source labels （简单/普通/困难/我的棋谱/错题本）.
 *  Stored apart from the settings file, so resetting settings
 *  does not erase training history. */
class GuessStatsStore(context: Context) {

    private val prefs =
        context.applicationContext.getSharedPreferences("guess_stats", Context.MODE_PRIVATE)

    fun record(bucket: String, outcome: GuessOutcome) {
        val e = prefs.edit()
        e.putInt("asked_$bucket", prefs.getInt("asked_$bucket", 0) + 1)
        when (outcome) {
            GuessOutcome.BEST ->
                e.putInt("best_$bucket", prefs.getInt("best_$bucket", 0) + 1)
            GuessOutcome.TOP4 ->
                e.putInt("top4_$bucket", prefs.getInt("top4_$bucket", 0) + 1)
            GuessOutcome.MISS -> {}
        }
        e.apply()
    }

    fun stat(bucket: String): GuessStat = GuessStat(
        asked = prefs.getInt("asked_$bucket", 0),
        bestHits = prefs.getInt("best_$bucket", 0),
        top4 = prefs.getInt("top4_$bucket", 0)
    )

    fun total(): GuessStat {
        var asked = 0
        var best = 0
        var top4 = 0
        for (b in BUCKETS) {
            val s = stat(b)
            asked += s.asked
            best += s.bestHits
            top4 += s.top4
        }
        return GuessStat(asked, best, top4)
    }

    companion object {
        val BUCKETS: List<String> = listOf("简单", "普通", "困难", "我的棋谱", "错题本")
    }
}
