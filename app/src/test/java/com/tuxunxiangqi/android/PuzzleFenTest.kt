package com.tuxunxiangqi.android

import com.tuxunxiangqi.android.data.GuessPositions
import com.tuxunxiangqi.android.data.Puzzles
import com.tuxunxiangqi.android.xiangqi.Fen
import com.tuxunxiangqi.android.xiangqi.PieceType
import com.tuxunxiangqi.android.xiangqi.Side
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** Every built-in training position must parse, contain both
 *  generals, and not start with the generals facing each other. */
class PuzzleFenTest {

    private fun checkFen(fen: String) {
        val parsed = Fen.fromFen(fen)
        assertNotNull("unparseable: $fen", parsed)
        val cells = parsed!!.first
        var redGeneral = -1
        var blackGeneral = -1
        for (i in cells.indices) {
            val p = cells[i] ?: continue
            if (p.type == PieceType.GENERAL) {
                if (p.side == Side.RED) redGeneral = i else blackGeneral = i
            }
        }
        assertTrue("missing red general: $fen", redGeneral >= 0)
        assertTrue("missing black general: $fen", blackGeneral >= 0)
        val rc = redGeneral % 9
        val bc = blackGeneral % 9
        if (rc == bc) {
            val lo = minOf(redGeneral, blackGeneral)
            val hi = maxOf(redGeneral, blackGeneral)
            var blocked = false
            var i = lo + 9
            while (i < hi) {
                if (cells[i] != null) blocked = true
                i += 9
            }
            assertTrue("generals facing: $fen", blocked)
        }
    }

    @Test
    fun allPuzzleFensAreSane() {
        for (p in Puzzles.all) checkFen(p.fen)
        assertEquals(327, Puzzles.all.size)
    }

    /** No two puzzles may share a position (1.71.0): the full
     *  library audit found #108 duplicating #100's board — the
     *  same position had been collected twice under different
     *  titles. Batch-local checks cannot see cross-batch
     *  duplicates; this one can. */
    @Test
    fun noTwoPuzzlesShareAPosition() {
        val boards = Puzzles.all.map { it.fen.substringBefore(' ') }
        assertEquals(
            "duplicate puzzle positions: " +
                boards.groupingBy { it }.eachCount().filter { it.value > 1 }.keys,
            boards.size, boards.toSet().size
        )
    }

    /** No two puzzles may share a display title (1.90.0): the
     *  title audit found 8 pairs of distinct positions carrying
     *  the same name, which reads as a duplicate in the list.
     *  The later entry of each pair now carries a （二） suffix. */
    @Test
    fun noTwoPuzzlesShareATitle() {
        val titles = Puzzles.all.map { it.title }
        assertEquals(
            "duplicate puzzle titles: " +
                titles.groupingBy { it }.eachCount().filter { it.value > 1 }.keys,
            titles.size, titles.toSet().size
        )
    }

    @Test
    fun everyPuzzleHasCategoryAndSaneDifficulty() {
        assertTrue(
            "puzzles without category: " + Puzzles.idsWithoutCategory(),
            Puzzles.idsWithoutCategory().isEmpty()
        )
        for (p in Puzzles.all) {
            assertTrue(Puzzles.categoryOf(p.id) in Puzzles.categoryOrder)
            assertTrue(Puzzles.difficultyOf(p.id) in 1..3)
        }
    }

    @Test
    fun allGuessFensAreSane() {
        for ((_, fen) in GuessPositions.easy + GuessPositions.normal + GuessPositions.hard) {
            checkFen(fen)
        }
        assertEquals(12, GuessPositions.easy.size)
        assertEquals(12, GuessPositions.normal.size)
        assertEquals(12, GuessPositions.hard.size)
    }
}
