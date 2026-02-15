package com.danphe.datebulletscreen.wallpaper

import android.os.Handler
import android.os.Looper
import android.service.wallpaper.WallpaperService
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.SurfaceHolder
import com.danphe.datebulletscreen.core.calendar.NepaliDateConverter
import com.danphe.datebulletscreen.core.renderer.DotGridRenderer
import com.danphe.datebulletscreen.core.renderer.GridDimensions

class DateBulletWallpaperService : WallpaperService() {

    override fun onCreateEngine(): Engine = DateBulletEngine()

    inner class DateBulletEngine : Engine() {

        private val renderer = DotGridRenderer()
        private val handler = Handler(Looper.getMainLooper())
        private var viewMode = DotGridRenderer.ViewMode.DAYS
        private var dims: GridDimensions? = null
        private var visible = false

        private lateinit var gestureDetector: GestureDetector

        private val drawRunnable = Runnable { drawFrame() }

        override fun onCreate(surfaceHolder: SurfaceHolder) {
            super.onCreate(surfaceHolder)
            setTouchEventsEnabled(true)

            gestureDetector = GestureDetector(applicationContext,
                object : GestureDetector.SimpleOnGestureListener() {
                    override fun onDoubleTap(e: MotionEvent): Boolean {
                        viewMode = when (viewMode) {
                            DotGridRenderer.ViewMode.DAYS -> DotGridRenderer.ViewMode.MONTHS
                            DotGridRenderer.ViewMode.MONTHS -> DotGridRenderer.ViewMode.DAYS
                        }
                        drawFrame()
                        return true
                    }
                }
            )
        }

        override fun onSurfaceChanged(
            holder: SurfaceHolder,
            format: Int,
            width: Int,
            height: Int
        ) {
            super.onSurfaceChanged(holder, format, width, height)
            dims = GridDimensions(width, height)
            drawFrame()
        }

        override fun onVisibilityChanged(visible: Boolean) {
            this.visible = visible
            if (visible) {
                drawFrame()
                scheduleNextDraw()
            } else {
                handler.removeCallbacks(drawRunnable)
            }
        }

        override fun onTouchEvent(event: MotionEvent) {
            gestureDetector.onTouchEvent(event)
            super.onTouchEvent(event)
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder) {
            visible = false
            handler.removeCallbacks(drawRunnable)
            super.onSurfaceDestroyed(holder)
        }

        private fun drawFrame() {
            val d = dims ?: return
            val holder = surfaceHolder
            var canvas: android.graphics.Canvas? = null
            try {
                canvas = holder.lockCanvas()
                if (canvas != null) {
                    val today = NepaliDateConverter.today()
                    renderer.draw(canvas, today, d, viewMode)
                }
            } finally {
                if (canvas != null) {
                    try {
                        holder.unlockCanvasAndPost(canvas)
                    } catch (_: IllegalArgumentException) {
                        // Surface already released
                    }
                }
            }
            scheduleNextDraw()
        }

        private fun scheduleNextDraw() {
            handler.removeCallbacks(drawRunnable)
            if (visible) {
                // Redraw once per minute
                handler.postDelayed(drawRunnable, 60_000L)
            }
        }
    }
}
