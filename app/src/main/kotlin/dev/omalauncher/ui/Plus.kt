package dev.omalauncher.ui

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * Omalauncher+'s own settings, kept in one place so they never collide with
 * upstream's: icon packs and sizes live here, not in the main settings.
 */
object PlusPrefs {
    /** Corner radius in px, 0 to 60; null follows style.toml's corners.scale. */
    var cornerPx: Float? by mutableStateOf(null)
        private set

    /** A light accent halo on every frame in the launcher; off by default. */
    var glow by mutableStateOf(false)
        private set

    /** Quiets the Linux session notification: gone while idle, still shows installs and errors. */
    var hideLinuxNotification by mutableStateOf(false)
        private set

    /** Reads the saved Plus settings; called once when the launcher starts. */
    fun load(context: Context) {
        val prefs = context.getSharedPreferences("launcher", Context.MODE_PRIVATE)
        hideLinuxNotification = prefs.getBoolean(HIDE_LINUX, false)
        glow = prefs.getBoolean(GLOW, false)
        // An earlier Plus saved a scale (0 theme-edge .. 2 full); a scale step is 16px of radius.
        val saved = prefs.getFloat(CORNERS, -1f)
        cornerPx = if (saved < 0f) null else (if (saved <= 2f) saved * 16f else saved).coerceIn(0f, 60f)
    }

    /** Quiets or restores the Linux notification and saves it; the caller restarts the service. */
    fun setHideLinuxNotification(
        context: Context,
        hide: Boolean,
    ) {
        hideLinuxNotification = hide
        context.getSharedPreferences("launcher", Context.MODE_PRIVATE).edit().putBoolean(HIDE_LINUX, hide).apply()
    }

    /** Rounds every frame with [px] of radius (null follows the theme) and saves it. */
    fun setCornerPx(
        context: Context,
        px: Float?,
    ) {
        cornerPx = px?.coerceIn(0f, 60f)
        context.getSharedPreferences("launcher", Context.MODE_PRIVATE).edit().putFloat(CORNERS, px ?: -1f).apply()
    }

    /** Turns the frame halo on or off and saves it. */
    fun setGlow(
        context: Context,
        glow: Boolean,
    ) {
        this.glow = glow
        context.getSharedPreferences("launcher", Context.MODE_PRIVATE).edit().putBoolean(GLOW, glow).apply()
    }

    private const val HIDE_LINUX = "plus_hide_linux_notification"
    private const val CORNERS = "plus_corners"
    private const val GLOW = "plus_glow"
}
