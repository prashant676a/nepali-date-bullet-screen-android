package com.prashant.datebulletscreen.core.renderer

import android.graphics.Color

object GridColors {
    val BACKGROUND = Color.parseColor("#FF0A0A0A")
    val DOT_PAST = Color.parseColor("#FFFFFFFF")        // Bright white — days passed
    val DOT_TODAY = Color.parseColor("#FFFF4444")        // Red — today
    val DOT_FUTURE = Color.parseColor("#2AFFFFFF")       // Dim white — days ahead
    val TEXT_PRIMARY = Color.parseColor("#DDFFFFFF")      // Date text
    val TEXT_SECONDARY = Color.parseColor("#88FFFFFF")    // Progress text
    val MONTH_LABEL = Color.parseColor("#55FFFFFF")       // Month label
    val MONTH_LABEL_CURRENT = Color.parseColor("#CCFFFFFF") // Current month label
    val PROGRESS_BAR_BG = Color.parseColor("#1AFFFFFF")   // Progress bar track
    val PROGRESS_BAR_FG = Color.parseColor("#55FFFFFF")   // Progress bar fill
}
