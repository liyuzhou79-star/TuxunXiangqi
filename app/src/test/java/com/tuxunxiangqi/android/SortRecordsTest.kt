package com.tuxunxiangqi.android

import com.tuxunxiangqi.android.data.GameRecord
import com.tuxunxiangqi.android.data.sortRecords
import org.junit.Assert.assertEquals
import org.junit.Test

/** Library sort orders (1.21.0): the five orders the records
 *  screen offers, their tie-breaks, and that sorting never
 *  mutates the caller's list. */
class SortRecordsTest {

    private fun rec(id: Long, title: String, dateMs: Long, moves: Int) = GameRecord(
        id = id,
        title = title,
        dateMs = dateMs,
        modeLabel = "人机对弈 · 普通",
        resultText = "红方胜 · 将死",
        initialFen = "rnbakabnr/9/1c5c1/p1p1p1p1p/9/9/P1P1P1P1P/1C5C1/9/RNBAKABNR w - - 0 1",
        movesUci = List(moves) { "h2e2" },
        evals = emptyMap()
    )

    private val records = listOf(
        rec(1, "乙局", 3_000L, 40),
        rec(2, "甲局", 1_000L, 80),
        rec(3, "丙局", 2_000L, 80),
        rec(4, "丁局", 2_000L, 10)
    )

    private fun ids(list: List<GameRecord>) = list.map { it.id }

    @Test
    fun newestFirstIsDefault() {
        assertEquals(listOf(1L, 3L, 4L, 2L), ids(sortRecords(records, "最新在前")))
        // Unknown keys fall back to the same default order.
        assertEquals(listOf(1L, 3L, 4L, 2L), ids(sortRecords(records, "不存在的排序")))
    }

    @Test
    fun oldestFirst() {
        assertEquals(listOf(2L, 3L, 4L, 1L), ids(sortRecords(records, "最早在前")))
    }

    @Test
    fun byMoveCountBothWays() {
        // 80-move records tie: newest (id 3) before older (id 2).
        assertEquals(listOf(3L, 2L, 1L, 4L), ids(sortRecords(records, "手数多到少")))
        assertEquals(listOf(4L, 1L, 3L, 2L), ids(sortRecords(records, "手数少到多")))
    }

    @Test
    fun byTitle() {
        assertEquals(listOf(4L, 3L, 1L, 2L), ids(sortRecords(records, "标题")))
    }

    @Test
    fun inputNotMutatedAndEmptyStaysEmpty() {
        val before = ids(records)
        sortRecords(records, "标题")
        assertEquals(before, ids(records))
        assertEquals(emptyList<GameRecord>(), sortRecords(emptyList(), "标题"))
    }
}
