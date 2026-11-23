package com.tuxunxiangqi.android

import com.tuxunxiangqi.android.data.GuessStat
import com.tuxunxiangqi.android.data.PuzzleProgress
import com.tuxunxiangqi.android.data.dailyStreak
import com.tuxunxiangqi.android.data.decodeTraining
import com.tuxunxiangqi.android.data.encodeGuessStats
import com.tuxunxiangqi.android.data.parseGuessStats
import com.tuxunxiangqi.android.data.parsePuzzleProgress
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/** Backup codecs for training progress (1.22.0): the portable
 *  string forms that carry puzzle progress and guess statistics
 *  inside the backup document must round-trip exactly, and a
 *  corrupt line must parse to null so restore leaves the
 *  current progress alone. */
class TrainingBackupCodecTest {

    @Test
    fun puzzleProgressRoundTrip() {
        val p = PuzzleProgress(
            solvedIds = setOf(9, 1, 5),
            assistedIds = setOf(5),
            bestMoves = mapOf(1 to 23, 9 to 31)
        )
        assertEquals("s=1,5,9;a=5;b=1:23,9:31", p.encode())
        assertEquals(p, parsePuzzleProgress(p.encode()))
    }

    @Test
    fun puzzleProgressEmptyAndPartial() {
        assertEquals("s=", PuzzleProgress().encode())
        assertEquals(PuzzleProgress(), parsePuzzleProgress("s="))
        // Only the solved section present (older/hand-written).
        assertEquals(
            PuzzleProgress(solvedIds = setOf(2, 7)),
            parsePuzzleProgress("s=2,7")
        )
    }

    @Test
    fun puzzleProgressDailyDaysRoundTrip() {
        val p = PuzzleProgress(
            solvedIds = setOf(3),
            dailyDays = setOf(20601L, 20600L)
        )
        assertEquals("s=3;d=20600,20601", p.encode())
        assertEquals(p, parsePuzzleProgress(p.encode()))
        // A pre-1.28 backup line has no "d" section: days parse
        // empty, everything else intact.
        assertEquals(
            PuzzleProgress(solvedIds = setOf(3)),
            parsePuzzleProgress("s=3")
        )
    }

    @Test
    fun dailyStreakCountsBackFromToday() {
        val today = 20610L
        assertEquals(0, dailyStreak(emptySet(), today))
        assertEquals(1, dailyStreak(setOf(today), today))
        assertEquals(
            3,
            dailyStreak(setOf(today, today - 1, today - 2), today)
        )
        // A gap breaks the run.
        assertEquals(
            2,
            dailyStreak(setOf(today, today - 1, today - 3), today)
        )
        // Today not done yet: the streak through yesterday is
        // still alive.
        assertEquals(
            2,
            dailyStreak(setOf(today - 1, today - 2), today)
        )
        // Yesterday also missing: streak is over.
        assertEquals(0, dailyStreak(setOf(today - 2), today))
    }

    @Test
    fun puzzleProgressCorruptIsNull() {
        assertNull(parsePuzzleProgress(""))
        assertNull(parsePuzzleProgress("hello world"))
        assertNull(parsePuzzleProgress("x=1,2,3"))
    }

    @Test
    fun guessStatsRoundTrip() {
        val stats = mapOf(
            "简单" to GuessStat(asked = 12, bestHits = 5, top4 = 4),
            "普通" to GuessStat(asked = 3, bestHits = 0, top4 = 1),
            "困难" to GuessStat(),
            "我的棋谱" to GuessStat(asked = 1, bestHits = 1, top4 = 0),
            "错题本" to GuessStat(),
            "限时挑战" to GuessStat(asked = 7, bestHits = 2, top4 = 2)
        )
        val encoded = encodeGuessStats(stats)
        assertEquals(
            "简单=12,5,4;普通=3,0,1;困难=0,0,0;我的棋谱=1,1,0;错题本=0,0,0;限时挑战=7,2,2",
            encoded
        )
        assertEquals(stats, parseGuessStats(encoded))
    }

    @Test
    fun guessStatsSkipsUnknownAndCorrupt() {
        assertNull(parseGuessStats(""))
        assertNull(parseGuessStats("不存在的桶=1,2,3"))
        assertNull(parseGuessStats("简单=1,2"))
        // One bad entry does not sink the good ones.
        val parsed = parseGuessStats("简单=1,2,3;普通=oops")
        assertEquals(mapOf("简单" to GuessStat(1, 2, 3)), parsed)
    }

    @Test
    fun openingBucketRoundTrips() {
        // The 开局 source (1.30.0) is a first-class stats
        // bucket: it encodes in canonical order and parses back.
        val stats = mapOf("开局" to GuessStat(asked = 4, bestHits = 1, top4 = 2))
        assertEquals("开局=4,1,2", encodeGuessStats(stats))
        assertEquals(stats, parseGuessStats("开局=4,1,2"))
    }

    @Test
    fun decodeTrainingAppliesEachFieldIndependently() {
        // Both fields good: both come back.
        val both = decodeTraining("s=1,5", "简单=2,1,0")
        assertEquals(setOf(1, 5), both!!.first!!.solvedIds)
        assertEquals(GuessStat(2, 1, 0), both.second!!["简单"])
        // One field corrupt: the other still applies.
        val partial = decodeTraining("garbage", "简单=2,1,0")
        assertNull(partial!!.first)
        assertEquals(GuessStat(2, 1, 0), partial.second!!["简单"])
        // Nothing usable (or nothing there): null, change nothing.
        assertNull(decodeTraining("garbage", "also garbage"))
        assertNull(decodeTraining(null, null))
        assertNull(decodeTraining("", "  "))
    }
}
