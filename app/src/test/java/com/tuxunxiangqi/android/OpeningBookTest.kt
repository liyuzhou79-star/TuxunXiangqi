package com.tuxunxiangqi.android

import com.tuxunxiangqi.android.xiangqi.Fen
import com.tuxunxiangqi.android.xiangqi.GameState
import com.tuxunxiangqi.android.xiangqi.Notation
import com.tuxunxiangqi.android.xiangqi.OpeningBook
import com.tuxunxiangqi.android.xiangqi.Rules
import org.junit.Assert.assertEquals
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
        assertEquals(30, OpeningBook.knownHistories().size)
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
}
