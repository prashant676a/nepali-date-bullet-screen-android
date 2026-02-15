package com.danphe.datebulletscreen.core.renderer

import android.graphics.Color

object GridColors {
    val BACKGROUND = Color.parseColor("#FF121212")
    val DOT_PAST = Color.parseColor("#FFFFFFFF")       // Bright white — days already passed
    val DOT_TODAY = Color.parseColor("#FFFF4444")       // Red — today
    val DOT_FUTURE = Color.parseColor("#33FFFFFF")      // Dim white — days yet to come
    val TEXT_PRIMARY = Color.parseColor("#DDFFFFFF")     // Date text
    val TEXT_SECONDARY = Color.parseColor("#99FFFFFF")   // Progress text
    val MONTH_LABEL = Color.parseColor("#66FFFFFF")      // Month label in months grid
    val MONTH_SEPARATOR = Color.parseColor("#1AFFFFFF")  // Divider between month blocks
}
