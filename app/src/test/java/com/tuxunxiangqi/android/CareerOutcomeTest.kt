package com.tuxunxiangqi.android

import com.tuxunxiangqi.android.data.GameRecord
import com.tuxunxiangqi.android.ui.recordOutcome
import org.junit.Assert.assertEquals
import org.junit.Test

/** Career-page outcome classification (extracted for testing
 *  in 1.79.0). The labels are the board screen's real formats:
 *  mode "人机对弈 · <档位> · 执红/执黑 [· 让子]", result
 *  "<红方胜/黑方胜/和棋> · <原因>" for finished games. */
class CareerOutcomeTest {

    private fun record(mode: String, result: String) = GameRecord(
        id = 1L,
        title = "t",
        dateMs = 0L,
        modeLabel = mode,
        resultText = result,
        initialFen = "",
        movesUci = emptyList(),
        evals = emptyMap()
    )

    @Test
    fun winsAndLossesFollowTheHumanSide() {
        val red = "人机对弈 · 高手 · 执红"
        val black = "人机对弈 · 普通 · 执黑"
        assertEquals("胜", recordOutcome(record(red, "红方胜 · 对方无棋可走")))
        assertEquals("负", recordOutcome(record(red, "黑方胜 · 对方认输")))
        assertEquals("负", recordOutcome(record(black, "红方胜 · 绝杀")))
        assertEquals("胜", recordOutcome(record(black, "黑方胜 · 对方超时")))
    }

    @Test
    fun drawsAndUnfinishedGames() {
        val red = "人机对弈 · 休闲 · 执红"
        assertEquals("和", recordOutcome(record(red, "和棋 · 双方同意和棋")))
        assertEquals("未完", recordOutcome(record(red, "对局进行中")))
        assertEquals("未完", recordOutcome(record(red, "对局结束")))
        assertEquals("未完", recordOutcome(record(red, "")))
    }

    @Test
    fun handicapSuffixDoesNotDisturbTheSide() {
        // The handicap text is appended after 执红/执黑; the
        // side must still be read correctly.
        val mode = "人机对弈 · 高手 · 执黑 · 让双马"
        assertEquals("胜", recordOutcome(record(mode, "黑方胜 · 绝杀")))
        assertEquals("负", recordOutcome(record(mode, "红方胜 · 绝杀")))
    }
}
