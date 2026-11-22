package com.tuxunxiangqi.android

import com.tuxunxiangqi.android.xiangqi.Fen
import com.tuxunxiangqi.android.xiangqi.GameState
import com.tuxunxiangqi.android.xiangqi.OpeningBook
import com.tuxunxiangqi.android.xiangqi.Rules
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
}
