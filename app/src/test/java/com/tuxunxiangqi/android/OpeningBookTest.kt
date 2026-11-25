package com.tuxunxiangqi.android

import com.tuxunxiangqi.android.xiangqi.Fen
import com.tuxunxiangqi.android.xiangqi.GameState
import com.tuxunxiangqi.android.xiangqi.Notation
import com.tuxunxiangqi.android.xiangqi.OpeningBook
import com.tuxunxiangqi.android.xiangqi.Rules
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** Every book line must be reachable and every suggestion legal —
 *  this is what validates the hand-written UCI strings. */
class OpeningBookTest {

    @Test
    fun allBookLinesAreLegal() {
        for (history in OpeningBook.knownHistories()) {
            val gs = GameState()
            val moves = if (history.isBlank()) emptyList() else history.split(" ")
            for (uci in moves) {
                val ft = Fen.parseUciMove(uci)
                assertTrue("bad uci $uci", ft != null)
                assertTrue("illegal history move $uci in '$history'", gs.tryMove(ft!!.first, ft.second))
            }
            for ((uci, _) in OpeningBook.lookup(moves)) {
                val ft = Fen.parseUciMove(uci)
                assertTrue("bad suggestion uci $uci", ft != null)
                assertTrue(
                    "illegal suggestion $uci after '$history'",
                    Rules.isLegal(gs.cells, ft!!.first, ft.second)
                )
            }
        }
    }

    /** Tree connectivity (1.70.0): the book must be one connected
     *  tree — every non-root history's parent must be a known
     *  history and the connecting move must be among the parent's
     *  suggestions. The 1.70.0 audit found two orphan nodes in the
     *  顺炮 line (their parents were never written) and one missing
     *  link （炮2平5 after 中炮）, which no legality or name check
     *  could see; this test keeps the tree whole. */
    @Test
    fun bookTreeIsConnected() {
        val known = OpeningBook.knownHistories()
        for (history in known) {
            if (history.isBlank()) continue
            val moves = history.split(" ")
            val parent = moves.dropLast(1)
            assertTrue(
                "orphan node '$history': parent '${parent.joinToString(" ")}' missing",
                parent.joinToString(" ") in known
            )
            assertTrue(
                "bad link '$history': ${moves.last()} not suggested by parent",
                OpeningBook.lookup(parent).any { it.first == moves.last() }
            )
        }
    }

    /** Browsing (1.15.0): allNodes covers exactly the known
     *  histories, root first with an empty history. */
    @Test
    fun allNodesCoverTheWholeBook() {
        val nodes = OpeningBook.allNodes()
        assertEquals(OpeningBook.knownHistories().size, nodes.size)
        assertTrue(nodes.first().historyUci.isEmpty())
        assertEquals(
            OpeningBook.knownHistories(),
            nodes.map { it.historyUci.joinToString(" ") }.toSet()
        )
        for (node in nodes) {
            assertEquals(
                OpeningBook.lookup(node.historyUci), node.suggestions
            )
        }
    }

    /** Search matches 定式 names carried in suggestion names. */
    @Test
    fun searchFindsOpeningNames() {
        val zhongPao = OpeningBook.search("中炮")
        assertTrue(zhongPao.isNotEmpty())
        assertTrue(zhongPao.any { it.historyUci.isEmpty() })
        val shunPao = OpeningBook.search("顺炮")
        assertTrue(shunPao.isNotEmpty())
        assertTrue(
            shunPao.any { node ->
                node.suggestions.any { it.second.contains("顺炮") }
            }
        )
    }

    /** Search also matches the accumulated line name (1.37.0):
     *  a deep node of the 顺炮 line is findable by 顺炮 even
     *  though none of its own suggestions carries the label. */
    @Test
    fun searchFindsLineNames() {
        val byLine = OpeningBook.search("顺炮")
        assertTrue(
            byLine.any { node ->
                OpeningBook.lineName(node.historyUci) == "顺炮" &&
                    node.suggestions.none { it.second.contains("顺炮") }
            }
        )
    }

    /** Search also matches move notation, both in a node's
     *  history and in its suggested moves. */
    @Test
    fun searchFindsNotationText() {
        assertTrue(OpeningBook.search("马二进三").isNotEmpty())
        assertTrue(OpeningBook.search("炮二平五").isNotEmpty())
    }

    /** Turn parity (1.16.0, "start a game from this line"):
     *  replaying any book history on the real state machine must
     *  end on the side turnAfter predicts. */
    @Test
    fun turnAfterMatchesReplayedState() {
        assertEquals(
            com.tuxunxiangqi.android.xiangqi.Side.RED,
            OpeningBook.turnAfter(emptyList())
        )
        for (node in OpeningBook.allNodes()) {
            val gs = GameState()
            for (uci in node.historyUci) {
                val ft = Fen.parseUciMove(uci)!!
                assertTrue(gs.tryMove(ft.first, ft.second))
            }
            assertEquals(
                "turn mismatch after '${node.historyUci}'",
                OpeningBook.turnAfter(node.historyUci), gs.turn
            )
        }
    }

