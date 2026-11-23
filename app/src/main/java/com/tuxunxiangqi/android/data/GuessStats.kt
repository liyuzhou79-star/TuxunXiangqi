package com.tuxunxiangqi.android.data

import android.content.Context

/** Outcome of one judged guess, in the trainer's own terms. */
enum class GuessOutcome { BEST, TOP4, MISS }

/** Lifetime counters for one guess source bucket. */
data class GuessStat(val asked: Int = 0, val bestHits: Int = 0, val top4: Int = 0) {
    val bestRatePercent: Int
        get() = if (asked == 0) 0 else bestHits * 100 / asked
}

/** Portable form of the whole guess-stats table for the data
 *  backup (1.22.0): "简单=12,5,4;普通=…" — per bucket
 *  asked,bestHits,top4, buckets in canonical order. */
fun encodeGuessStats(stats: Map<String, GuessStat>): String =
    (GuessStatsStore.BUCKETS + GuessStatsStore.TIMED_BUCKET).mapNotNull { b ->
        stats[b]?.let { "$b=${it.asked},${it.bestHits},${it.top4}" }
    }.joinToString(";")

/** Parses [encodeGuessStats] output back. Unknown buckets and
 *  malformed entries are skipped; null when nothing in the
 *  text is recognisable, so a corrupt backup line never wipes
 *  the current table. */
fun parseGuessStats(text: String): Map<String, GuessStat>? {
    val known = GuessStatsStore.BUCKETS + GuessStatsStore.TIMED_BUCKET
    val out = mutableMapOf<String, GuessStat>()
    for (part in text.trim().split(';')) {
        val eq = part.indexOf('=')
        if (eq <= 0) continue
        val bucket = part.substring(0, eq).trim()
        if (bucket !in known) continue
        val nums = part.substring(eq + 1).split(',')
            .map { it.trim().toIntOrNull() }
        if (nums.size == 3 && nums.all { it != null }) {
            out[bucket] = GuessStat(nums[0]!!, nums[1]!!, nums[2]!!)
        }
    }
    return out.ifEmpty { null }
}

/** Persistent guess-the-best-move statistics, bucketed by the
 *  trainer's source labels （简单/普通/困难/我的棋谱/错题本）.
 *  Timed-mode questions are accounted separately under
 *  [TIMED_BUCKET] (since 1.8.0), so the per-source numbers stay
 *  untimed-only and the timed record stands on its own line.
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

    /** The separately-accounted timed-challenge record. */
    fun timedStat(): GuessStat = stat(TIMED_BUCKET)

    /** Every bucket's counters, timed included (backup, 1.22.0). */
    fun allStats(): Map<String, GuessStat> =
        (BUCKETS + TIMED_BUCKET).associateWith { stat(it) }

    /** Replaces every bucket's counters (backup restore). */
    fun replaceAll(stats: Map<String, GuessStat>) {
        val e = prefs.edit()
        for (b in BUCKETS + TIMED_BUCKET) {
            val s = stats[b] ?: GuessStat()
            e.putInt("asked_$b", s.asked)
            e.putInt("best_$b", s.bestHits)
            e.putInt("top4_$b", s.top4)
        }
        e.apply()
    }

    companion object {
        val BUCKETS: List<String> = listOf("简单", "普通", "困难", "我的棋谱", "错题本")

        /** Bucket for questions answered under the 30-second
         *  timed mode; kept out of [BUCKETS] on purpose so the
         *  source totals stay untimed-only. */
        const val TIMED_BUCKET = "限时挑战"
    }
}
