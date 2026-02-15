package com.danphe.datebulletscreen.core.renderer

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import com.danphe.datebulletscreen.core.calendar.NepaliDate
import com.danphe.datebulletscreen.core.calendar.NepaliDateUtils
import com.danphe.datebulletscreen.core.calendar.NepaliMonthData

/**
 * Renders two views of year progress as a dot grid on a Canvas:
 * - Days Grid: ~365 dots in a flat grid
 * - Months Grid: Dots organized by 12 Nepali months
 */
class DotGridRenderer {

    enum class ViewMode { DAYS, MONTHS }

    // Pre-allocated paints (no allocation in draw loop)
    private val bgPaint = Paint().apply {
        color = GridColors.BACKGROUND
        style = Paint.Style.FILL
    }

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
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }

    private val progressTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = GridColors.TEXT_SECONDARY
        textAlign = Paint.Align.CENTER
    }

    private val monthLabelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = GridColors.MONTH_LABEL
        textAlign = Paint.Align.LEFT
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
    }

    fun draw(
        canvas: Canvas,
        date: NepaliDate,
        dims: GridDimensions,
        viewMode: ViewMode
    ) {
        // Background
        canvas.drawColor(GridColors.BACKGROUND)

        when (viewMode) {
            ViewMode.DAYS -> drawDaysGrid(canvas, date, dims)
            ViewMode.MONTHS -> drawMonthsGrid(canvas, date, dims)
        }

        drawBottomText(canvas, date, dims)
    }

    private fun drawDaysGrid(canvas: Canvas, date: NepaliDate, dims: GridDimensions) {
        val totalDays = NepaliDateUtils.daysInYear(date.year)
        val todayIndex = NepaliDateUtils.dayOfYear(date) - 1 // 0-based

        val cols = dims.daysColumns
        val rows = (totalDays + cols - 1) / cols
        val spacing = dims.dotSpacing
        val radius = dims.dotRadius

        // Center the grid vertically in the available area
        val totalGridHeight = rows * spacing
        val yOffset = dims.gridTop + (dims.gridAreaHeight - totalGridHeight) / 2f
        val xOffset = GridDimensions.horizontalPadding + spacing / 2f

        for (i in 0 until totalDays) {
            val col = i % cols
            val row = i / cols
            val cx = xOffset + col * spacing
            val cy = yOffset + row * spacing

            val paint = when {
                i < todayIndex -> pastPaint
                i == todayIndex -> todayPaint
                else -> futurePaint
            }

            canvas.drawCircle(cx, cy, if (i == todayIndex) radius * 1.3f else radius, paint)
        }
    }

    private fun drawMonthsGrid(canvas: Canvas, date: NepaliDate, dims: GridDimensions) {
        val todayDoy = NepaliDateUtils.dayOfYear(date) // 1-based day of year

        val blockW = dims.monthBlockWidth
        val blockH = dims.monthBlockHeight
        val labelSize = blockW * 0.11f
        monthLabelPaint.textSize = labelSize

        // Center the 3x4 grid of month blocks vertically
        val totalBlocksHeight = dims.monthRows * blockH
        val yStart = dims.gridTop + (dims.gridAreaHeight - totalBlocksHeight) / 2f
        val xStart = GridDimensions.horizontalPadding

        var doyAccum = 0 // accumulated day-of-year before this month

        for (m in 1..12) {
            val daysInMonth = NepaliMonthData.daysInMonth(date.year, m)
            val blockCol = (m - 1) % dims.monthColumns
            val blockRow = (m - 1) / dims.monthColumns

            val blockX = xStart + blockCol * blockW
            val blockY = yStart + blockRow * blockH

            // Month label
            val shortName = NepaliDate.MONTH_NAMES[m - 1].take(3)
            canvas.drawText(shortName, blockX + 4f, blockY + labelSize, monthLabelPaint)

            // Dots inside the month block
            val dotCols = dims.monthDotColumns
            val innerPadding = 4f
            val availW = blockW - 2 * innerPadding
            val dotSpacing = availW / dotCols.toFloat()
            val dotRadius = dotSpacing * 0.28f
            val dotAreaTop = blockY + labelSize + 6f

            for (d in 0 until daysInMonth) {
                val doy = doyAccum + d + 1 // 1-based
                val col = d % dotCols
                val row = d / dotCols

                val cx = blockX + innerPadding + dotSpacing / 2f + col * dotSpacing
                val cy = dotAreaTop + dotSpacing / 2f + row * dotSpacing

                val paint = when {
                    doy < todayDoy -> pastPaint
                    doy == todayDoy -> todayPaint
                    else -> futurePaint
                }

                canvas.drawCircle(
                    cx, cy,
                    if (doy == todayDoy) dotRadius * 1.3f else dotRadius,
                    paint
                )
            }

            doyAccum += daysInMonth
        }
    }

    private fun drawBottomText(canvas: Canvas, date: NepaliDate, dims: GridDimensions) {
        dateTextPaint.textSize = dims.dateTextSize
        progressTextPaint.textSize = dims.progressTextSize

        val centerX = dims.screenWidth / 2f
        val dateY = dims.textAreaTop + dims.dateTextSize + 16f
        val progressY = dateY + dims.progressTextSize + 12f

        canvas.drawText(date.format(), centerX, dateY, dateTextPaint)
        canvas.drawText(NepaliDateUtils.progressText(date), centerX, progressY, progressTextPaint)
    }
}
