package com.tuxunxiangqi.android

import com.tuxunxiangqi.android.engine.Candidate
import com.tuxunxiangqi.android.ui.buildCandidateCards
import com.tuxunxiangqi.android.ui.candidateRankLabel
import com.tuxunxiangqi.android.xiangqi.EvalUtil
import com.tuxunxiangqi.android.xiangqi.Fen
import com.tuxunxiangqi.android.xiangqi.GameState
import com.tuxunxiangqi.android.xiangqi.Notation
import com.tuxunxiangqi.android.xiangqi.Side
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** The shared candidate cards (game analysis, review deep
 *  analysis, guess trainer, puzzle hint — unified in 1.14.0)
 *  are built by one pure builder: rank order is normalised,
 *  titles come from the notation engine, scores are stated
 *  from Red's perspective, and unparseable moves are dropped
 *  instead of crashing the card list. */
class CandidateCardsTest {

    private val startCells = GameState().cells.toList()

    private fun cand(
        rank: Int,
        uci: String,
        cp: Int? = null,
        mate: Int? = null,
        pv: List<String> = emptyList()
    ) = Candidate(rank, uci, cp, mate, 9, pv)

    @Test
    fun cardsComeOutInRankOrder() {
        val cards = buildCandidateCards(
            startCells, Side.RED,
            listOf(
                cand(2, "h7e7", cp = 10, pv = listOf("h7e7")),
                cand(1, "b7e7", cp = 25, pv = listOf("b7e7", "b0c2"))
            )
        )
        assertEquals(2, cards.size)
        assertEquals(listOf(1, 2), cards.map { it.rank })
    }

    @Test
    fun titleAndEndpointsMatchTheNotationEngine() {
        val cards = buildCandidateCards(
            startCells, Side.RED,
            listOf(cand(1, "b7e7", cp = 25, pv = listOf("b7e7")))
        )
        val mv = Fen.parseUciMove("b7e7")!!
        assertEquals(mv.first, cards[0].from)
        assertEquals(mv.second, cards[0].to)
        assertEquals(
            Notation.moveText(startCells, mv.first, mv.second),
            cards[0].title
        )
        // The variation line starts with the candidate itself.
        assertTrue(cards[0].pvText.startsWith(cards[0].title))
    }

    @Test
    fun scoreIsStatedFromRedPerspective() {
        val red = buildCandidateCards(
            startCells, Side.RED, listOf(cand(1, "b7e7", cp = 25))
        )
        assertEquals(EvalUtil.describeRed(25, null), red[0].scoreText)
        // Same engine number with Black to move flips sign.
        val black = buildCandidateCards(
            startCells, Side.BLACK, listOf(cand(1, "b2e2", cp = 40))
        )
        assertEquals(EvalUtil.describeRed(-40, null), black[0].scoreText)
        // Mate scores pass through the same conversion.
        val mate = buildCandidateCards(
            startCells, Side.RED, listOf(cand(1, "b7e7", mate = 2))
        )
        assertEquals(EvalUtil.describeRed(null, 2), mate[0].scoreText)
    }

    @Test
    fun unparseableMovesAreDroppedNotShown() {
        val cards = buildCandidateCards(
            startCells, Side.RED,
            listOf(cand(1, "zzzz", cp = 0), cand(2, "b7e7", cp = 5))
        )
        assertEquals(1, cards.size)
        assertEquals("b7e7", cards[0].moveUci)
    }

    @Test
    fun emptyVariationStaysBlank() {
        val cards = buildCandidateCards(
            startCells, Side.RED, listOf(cand(1, "b7e7", cp = 5))
        )
        assertEquals("", cards[0].pvText)
    }

    @Test
    fun rankLabelsUseTheSharedWords() {
        assertEquals("一选", candidateRankLabel(1))
        assertEquals("二选", candidateRankLabel(2))
        assertEquals("三选", candidateRankLabel(3))
        assertEquals("四选", candidateRankLabel(4))
        assertEquals("第 5 选", candidateRankLabel(5))
    }
}