    /** Prefix continuation (1.18.0): a game started from a book
     *  node keeps quoting the book when the lookup history is
     *  prefix + in-game moves along a written line. */
    @Test
    fun lookupContinuesFromNodePrefix() {
        // 中炮对马 node, then 马二进三 lands on a deeper node.
        val deeper = OpeningBook.lookup(
            listOf("h2e2", "h9g7", "h0g2")
        )
        assertTrue(deeper.isNotEmpty())
        // Further along the same written line stays in book.
        val further = OpeningBook.lookup(
            listOf("h2e2", "h9g7", "h0g2", "a9a8", "b0c2")
        )
        assertTrue(further.isNotEmpty())
        // Leaving the written lines leaves the book.
        assertTrue(
            OpeningBook.lookup(listOf("h2e2", "h9g7", "a0a1")).isEmpty()
        )
    }

    /** Blank query is the whole book; nonsense finds nothing. */
    @Test
    fun searchBlankAndNonsense() {
        assertEquals(
            OpeningBook.allNodes().size, OpeningBook.search("").size
        )
        assertEquals(
            OpeningBook.allNodes().size, OpeningBook.search("  ").size
        )
        assertTrue(OpeningBook.search("不存在的定式xyz").isEmpty())
    }

    /** The printed name of every suggestion must be the notation
     *  the app itself generates for that move. Legality alone did
     *  not catch the mirrored entries fixed in 1.3.0 （仙人指路
     *  and 飞相局 first moves written on the wrong wing, cannon
     *  files swapped), because the mirrored moves are legal too —
     *  only the names were lying. Book names use simplified
     *  马/车 where Notation uses the piece glyphs 傌/馬/俥/車,
     *  so those are normalised before comparing. */
    @Test
    fun suggestionNamesMatchNotation() {
        fun normalise(s: String): String = s
            .replace('傌', '马').replace('馬', '马')
            .replace('俥', '车').replace('車', '车')
        assertEquals(488, OpeningBook.knownHistories().size)
        for (history in OpeningBook.knownHistories()) {
            val gs = GameState()
            val moves = if (history.isBlank()) emptyList() else history.split(" ")
            for (uci in moves) {
                val ft = Fen.parseUciMove(uci)!!
                assertTrue(gs.tryMove(ft.first, ft.second))
            }
            val cellsBefore = gs.cells.toList()
            for ((uci, name) in OpeningBook.lookup(moves)) {
                val ft = Fen.parseUciMove(uci)!!
                val generated = normalise(Notation.moveText(cellsBefore, ft.first, ft.second))
                val printed = name.substringBefore('（')
                assertEquals(
                    "name mismatch for $uci after '$history'",
                    printed, generated
                )
            }
        }
    }
    @Test
    fun lineNameFollowsTheNamedMovesOnThePath() {
        // The last bracketed tag along the path names the line.
        assertEquals("中炮", OpeningBook.lineName(listOf("h2e2")))
        assertEquals("中炮", OpeningBook.lineName(listOf("b2e2")))
        assertEquals("顺炮", OpeningBook.lineName(listOf("h2e2", "h7e7")))
        assertEquals("顺炮", OpeningBook.lineName(listOf("b2e2", "b7e7")))
        assertEquals("仙人指路", OpeningBook.lineName(listOf("c3c4")))
        assertEquals("对兵局", OpeningBook.lineName(listOf("c3c4", "c6c5")))
        assertEquals("飞相局", OpeningBook.lineName(listOf("g0e2")))
        // Untagged moves keep the earlier tag; an untagged first
        // move and an empty history have no line name.
        assertEquals(
            "中炮",
            OpeningBook.lineName(listOf("h2e2", "h9g7", "h0g2"))
        )
        assertNull(OpeningBook.lineName(listOf("h0g2")))
        assertNull(OpeningBook.lineName(emptyList()))
        // Every 4–6 ply node the guess pool draws from resolves
        // to one of the book's real line names.
        val known = setOf("中炮", "顺炮", "仙人指路", "飞相局", "对兵局")
        for (node in OpeningBook.allNodes()) {
            if (node.historyUci.size in 4..6) {
                val name = OpeningBook.lineName(node.historyUci)
                assertTrue(
                    "unexpected line name $name for ${node.historyUci}",
                    name == null || name in known
                )
            }
        }
    }
}
