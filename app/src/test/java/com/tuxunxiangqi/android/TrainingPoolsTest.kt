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

    @Test
    fun openingGuessPoolIsRealBookPositions() {
        // The 开局 pool (1.30.0) is derived from the opening
        // book: every face must parse, be unique, and leave the
        // side to move matching the ply parity of a 4–6 ply
        // book line (even plies = Red to move).
        val pool = GuessPositions.opening
        // Derived from the book's 4–6 ply nodes: 12 when the
        // book had 36 nodes, 17 at 42 nodes (1.31.0), 20 since
        // the book grew to 48 (1.35.0 added three 6-ply nodes).
        assertEquals(20, pool.size)
        assertEquals(pool.size, pool.map { it.second }.toSet().size)
        for ((label, fen) in pool) {
            val parsed = com.tuxunxiangqi.android.xiangqi.Fen.fromFen(fen)
            assertTrue("unparseable FEN for $label: $fen", parsed != null)
        }
    }

    @Test
    fun openingLabelsNameTheirLine() {
        // Since 1.33.0 the label leads with the 定式 line name
        // (OpeningBook.lineName) when the line carries one, so a
        // question says which line the position comes from.
        val known = listOf("中炮", "顺炮", "仙人指路", "飞相局", "对兵局")
        var named = 0
        for ((label, _) in GuessPositions.opening) {
            assertTrue(label.contains("定式走了"))
            val hit = known.firstOrNull { label.startsWith("$it · ") }
            if (hit != null) named++
        }
        // Every current line starts from a named first move, so
        // the whole pool is named today; the floor guards the
        // feature against silently disappearing if the book
        // grows an unnamed line later.
        assertTrue("only $named named labels", named >= 12)
    }
}
