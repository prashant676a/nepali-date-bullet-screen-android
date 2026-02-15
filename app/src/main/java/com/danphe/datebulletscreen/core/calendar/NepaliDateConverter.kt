package com.danphe.datebulletscreen.core.calendar

import java.util.Calendar
import java.util.GregorianCalendar

object NepaliDateConverter {

    /**
     * Convert a Gregorian (AD) date to Bikram Sambat (BS).
     *
     * Algorithm:
     * 1. Calculate the number of days between the input AD date and the epoch
     *    (AD 1913/04/13 = BS 1970/01/01).
     * 2. Walk through BS months consuming days until we land on the target date.
     */
    fun fromGregorian(adYear: Int, adMonth: Int, adDay: Int): NepaliDate {
        val epoch = GregorianCalendar(
            NepaliMonthData.EPOCH_AD_YEAR,
            NepaliMonthData.EPOCH_AD_MONTH - 1, // Calendar months are 0-based
            NepaliMonthData.EPOCH_AD_DAY
        )
        val target = GregorianCalendar(adYear, adMonth - 1, adDay)

        var totalDays = daysBetween(epoch, target)
        require(totalDays >= 0) { "AD date $adYear/$adMonth/$adDay is before the epoch" }

        var bsYear = NepaliMonthData.MIN_YEAR
        var bsMonth = 1
        var bsDay = 1

        // Walk through years
        while (bsYear <= NepaliMonthData.MAX_YEAR) {
            val diy = NepaliMonthData.daysInYear(bsYear)
            if (totalDays < diy) break
            totalDays -= diy
            bsYear++
        }

        // Walk through months
        while (bsMonth <= 12) {
            val dim = NepaliMonthData.daysInMonth(bsYear, bsMonth)
            if (totalDays < dim) break
            totalDays -= dim
            bsMonth++
        }

        bsDay += totalDays
        return NepaliDate(bsYear, bsMonth, bsDay)
    }

    /** Convert today's date to BS. */
    fun today(): NepaliDate {
        val cal = Calendar.getInstance()
        return fromGregorian(
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH) + 1,
            cal.get(Calendar.DAY_OF_MONTH)
        )
    }

    /** Convert BS date to Gregorian (AD). Returns a [GregorianCalendar]. */
    fun toGregorian(bsYear: Int, bsMonth: Int, bsDay: Int): GregorianCalendar {
        var totalDays = 0

        // Add days for complete years from epoch
        for (y in NepaliMonthData.MIN_YEAR until bsYear) {
            totalDays += NepaliMonthData.daysInYear(y)
        }

        // Add days for complete months in the target year
        for (m in 1 until bsMonth) {
            totalDays += NepaliMonthData.daysInMonth(bsYear, m)
        }

        // Add remaining days (subtract 1 because day 1 is the epoch itself)
        totalDays += bsDay - 1

        val result = GregorianCalendar(
            NepaliMonthData.EPOCH_AD_YEAR,
            NepaliMonthData.EPOCH_AD_MONTH - 1,
            NepaliMonthData.EPOCH_AD_DAY
        )
        result.add(Calendar.DAY_OF_YEAR, totalDays)
        return result
    }

    private fun daysBetween(from: Calendar, to: Calendar): Int {
        val diffMs = to.timeInMillis - from.timeInMillis
        return (diffMs / (24 * 60 * 60 * 1000)).toInt()
    }
}
