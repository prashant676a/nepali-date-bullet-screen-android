package com.danphe.datebulletscreen

import com.danphe.datebulletscreen.core.calendar.NepaliDate
import com.danphe.datebulletscreen.core.calendar.NepaliDateConverter
import com.danphe.datebulletscreen.core.calendar.NepaliDateUtils
import com.danphe.datebulletscreen.core.calendar.NepaliMonthData
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Calendar

class NepaliDateConverterTest {

    @Test
    fun `epoch date converts correctly`() {
        // BS 1970/01/01 = AD 1913/04/13
        val bs = NepaliDateConverter.fromGregorian(1913, 4, 13)
        assertEquals(1970, bs.year)
        assertEquals(1, bs.month)
        assertEquals(1, bs.day)
    }

    @Test
    fun `known date BS 2082-10-09 equals AD 2026-01-23`() {
        val bs = NepaliDateConverter.fromGregorian(2026, 1, 23)
        assertEquals(2082, bs.year)
        assertEquals(10, bs.month)
        assertEquals(9, bs.day)
    }

    @Test
    fun `known date BS 2080-01-01 equals AD 2023-04-14`() {
        val bs = NepaliDateConverter.fromGregorian(2023, 4, 14)
        assertEquals(2080, bs.year)
        assertEquals(1, bs.month)
        assertEquals(1, bs.day)
    }

    @Test
    fun `round trip AD to BS to AD`() {
        val adYear = 2025
        val adMonth = 6
        val adDay = 15

        val bs = NepaliDateConverter.fromGregorian(adYear, adMonth, adDay)
        val adCal = NepaliDateConverter.toGregorian(bs.year, bs.month, bs.day)

        assertEquals(adYear, adCal.get(Calendar.YEAR))
        assertEquals(adMonth - 1, adCal.get(Calendar.MONTH)) // Calendar month is 0-based
        assertEquals(adDay, adCal.get(Calendar.DAY_OF_MONTH))
    }

    @Test
    fun `daysInYear returns correct total for BS 2080`() {
        val total = NepaliMonthData.daysInYear(2080)
        // BS 2080: 31+32+31+32+31+30+30+30+29+29+30+30 = 365
        assertEquals(365, total)
    }

    @Test
    fun `daysInYear returns correct total for BS 2082`() {
        val total = NepaliMonthData.daysInYear(2082)
        // BS 2082: 30+32+31+32+31+30+30+30+29+30+30+30 = 365
        assertEquals(365, total)
    }

    @Test
    fun `dayOfYear for Baisakh 1 is 1`() {
        val date = NepaliDate(2082, 1, 1)
        assertEquals(1, NepaliDateUtils.dayOfYear(date))
    }

    @Test
    fun `dayOfYear for Chaitra last day equals total days in year`() {
        val year = 2082
        val lastMonth = 12
        val lastDay = NepaliMonthData.daysInMonth(year, lastMonth)
        val date = NepaliDate(year, lastMonth, lastDay)
        assertEquals(NepaliMonthData.daysInYear(year), NepaliDateUtils.dayOfYear(date))
    }

    @Test
    fun `daysRemaining plus dayOfYear equals daysInYear`() {
        val date = NepaliDate(2082, 6, 15)
        val doy = NepaliDateUtils.dayOfYear(date)
        val remaining = NepaliDateUtils.daysRemaining(date)
        assertEquals(NepaliMonthData.daysInYear(2082), doy + remaining)
    }

    @Test
    fun `month name is correct`() {
        val date = NepaliDate(2082, 10, 9)
        assertEquals("Magh", date.monthName)
    }

    @Test
    fun `format produces expected string`() {
        val date = NepaliDate(2082, 10, 9)
        assertEquals("Magh 09, 2082", date.format())
    }

    @Test
    fun `yearProgress at midpoint is approximately 50 percent`() {
        // Pick a date roughly halfway through the year
        val year = 2082
        val totalDays = NepaliMonthData.daysInYear(year)
        val halfDoy = totalDays / 2

        // Walk through months to find the date at halfDoy
        var remaining = halfDoy
        var month = 1
        while (month <= 12) {
            val dim = NepaliMonthData.daysInMonth(year, month)
            if (remaining <= dim) break
            remaining -= dim
            month++
        }

        val date = NepaliDate(year, month, remaining)
        val progress = NepaliDateUtils.yearProgress(date)
        // Should be around 49-51%
        assert(progress in 48..52) { "Expected ~50%, got $progress%" }
    }

    @Test
    fun `toGregorian for epoch returns epoch AD date`() {
        val cal = NepaliDateConverter.toGregorian(1970, 1, 1)
        assertEquals(1913, cal.get(Calendar.YEAR))
        assertEquals(3, cal.get(Calendar.MONTH)) // April = 3 (0-based)
        assertEquals(13, cal.get(Calendar.DAY_OF_MONTH))
    }

    @Test
    fun `all years have between 364 and 367 days`() {
        for (year in NepaliMonthData.MIN_YEAR..NepaliMonthData.MAX_YEAR) {
            val days = NepaliMonthData.daysInYear(year)
            assert(days in 364..367) {
                "BS year $year has $days days, expected 364-367"
            }
        }
    }
}
