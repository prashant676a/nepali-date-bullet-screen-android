package com.danphe.datebulletscreen.ui

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.danphe.datebulletscreen.core.calendar.NepaliDateConverter
import com.danphe.datebulletscreen.core.renderer.DotGridRenderer
import com.danphe.datebulletscreen.core.renderer.GridDimensions

@Composable
fun PreviewScreen(onBack: () -> Unit) {
    val today = remember { NepaliDateConverter.today() }
    val renderer = remember { DotGridRenderer() }
    var viewMode by remember { mutableStateOf(DotGridRenderer.ViewMode.DAYS) }
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .onSizeChanged { size ->
                if (size.width > 0 && size.height > 0) {
                    val bmp = Bitmap.createBitmap(size.width, size.height, Bitmap.Config.ARGB_8888)
                    val canvas = android.graphics.Canvas(bmp)
                    val dims = GridDimensions(size.width, size.height)
                    renderer.draw(canvas, today, dims, viewMode)
                    bitmap = bmp
                }
            }
            .clickable {
                viewMode = when (viewMode) {
                    DotGridRenderer.ViewMode.DAYS -> DotGridRenderer.ViewMode.MONTHS
                    DotGridRenderer.ViewMode.MONTHS -> DotGridRenderer.ViewMode.DAYS
                }
                // Force re-render by invalidating bitmap
                bitmap?.let { oldBmp ->
                    val bmp = Bitmap.createBitmap(oldBmp.width, oldBmp.height, Bitmap.Config.ARGB_8888)
                    val canvas = android.graphics.Canvas(bmp)
                    val dims = GridDimensions(oldBmp.width, oldBmp.height)
                    renderer.draw(canvas, today, dims, viewMode)
                    bitmap = bmp
                }
            }
    ) {
        bitmap?.let {
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = "Dot grid preview",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.FillBounds
            )
        }

        // Back button
        Text(
            text = "< Back",
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(16.dp)
                .clickable { onBack() }
        )

        // Hint
        Text(
            text = "Tap to switch view",
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
        )
    }
}
