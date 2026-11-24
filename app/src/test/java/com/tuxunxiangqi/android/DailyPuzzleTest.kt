package com.tuxunxiangqi.android

import com.tuxunxiangqi.android.data.Puzzles
import com.tuxunxiangqi.android.data.recentDailyStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** Daily-puzzle catch-up (1.32.0): the puzzle named for a
 *  calendar date must be the one the app showed that day, and
 *  the recent-days strip walks back from today in order. */
class DailyPuzzleTest {

    @Test
    fun dailyForEpochDayMatchesThatDatesPuzzle() {
        // A spread of dates across years, leap and common.
        val dates = listOf(
            java.time.LocalDate.of(2025, 1, 1),
            java.time.LocalDate.of(2025, 12, 31),
            java.time.LocalDate.of(2026, 2, 28),
            java.time.LocalDate.of(2026, 3, 1),
            java.time.LocalDate.of(2028, 2, 29)
        )
        for (date in dates) {
            assertEquals(
                Puzzles.daily(date.dayOfYear).id,
                Puzzles.dailyForEpochDay(date.toEpochDay()).id
            )
        }
    }

    @Test
    fun recentDailyDaysWalkBackFromToday() {
        val today = java.time.LocalDate.of(2026, 3, 10).toEpochDay()
        val days = Puzzles.recentDailyDays(today)
        assertEquals(7, days.size)
        assertEquals(today, days.first())
        assertEquals(today - 6, days.last())
        assertTrue(days.zipWithNext().all { (a, b) -> a - b == 1L })
    }

    @Test
    fun aWeekOfDailiesAreAllDifferentPuzzles() {
        // Mid-March 2026: day-of-year 69..75, no wrap of the
        // 48-puzzle rotation inside the week.
        val today = java.time.LocalDate.of(2026, 3, 16).toEpochDay()
        val ids = Puzzles.recentDailyDays(today).map {
            Puzzles.dailyForEpochDay(it).id
        }
        assertEquals(7, ids.toSet().size)
    }
    @Test
    fun aWeekAcrossNewYearAreAllDifferentPuzzles() {
        // The 1.77.0 daily-logic audit's one uncovered edge:
        // the catch-up strip walks by epoch day, but the puzzle
        // is picked by day-of-year, which wraps at New Year
        // (365/366 -> 1). The wrapped indices must still land
        // on seven different puzzles — checked for a common
        // year-end and a leap year-end.
        for (jan3 in listOf(
            java.time.LocalDate.of(2027, 1, 3),
            java.time.LocalDate.of(2029, 1, 3)
        )) {
            val ids = Puzzles.recentDailyDays(jan3.toEpochDay()).map {
                Puzzles.dailyForEpochDay(it).id
            }
            assertEquals(7, ids.toSet().size)
        }
    }

    @Test
    fun recentDailyStatusWalksTheLastSevenDays() {
        // Career-page dot row data (1.33.0): oldest first,
        // today last, flags straight from the solved-day set.
        val today = 21000L
        val days = setOf(today, today - 1, today - 3, today - 6, today - 9)
        val rows = recentDailyStatus(days, today)
        assertEquals(7, rows.size)
        assertEquals(
            listOf(today - 6, today - 5, today - 4, today - 3, today - 2, today - 1, today),
            rows.map { it.first }
        )
        assertEquals(
            listOf(true, false, false, true, false, true, true),
            rows.map { it.second }
        )
        // Nothing solved: a clean row of misses, still 7 long.
        assertEquals(
            List(7) { false },
            recentDailyStatus(emptySet(), today).map { it.second }
        )
    }
}
