package com.tuxunxiangqi.android

import com.tuxunxiangqi.android.xiangqi.EvalUtil
import com.tuxunxiangqi.android.xiangqi.GameState
import com.tuxunxiangqi.android.xiangqi.Notation
import com.tuxunxiangqi.android.xiangqi.Pos
import com.tuxunxiangqi.android.xiangqi.Side
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class NotationTest {

    @Test
    fun startposCannonIsPaoErPingWu() {
        val s = GameState()
        // Red cannon from (7,7) to (4,7): the famous 炮二平五.
        assertEquals("炮二平五", Notation.moveText(s.cells, Pos(7, 7), Pos(4, 7)))
        // The mirror cannon is 炮八平五.
        assertEquals("炮八平五", Notation.moveText(s.cells, Pos(1, 7), Pos(4, 7)))
    }

    @Test
    fun blackHorseUsesArabicNumerals() {
        val s = GameState()
        // Black horse from (7,0) to (6,2): 马8进7.
        assertEquals("馬8进7", Notation.moveText(s.cells, Pos(7, 0), Pos(6, 2)))
    }

    @Test
    fun redHorseAdvanceUsesTargetFile() {
        val s = GameState()
        // Red horse from (1,9) to (2,7): 马八进七.
        assertEquals("傌八进七", Notation.moveText(s.cells, Pos(1, 9), Pos(2, 7)))
    }

    @Test
    fun redSoldierAdvanceOne() {
        val s = GameState()
        assertEquals("兵五进一", Notation.moveText(s.cells, Pos(4, 6), Pos(4, 5)))
    }

    @Test
    fun pvFormatsSequence() {
        val s = GameState()
        val pv = Notation.pvText(s.cells, listOf("h2e2", "h9g7"), maxMoves = 2)
        assertEquals(listOf("炮二平五", "馬8进7"), pv)
    }

    @Test
    fun winRateIsMonotonicAndBounded() {
        assertTrue(EvalUtil.winRatePercent(0, null) == 50.0)
        assertTrue(EvalUtil.winRatePercent(300, null) > 70.0)
        assertTrue(EvalUtil.winRatePercent(-300, null) < 30.0)
        assertEquals(100.0, EvalUtil.winRatePercent(null, 3), 0.001)
        assertEquals(0.0, EvalUtil.winRatePercent(null, -3), 0.001)
    }

    @Test
    fun redPerspectiveFlipsForBlack() {
        val (cp, mate) = EvalUtil.toRedPerspective(120, null, Side.BLACK)
        assertEquals(-120, cp)
        assertEquals(null, mate)
    }
}
