package com.danphe.datebulletscreen.core.calendar

object NepaliDateUtils {

    /** Day of year (1-based). Baisakh 1 = day 1. */
    fun dayOfYear(date: NepaliDate): Int {
        var day = 0
        for (m in 1 until date.month) {
            day += NepaliMonthData.daysInMonth(date.year, m)
        }
        day += date.day
        return day
    }

    /** Total days in the given BS year. */
    fun daysInYear(year: Int): Int = NepaliMonthData.daysInYear(year)

    /** Days remaining in the year (not counting today). */
    fun daysRemaining(date: NepaliDate): Int {
        return daysInYear(date.year) - dayOfYear(date)
    }

    /** Percentage of year elapsed (0-100). */
    fun yearProgress(date: NepaliDate): Int {
        val total = daysInYear(date.year)
        val elapsed = dayOfYear(date)
        return (elapsed * 100) / total
    }

    /** Format progress text: e.g. "83d left / 78%" */
    fun progressText(date: NepaliDate): String {
        val remaining = daysRemaining(date)
        val pct = yearProgress(date)
        return "${remaining}d left / $pct%"
    }

    /** Days in each month for the given year, as a list of (monthIndex 1-12, dayCount). */
    fun monthDaysList(year: Int): List<Pair<Int, Int>> {
        return (1..12).map { m -> m to NepaliMonthData.daysInMonth(year, m) }
    }
}
