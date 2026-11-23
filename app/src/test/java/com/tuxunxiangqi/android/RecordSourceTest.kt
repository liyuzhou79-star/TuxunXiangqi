package com.tuxunxiangqi.android

import com.tuxunxiangqi.android.data.GameRecord
import com.tuxunxiangqi.android.data.fenPositionKey
import com.tuxunxiangqi.android.data.filterRecords
import com.tuxunxiangqi.android.data.sourceLabel
import com.tuxunxiangqi.android.data.sourceNote
import com.tuxunxiangqi.android.xiangqi.Fen
import com.tuxunxiangqi.android.xiangqi.GameState
import com.tuxunxiangqi.android.xiangqi.Side
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** Start-position source labels (1.23.0): judged purely by the
 *  record's initial position — standard start, a position the
 *  opening book reaches, or a hand-set editor position. The
 *  fullmove counter must not matter: BoardScreen stores start
 *  positions with fullmove 1 while a replayed book line counts
 *  real fullmoves. */
class RecordSourceTest {

    private fun rec(fen: String) = GameRecord(
        id = 1L,
        title = "测试局",
        dateMs = 0L,
        modeLabel = "人机对弈 · 普通",
        resultText = "",
        initialFen = fen,
        movesUci = emptyList(),
        evals = emptyMap()
    )

    private fun replayFen(history: List<String>): String {
        val gs = GameState()
        for (uci in history) {
            val ft = Fen.parseUciMove(uci)!!
            assertTrue(gs.tryMove(ft.first, ft.second))
        }
        return Fen.toFen(gs)
    }

    @Test
    fun standardStartFromReplay() {
        assertEquals("标准开局", rec(replayFen(emptyList())).sourceLabel())
        assertEquals("", rec(replayFen(emptyList())).sourceNote())
    }

    @Test
    fun standardStartAsBoardScreenStoresIt() {
        // BoardScreen stores starts via toFen(cells, turn), which
        // always writes fullmove 1 — same label.
        val fen = Fen.toFen(GameState().cells.toList(), Side.RED)
        assertEquals("标准开局", rec(fen).sourceLabel())
    }

    @Test
    fun bookNodeIsBookStartWithNotation() {
        val r = rec(replayFen(listOf("h2e2")))
        assertEquals("定式开局", r.sourceLabel())
        assertTrue(r.sourceNote().contains("炮二平五"))
    }

    @Test
    fun deeperBookNodeIsBookStart() {
        val r = rec(replayFen(listOf("h2e2", "h9g7", "h0g2")))
        assertEquals("定式开局", r.sourceLabel())
        // Generated notation uses the red horse glyph 傌.
        assertTrue(r.sourceNote().contains("傌二进三"))
    }

    @Test
    fun offBookPositionIsEditorStart() {
        // One red pawn missing from the standard start: not the
        // start, and no book line reaches it.
        val fen = "rnbakabnr/9/1c5c1/p1p1p1p1p/9/9/P1P1P1P2/" +
            "1C5C1/9/RNBAKABNR w - - 0 1"
        assertEquals("摆棋开局", rec(fen).sourceLabel())
        assertEquals("", rec(fen).sourceNote())
    }

    @Test
    fun blankFenIsEditorStart() {
        assertEquals("摆棋开局", rec("").sourceLabel())
    }

    @Test
    fun sourceFilterKeepsOnlyThatSource() {
        // The 1.36.0 library source row: each label keeps only
        // its own records.
        val standard = rec(replayFen(emptyList())).copy(id = 1)
        val book = rec(replayFen(listOf("h2e2"))).copy(id = 2)
        val editor = rec("").copy(id = 3)
        val records = listOf(standard, book, editor)
        assertEquals(3, filterRecords(records, "全部", "").size)
        assertEquals(
            3,
            filterRecords(records, "全部", "", source = "全部来源").size
        )
        assertEquals(
            listOf(1L),
            filterRecords(records, "全部", "", source = "标准开局")
                .map { it.id }
        )
        assertEquals(
            listOf(2L),
            filterRecords(records, "全部", "", source = "定式开局")
                .map { it.id }
        )
        assertEquals(
            listOf(3L),
            filterRecords(records, "全部", "", source = "摆棋开局")
                .map { it.id }
        )
    }

    @Test
    fun positionKeyIgnoresCounters() {
        assertEquals(
            fenPositionKey("rnbakabnr/9/1c5c1 w - - 0 1"),
            fenPositionKey("rnbakabnr/9/1c5c1 w - - 7 12")
        )
        assertEquals(
            "rnbakabnr/9/1c5c1 w",
            fenPositionKey("rnbakabnr/9/1c5c1 w - - 0 1")
        )
    }
}
