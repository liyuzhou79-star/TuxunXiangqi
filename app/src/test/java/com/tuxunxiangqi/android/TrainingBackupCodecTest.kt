package com.tuxunxiangqi.android

import com.tuxunxiangqi.android.data.GuessStat
import com.tuxunxiangqi.android.data.PuzzleProgress
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
}
