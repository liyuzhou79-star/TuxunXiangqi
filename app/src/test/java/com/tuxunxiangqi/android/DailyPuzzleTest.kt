package com.tuxunxiangqi.android

import com.tuxunxiangqi.android.data.Puzzles
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
}
