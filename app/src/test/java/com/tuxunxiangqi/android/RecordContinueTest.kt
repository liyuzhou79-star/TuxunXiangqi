package com.tuxunxiangqi.android

import com.tuxunxiangqi.android.data.GameRecord
import com.tuxunxiangqi.android.data.canContinue
import com.tuxunxiangqi.android.data.toResumeGame
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** Continuing an unfinished record (1.24.0): the resume
 *  snapshot is rebuilt from what BoardScreen wrote into the
 *  record — mode, level, side and handicap all live in the
 *  mode label; strength and clock were never stored and come
 *  from the caller's current settings. */
class RecordContinueTest {

    private fun rec(
        modeLabel: String,
        resultText: String,
        id: Long = 42L
    ) = GameRecord(
        id = id,
        title = "11-23 15:00 $modeLabel",
        dateMs = 0L,
        modeLabel = modeLabel,
        resultText = resultText,
        initialFen = "rnbakabnr/9/1c5c1/p1p1p1p1p/9/9/P1P1P1P1P/1C5C1/9/RNBAKABNR w - - 0 1",
        movesUci = listOf("h2e2", "h9g7"),
        evals = mapOf(2 to 35)
    )

    @Test
    fun aiGameParsesBackFully() {
        val r = rec("人机对弈 · 高手 · 执黑 · 让一马", "对局进行中")
        assertTrue(r.canContinue())
        val g = r.toResumeGame(strength = 7, clockMinutes = 15)!!
        assertEquals("AI", g.modeName)
        assertEquals("高手", g.levelLabel)
        assertEquals(false, g.humanSideRed)
        assertEquals("让一马", g.handicapLabel)
        assertEquals(7, g.strength)
        assertEquals(15, g.clockMinutes)
        assertEquals(15 * 60_000L, g.redMs)
        assertEquals(15 * 60_000L, g.blackMs)
        assertEquals(r.initialFen, g.initialFen)
        assertEquals(r.movesUci, g.movesUci)
        assertEquals(r.evals, g.evals)
        assertEquals(42L, g.recordId)
    }

    @Test
    fun aiGameRedNoHandicap() {
        val g = rec("人机对弈 · 休闲 · 执红", "对局进行中").toResumeGame(5, 0)!!
        assertEquals("休闲", g.levelLabel)
        assertEquals(true, g.humanSideRed)
        assertEquals("不让子", g.handicapLabel)
        assertEquals(0L, g.redMs)
    }

    @Test
    fun pvpGameContinuesAsPvp() {
        val r = rec("人人对弈", "对局进行中")
        assertTrue(r.canContinue())
        val g = r.toResumeGame(5, 10)!!
        assertEquals("PVP", g.modeName)
        assertEquals(true, g.humanSideRed)
    }

    @Test
    fun finishedGamesCannotContinue() {
        assertFalse(rec("人机对弈 · 普通 · 执红", "红方胜 · 将死").canContinue())
        assertFalse(rec("人机对弈 · 普通 · 执红", "和棋 · 双方同意和棋").canContinue())
        assertNull(rec("人机对弈 · 普通 · 执红", "黑方胜 · 困毙").toResumeGame(5, 0))
    }

    @Test
    fun spectacleCannotContinue() {
        val r = rec("AI 表演赛", "对局进行中")
        assertFalse(r.canContinue())
        assertNull(r.toResumeGame(5, 0))
    }
}
