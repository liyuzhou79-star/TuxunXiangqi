package com.tuxunxiangqi.android

import com.tuxunxiangqi.android.xiangqi.Fen
import com.tuxunxiangqi.android.xiangqi.GameState
import com.tuxunxiangqi.android.xiangqi.Pos
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class FenTest {

    @Test
    fun initialPositionFen() {
        val s = GameState()
        assertEquals(
            "rnbakabnr/9/1c5c1/p1p1p1p1p/9/9/P1P1P1P1P/1C5C1/9/RNBAKABNR w - - 0 1",
            Fen.toFen(s)
        )
    }

    @Test
    fun fenAfterFirstMove() {
        val s = GameState()
        // 炮二平五: red cannon (1,7) -> (4,7).
        assertTrue(s.tryMove(Pos(1, 7), Pos(4, 7)))
        assertEquals(
            "rnbakabnr/9/1c5c1/p1p1p1p1p/9/9/P1P1P1P1P/1C2C4/9/RNBAKABNR b - - 0 1",
            Fen.toFen(s)
        )
    }

    @Test
    fun uciRoundTrip() {
        // The CI smoke test's bestmove from startpos: b2e2.
        val (from, to) = Fen.parseUciMove("b2e2")!!
        assertEquals(Pos(1, 7), from)
        assertEquals(Pos(4, 7), to)
        assertEquals("b2e2", Fen.toUci(from, to))
        assertEquals("h2e2", Fen.toUci(Pos(7, 7), Pos(4, 7)))
        assertNull(Fen.parseUciMove("zzzz"))
        assertNull(Fen.parseUciMove("b2"))
    }
}
