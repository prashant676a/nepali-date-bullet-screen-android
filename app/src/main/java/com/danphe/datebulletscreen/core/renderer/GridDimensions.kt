package com.danphe.datebulletscreen.core.renderer

/**
 * Computes responsive layout dimensions based on screen size.
 */
class GridDimensions(
    val screenWidth: Int,
    val screenHeight: Int,
    val topMarginPx: Int = 350
) {
    // Days Grid layout
    val daysColumns = 20
    val dotSpacing: Float = (screenWidth - 2 * horizontalPadding) / daysColumns.toFloat()
    val dotRadius: Float = dotSpacing * 0.28f

    // Months Grid layout (3 columns x 4 rows of month blocks)
    val monthColumns = 3
    val monthRows = 4
    val monthBlockWidth: Float = (screenWidth - 2 * horizontalPadding) / monthColumns.toFloat()
    val monthBlockHeight: Float get() = monthBlockWidth * 1.3f
    val monthDotColumns = 6  // dots per row inside a month block

    // Text area at bottom
    val textAreaHeight = 160f
    val dateTextSize: Float = screenWidth * 0.045f
    val progressTextSize: Float = screenWidth * 0.035f

    /** Available height for the dot grid (between top margin and text area). */
    val gridAreaHeight: Float
        get() = screenHeight - topMarginPx - textAreaHeight

    /** Y offset where grid starts. */
    val gridTop: Float
        get() = topMarginPx.toFloat()

    /** Y offset where text area starts. */
    val textAreaTop: Float
        get() = screenHeight - textAreaHeight

    companion object {
        const val horizontalPadding = 40f
    }
}
