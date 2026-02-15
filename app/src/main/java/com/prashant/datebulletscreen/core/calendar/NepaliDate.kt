package com.prashant.datebulletscreen.core.calendar

data class NepaliDate(
    val year: Int,
    val month: Int,  // 1-12 (Baisakh=1, Chaitra=12)
    val day: Int
) {
    val monthName: String
        get() = MONTH_NAMES[month - 1]

    val monthNameNepali: String
        get() = MONTH_NAMES_NEPALI[month - 1]

    fun format(): String = "$monthName ${day.toString().padStart(2, '0')}, $year"

    fun formatNepali(): String = "$monthNameNepali ${day.toString().padStart(2, '0')}, $year"

    companion object {
        val MONTH_NAMES = arrayOf(
            "Baisakh", "Jestha", "Ashadh", "Shrawan",
            "Bhadra", "Ashwin", "Kartik", "Mangsir",
            "Poush", "Magh", "Falgun", "Chaitra"
        )

        val MONTH_NAMES_NEPALI = arrayOf(
            "बैशाख", "जेठ", "असार", "श्रावण",
            "भाद्र", "आश्विन", "कार्तिक", "मंसिर",
            "पौष", "माघ", "फाल्गुन", "चैत्र"
        )
    }
}
