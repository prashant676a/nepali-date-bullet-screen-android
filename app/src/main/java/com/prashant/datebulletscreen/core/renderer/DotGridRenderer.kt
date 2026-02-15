package com.prashant.datebulletscreen.core.renderer

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import com.prashant.datebulletscreen.core.calendar.NepaliDate
import com.prashant.datebulletscreen.core.calendar.NepaliDateUtils
import com.prashant.datebulletscreen.core.calendar.NepaliMonthData

/**
 * Renders year progress as a months dot grid on a Canvas.
 * 12 Nepali months in a 3x4 layout, each showing its days as dots.
 */
class DotGridRenderer {

    // Pre-allocated paints (no allocation in draw loop)
    private val pastPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = GridColors.DOT_PAST
        style = Paint.Style.FILL
    }

    private val todayPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = GridColors.DOT_TODAY
        style = Paint.Style.FILL
    }

    private val futurePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = GridColors.DOT_FUTURE
        style = Paint.Style.FILL
    }

    private val dateTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = GridColors.TEXT_PRIMARY
        textAlign = Paint.Align.CENTER
        typeface = Typeface.create("sans-serif-light", Typeface.NORMAL)
    }

    private val progressTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = GridColors.TEXT_SECONDARY
        textAlign = Paint.Align.CENTER
        typeface = Typeface.create("sans-serif", Typeface.NORMAL)
    }

    private val monthLabelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = GridColors.MONTH_LABEL
        textAlign = Paint.Align.LEFT
        typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL)
    }

    private val progressBarBgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = GridColors.PROGRESS_BAR_BG
        style = Paint.Style.FILL
    }

    private val progressBarFgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = GridColors.PROGRESS_BAR_FG
        style = Paint.Style.FILL
    }

    private val barRect = RectF()

    fun draw(canvas: Canvas, date: NepaliDate, dims: GridDimensions) {
        canvas.drawColor(GridColors.BACKGROUND)
        drawMonthsGrid(canvas, date, dims)
        drawBottomText(canvas, date, dims)
    }

    private fun drawMonthsGrid(canvas: Canvas, date: NepaliDate, dims: GridDimensions) {
        val todayDoy = NepaliDateUtils.dayOfYear(date)

        val blockW = dims.monthBlockWidth
        val blockH = dims.monthBlockHeight
        val labelSize = blockW * 0.12f
        monthLabelPaint.textSize = labelSize

        val xStart = GridDimensions.horizontalPadding
        val yStart = dims.gridTop

        var doyAccum = 0

        for (m in 1..12) {
            val daysInMonth = NepaliMonthData.daysInMonth(date.year, m)
            val blockCol = (m - 1) % dims.monthColumns
            val blockRow = (m - 1) / dims.monthColumns

            val blockX = xStart + blockCol * blockW
            val blockY = yStart + blockRow * blockH

            // Highlight current month label
            val isCurrentMonth = (m == date.month)
            monthLabelPaint.color = if (isCurrentMonth) {
                GridColors.MONTH_LABEL_CURRENT
            } else {
                GridColors.MONTH_LABEL
            }

            // Month label — 3-letter abbreviation
            val shortName = NepaliDate.MONTH_NAMES[m - 1].take(3)
            canvas.drawText(shortName, blockX + 6f, blockY + labelSize + 2f, monthLabelPaint)

            // Dots inside the month block
            val dotCols = dims.monthDotColumns
            val innerPaddingH = 6f
            val availW = blockW - 2 * innerPaddingH
            val dotSpacing = availW / dotCols.toFloat()
            val dotRadius = dotSpacing * 0.3f
            val dotAreaTop = blockY + labelSize + 10f

            for (d in 0 until daysInMonth) {
                val doy = doyAccum + d + 1
                val col = d % dotCols
                val row = d / dotCols

                val cx = blockX + innerPaddingH + dotSpacing / 2f + col * dotSpacing
                val cy = dotAreaTop + dotSpacing / 2f + row * dotSpacing

                val paint = when {
                    doy < todayDoy -> pastPaint
                    doy == todayDoy -> todayPaint
                    else -> futurePaint
                }

                val r = if (doy == todayDoy) dotRadius * 1.4f else dotRadius
                canvas.drawCircle(cx, cy, r, paint)
            }

            doyAccum += daysInMonth
        }
    }

    private fun drawBottomText(canvas: Canvas, date: NepaliDate, dims: GridDimensions) {
        dateTextPaint.textSize = dims.dateTextSize
        progressTextPaint.textSize = dims.progressTextSize

        val centerX = dims.screenWidth / 2f
        val progress = NepaliDateUtils.yearProgress(date)

        // Progress bar
        val barY = dims.textAreaTop + 20f
        val barWidth = dims.screenWidth * 0.5f
        val barLeft = centerX - barWidth / 2f
        val barH = dims.progressBarHeight

        // Track
        barRect.set(barLeft, barY, barLeft + barWidth, barY + barH)
        canvas.drawRoundRect(barRect, barH / 2, barH / 2, progressBarBgPaint)

        // Fill
        val fillWidth = barWidth * progress / 100f
        barRect.set(barLeft, barY, barLeft + fillWidth, barY + barH)
        canvas.drawRoundRect(barRect, barH / 2, barH / 2, progressBarFgPaint)

        // Date
        val dateY = barY + 28f + dims.dateTextSize
        canvas.drawText(date.format(), centerX, dateY, dateTextPaint)

        // Progress text
        val progressY = dateY + dims.progressTextSize + 8f
        canvas.drawText(NepaliDateUtils.progressText(date), centerX, progressY, progressTextPaint)
    }
}
