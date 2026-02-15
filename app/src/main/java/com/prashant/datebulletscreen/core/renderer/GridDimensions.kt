package com.prashant.datebulletscreen.core.renderer

/**
 * Computes responsive layout dimensions based on screen size.
 * Optimized for the months grid view.
 */
class GridDimensions(
    val screenWidth: Int,
    val screenHeight: Int,
    val topMarginPx: Int = 350
) {
    // Months Grid layout (3 columns x 4 rows of month blocks)
    val monthColumns = 3
    val monthRows = 4
    private val gridWidth: Float = screenWidth - 2 * horizontalPadding
    val monthBlockWidth: Float = gridWidth / monthColumns
    val monthBlockHeight: Float get() {
        val available = gridAreaHeight
        return available / monthRows
    }
    val monthDotColumns = 6  // dots per row inside a month block

    // Text area at bottom
    val textAreaHeight = 180f
    val dateTextSize: Float = screenWidth * 0.05f
    val progressTextSize: Float = screenWidth * 0.033f
    val progressBarHeight = 3f

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
        const val horizontalPadding = 32f
    }
}
