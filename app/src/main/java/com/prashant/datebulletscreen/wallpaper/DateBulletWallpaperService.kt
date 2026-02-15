package com.prashant.datebulletscreen.wallpaper

import android.os.Handler
import android.os.Looper
import android.service.wallpaper.WallpaperService
import android.view.SurfaceHolder
import com.prashant.datebulletscreen.core.calendar.NepaliDateConverter
import com.prashant.datebulletscreen.core.renderer.DotGridRenderer
import com.prashant.datebulletscreen.core.renderer.GridDimensions

class DateBulletWallpaperService : WallpaperService() {

    override fun onCreateEngine(): Engine = DateBulletEngine()

    inner class DateBulletEngine : Engine() {

        private val renderer = DotGridRenderer()
        private val handler = Handler(Looper.getMainLooper())
        private var dims: GridDimensions? = null
        private var visible = false

        private val drawRunnable = Runnable { drawFrame() }

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
            } else {
                handler.removeCallbacks(drawRunnable)
            }
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
                    renderer.draw(canvas, today, d)
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
                handler.postDelayed(drawRunnable, 60_000L)
            }
        }
    }
}
