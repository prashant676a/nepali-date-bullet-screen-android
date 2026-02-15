package com.danphe.datebulletscreen

import com.danphe.datebulletscreen.core.calendar.NepaliDate
import com.danphe.datebulletscreen.core.calendar.NepaliDateConverter
import com.danphe.datebulletscreen.core.calendar.NepaliDateUtils
import com.danphe.datebulletscreen.core.calendar.NepaliMonthData
import org.junit.Assert.assertEquals
import org.junit.Test

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
        val adDate = NepaliDateConverter.toGregorian(bs.year, bs.month, bs.day)

        assertEquals(adYear, adDate.year)
        assertEquals(adMonth, adDate.monthValue)
        assertEquals(adDay, adDate.dayOfMonth)
    }

    @Test
    fun `round trip multiple dates`() {
        // Test several dates across the range
        val testDates = listOf(
            Triple(2020, 1, 1),
            Triple(2023, 4, 14),
            Triple(2025, 12, 25),
            Triple(2026, 2, 15),
            Triple(2030, 7, 4),
        )
        for ((y, m, d) in testDates) {
            val bs = NepaliDateConverter.fromGregorian(y, m, d)
            val ad = NepaliDateConverter.toGregorian(bs.year, bs.month, bs.day)
            assertEquals("Round trip failed for $y-$m-$d", y, ad.year)
            assertEquals("Round trip failed for $y-$m-$d", m, ad.monthValue)
            assertEquals("Round trip failed for $y-$m-$d", d, ad.dayOfMonth)
        }
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
        val year = 2082
        val totalDays = NepaliMonthData.daysInYear(year)
        val halfDoy = totalDays / 2

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
        assert(progress in 48..52) { "Expected ~50%, got $progress%" }
    }

    @Test
    fun `toGregorian for epoch returns epoch AD date`() {
        val ad = NepaliDateConverter.toGregorian(1970, 1, 1)
        assertEquals(1913, ad.year)
        assertEquals(4, ad.monthValue)
        assertEquals(13, ad.dayOfMonth)
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
