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

    /** Quiets the Linux session notification: gone while idle, still shows installs and errors. */
    var hideLinuxNotification by mutableStateOf(false)
        private set

    /** Hides app names under icons, on home and in the drawer; icons grow into the room. */
    var hideLabels by mutableStateOf(false)
        private set

    /** Reshapes every app icon; system keeps each icon as it ships. */
    var iconShape by mutableStateOf(IconShape.System)
        private set

    /** Drains every app icon of color. */
    var grayscaleIcons by mutableStateOf(false)
        private set

    /** Multiplies every app icon toward this ARGB color; null leaves colors alone. */
    var iconTint: Int? by mutableStateOf(null)
        private set

    /** Blurs the wallpaper behind the window, so widget tiles frost over. Needs Android 12+. */
    var widgetBlur by mutableStateOf(false)
        private set

    /** Reads the saved Plus settings; called once when the launcher starts. */
    fun load(context: Context) {
        val prefs = context.getSharedPreferences("launcher", Context.MODE_PRIVATE)
        hideLinuxNotification = prefs.getBoolean(HIDE_LINUX, false)
        hideLabels = prefs.getBoolean(HIDE_LABELS, false)
        iconShape = IconShape.of(prefs.getString(ICON_SHAPE, null))
        grayscaleIcons = prefs.getBoolean(GRAYSCALE, false)
        iconTint = if (prefs.contains(ICON_TINT)) prefs.getInt(ICON_TINT, 0) else null
        widgetBlur = prefs.getBoolean(WIDGET_BLUR, false)
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

    /** Hides app names under icons and saves it; the tiles redraw themselves. */
    fun setHideLabels(
        context: Context,
        hide: Boolean,
    ) {
        hideLabels = hide
        context.getSharedPreferences("launcher", Context.MODE_PRIVATE).edit().putBoolean(HIDE_LABELS, hide).apply()
    }

    /** Reshapes app icons and saves it; the caller redraws the icons. */
    fun setIconShape(
        context: Context,
        shape: IconShape,
    ) {
        iconShape = shape
        context.getSharedPreferences("launcher", Context.MODE_PRIVATE).edit().putString(ICON_SHAPE, shape.id).apply()
    }

    /** Drains app icons of color and saves it; the caller redraws the icons. */
    fun setGrayscaleIcons(
        context: Context,
        gray: Boolean,
    ) {
        grayscaleIcons = gray
        context.getSharedPreferences("launcher", Context.MODE_PRIVATE).edit().putBoolean(GRAYSCALE, gray).apply()
    }

    /** Tints app icons toward [argb] (null leaves colors alone) and saves it; the caller redraws the icons. */
    fun setIconTint(
        context: Context,
        argb: Int?,
    ) {
        iconTint = argb
        context.getSharedPreferences("launcher", Context.MODE_PRIVATE).edit().apply {
            if (argb == null) remove(ICON_TINT) else putInt(ICON_TINT, argb)
        }.apply()
    }

    /** Blurs the wallpaper behind widgets and saves it. */
    fun setWidgetBlur(
        context: Context,
        blur: Boolean,
    ) {
        widgetBlur = blur
        context.getSharedPreferences("launcher", Context.MODE_PRIVATE).edit().putBoolean(WIDGET_BLUR, blur).apply()
    }

    private const val HIDE_LINUX = "plus_hide_linux_notification"
    private const val CORNERS = "plus_corners"
    private const val HIDE_LABELS = "plus_hide_labels"
    private const val ICON_SHAPE = "plus_icon_shape"
    private const val GRAYSCALE = "plus_grayscale_icons"
    private const val ICON_TINT = "plus_icon_tint"
    private const val WIDGET_BLUR = "plus_widget_blur"
}
