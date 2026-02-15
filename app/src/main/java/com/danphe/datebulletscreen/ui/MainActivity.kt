package com.danphe.datebulletscreen.ui

import android.app.WallpaperManager
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.danphe.datebulletscreen.ui.theme.DateBulletScreenTheme
import com.danphe.datebulletscreen.wallpaper.DateBulletWallpaperService

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DateBulletScreenTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var showPreview by remember { mutableStateOf(false) }

                    if (showPreview) {
                        PreviewScreen(onBack = { showPreview = false })
                    } else {
                        SettingsScreen(
                            onSetWallpaper = { launchWallpaperPicker() },
                            onPreview = { showPreview = true }
                        )
                    }
                }
            }
        }
    }

    private fun launchWallpaperPicker() {
        val intent = Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER).apply {
            putExtra(
                WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                ComponentName(this@MainActivity, DateBulletWallpaperService::class.java)
            )
        }
        startActivity(intent)
    }
}
