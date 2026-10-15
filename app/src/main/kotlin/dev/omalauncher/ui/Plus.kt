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
    /** The corner override for the whole launcher; null follows style.toml's corners.scale. */
    val cornerOptions = listOf(null to "theme", 0f to "edge", 0.5f to "soft", 1f to "round", 2f to "full")

    /** Quiets the Linux session notification: silent, no status bar icon, still in the shade. */
    var hideLinuxNotification by mutableStateOf(false)
        private set

    /** The corner override; null follows the theme. */
    var corners: Float? by mutableStateOf(null)
        private set

    /** Reads the saved Plus settings; called once when the launcher starts. */
    fun load(context: Context) {
        val prefs = context.getSharedPreferences("launcher", Context.MODE_PRIVATE)
        hideLinuxNotification = prefs.getBoolean(HIDE_LINUX, false)
        corners = prefs.getFloat(CORNERS, -1f).takeIf { it >= 0f }
    }

    /** Quiets or restores the Linux notification and saves it; the caller restarts the service. */
    fun setHideLinuxNotification(
        context: Context,
        hide: Boolean,
    ) {
        hideLinuxNotification = hide
        context.getSharedPreferences("launcher", Context.MODE_PRIVATE).edit().putBoolean(HIDE_LINUX, hide).apply()
    }

    /** Overrides the theme's corners with [scale] (null follows the theme) and saves it. */
    fun setCorners(
        context: Context,
        scale: Float?,
    ) {
        corners = scale
        context.getSharedPreferences("launcher", Context.MODE_PRIVATE).edit().putFloat(CORNERS, scale ?: -1f).apply()
    }

    private const val HIDE_LINUX = "plus_hide_linux_notification"
    private const val CORNERS = "plus_corners"
}
