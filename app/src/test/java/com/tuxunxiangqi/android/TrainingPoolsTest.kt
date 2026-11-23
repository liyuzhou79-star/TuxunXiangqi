package com.tuxunxiangqi.android

import com.tuxunxiangqi.android.data.GuessPositions
import com.tuxunxiangqi.android.data.Puzzles
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** The puzzle list's difficulty filter (1.19.0) and the guess
 *  trainer's pool sizes shown on the source chips: the numbers
 *  the UI displays come straight from these lists, so pin them. */
class TrainingPoolsTest {

    @Test
    fun difficultyTiersCoverEveryPuzzleExactlyOnce() {
        val counts = (1..3).map { tier ->
            Puzzles.filterPuzzles(Puzzles.all, tier, false, emptySet()).size
        }
        assertEquals(Puzzles.all.size, counts.sum())
        assertTrue(counts.all { it > 0 })
        // The unfiltered call returns the whole pool untouched.
        assertEquals(
            Puzzles.all.size,
            Puzzles.filterPuzzles(Puzzles.all, null, false, emptySet()).size
        )
    }

    @Test
    fun filterKeepsOnlyTheRequestedTier() {
        for (tier in 1..3) {
            val kept = Puzzles.filterPuzzles(Puzzles.all, tier, false, emptySet())
            assertTrue(kept.all { Puzzles.difficultyOf(it.id) == tier })
        }
    }

    @Test
    fun unsolvedOnlyDropsSolvedAndCombinesWithTier() {
        val solved = Puzzles.all.take(5).map { it.id }.toSet()
        val kept = Puzzles.filterPuzzles(Puzzles.all, null, true, solved)
        assertEquals(Puzzles.all.size - solved.size, kept.size)
        assertTrue(kept.none { it.id in solved })
        val tierSolved = Puzzles.filterPuzzles(Puzzles.all, 2, true, solved)
        assertTrue(tierSolved.all {
            Puzzles.difficultyOf(it.id) == 2 && it.id !in solved
        })
        // Everything solved + unsolved-only = empty (the list
        // page shows its empty-state card in this case).
        val allSolved = Puzzles.all.map { it.id }.toSet()
        assertTrue(Puzzles.filterPuzzles(Puzzles.all, null, true, allSolved).isEmpty())
    }

    @Test
    fun guessPoolsMatchTheCountsShownOnChips() {
        assertEquals(12, GuessPositions.easy.size)
        assertEquals(12, GuessPositions.normal.size)
        assertEquals(12, GuessPositions.hard.size)
    }
}
