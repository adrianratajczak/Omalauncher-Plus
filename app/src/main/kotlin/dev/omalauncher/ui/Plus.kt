package dev.omalauncher.ui

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.toArgb

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

    /** Draws adaptive icons without their backdrop, logo only; stock otherwise. */
    var adaptiveBackground by mutableStateOf(true)
        private set

    /** Where the icon tint comes from: none, the custom colors, or the theme's accent. */
    var tintSource by mutableStateOf(TintSource.Off)
        private set

    /** Multiplies every app icon toward this ARGB color; the custom source's top. */
    var tintTop: Int? by mutableStateOf(null)
        private set

    /** The gradient's bottom; with the top set, tints shade top to bottom keeping light. */
    var tintBottom: Int? by mutableStateOf(null)
        private set

    /** Blurs the wallpaper behind the window, so widget tiles frost over. Needs Android 12+. */
    var widgetBlur by mutableStateOf(false)
        private set

    /** Pins drawer app rows to the right edge, by the letter rail; left otherwise. */
    var drawerAlignRight by mutableStateOf(false)
        private set

    /** Shows the recent apps strip on home; off hides it everywhere. */
    var showRecents by mutableStateOf(true)
        private set

    /** App keys hidden from the recent strip by long-press; shown again from Plus. */
    var recentHidden: Set<String> by mutableStateOf(emptySet())
        private set

    /** Reads the saved Plus settings; called once when the launcher starts. */
    fun load(context: Context) {
        val prefs = context.getSharedPreferences("launcher", Context.MODE_PRIVATE)
        hideLinuxNotification = prefs.getBoolean(HIDE_LINUX, false)
        hideLabels = prefs.getBoolean(HIDE_LABELS, false)
        iconShape = IconShape.of(prefs.getString(ICON_SHAPE, null))
        grayscaleIcons = prefs.getBoolean(GRAYSCALE, false)
        adaptiveBackground = prefs.getBoolean(ADAPTIVE_BACKGROUND, true)
        // The old single tint becomes the custom source's top.
        tintTop = if (prefs.contains(ICON_TINT)) prefs.getInt(ICON_TINT, 0) else null
        tintBottom = if (prefs.contains(ICON_TINT_BOTTOM)) prefs.getInt(ICON_TINT_BOTTOM, 0) else null
        tintSource =
            when {
                tintTop == null && tintBottom == null -> TintSource.Off
                prefs.getBoolean(TINT_THEME, false) -> TintSource.Theme
                else -> TintSource.Custom
            }
        widgetBlur = prefs.getBoolean(WIDGET_BLUR, false)
        drawerAlignRight = prefs.getBoolean(DRAWER_ALIGN_RIGHT, false)
        showRecents = prefs.getBoolean(SHOW_RECENTS, true)
        recentHidden = prefs.getStringSet(RECENT_HIDDEN, emptySet()).orEmpty().toSet()
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

    /** Draws adaptive backdrops or not and saves it; the caller redraws the icons. */
    fun setAdaptiveBackground(
        context: Context,
        background: Boolean,
    ) {
        adaptiveBackground = background
        context.getSharedPreferences("launcher", Context.MODE_PRIVATE).edit().putBoolean(ADAPTIVE_BACKGROUND, background).apply()
    }

    /** Picks where the icon tint comes from and saves it; the caller redraws the icons. */
    fun setTintSource(
        context: Context,
        source: TintSource,
    ) {
        tintSource = source
        context.getSharedPreferences("launcher", Context.MODE_PRIVATE).edit().putBoolean(TINT_THEME, source == TintSource.Theme).apply()
    }

    /** Tints app icons toward [argb] (null leaves colors alone) and saves it; the caller redraws the icons. */
    fun setIconTint(
        context: Context,
        argb: Int?,
    ) {
        tintTop = argb
        context.getSharedPreferences("launcher", Context.MODE_PRIVATE).edit().apply {
            if (argb == null) remove(ICON_TINT) else putInt(ICON_TINT, argb)
        }.apply()
    }

    /** Tints the gradient's bottom (null leaves a single tint) and saves it; the caller redraws the icons. */
    fun setIconTintBottom(
        context: Context,
        argb: Int?,
    ) {
        tintBottom = argb
        context.getSharedPreferences("launcher", Context.MODE_PRIVATE).edit().apply {
            if (argb == null) remove(ICON_TINT_BOTTOM) else putInt(ICON_TINT_BOTTOM, argb)
        }.apply()
    }

    /**
     * The tint to draw with: the custom top (and bottom for a gradient), or the
     * theme's accent, which follows the theme. Read at render time, off the main thread.
     */
    fun resolvedTints(): Pair<Int?, Int?> =
        when (tintSource) {
            TintSource.Off -> null to null
            TintSource.Theme -> Palette.accent.toArgb() to null
            TintSource.Custom -> tintTop to tintBottom
        }

    /** Blurs the wallpaper behind widgets and saves it. */
    fun setWidgetBlur(
        context: Context,
        blur: Boolean,
    ) {
        widgetBlur = blur
        context.getSharedPreferences("launcher", Context.MODE_PRIVATE).edit().putBoolean(WIDGET_BLUR, blur).apply()
    }

    /** Pins drawer rows left or right and saves it; the drawer redraws itself. */
    fun setDrawerAlignRight(
        context: Context,
        right: Boolean,
    ) {
        drawerAlignRight = right
        context.getSharedPreferences("launcher", Context.MODE_PRIVATE).edit().putBoolean(DRAWER_ALIGN_RIGHT, right).apply()
    }

    /** Shows or hides the recent apps strip and saves it; home redraws itself. */
    fun setShowRecents(
        context: Context,
        show: Boolean,
    ) {
        showRecents = show
        context.getSharedPreferences("launcher", Context.MODE_PRIVATE).edit().putBoolean(SHOW_RECENTS, show).apply()
    }

    /** Hides an app from the recent strip (or shows it again) and saves it. */
    fun setRecentHidden(
        context: Context,
        key: String,
        hidden: Boolean,
    ) {
        recentHidden = if (hidden) recentHidden + key else recentHidden - key
        context.getSharedPreferences("launcher", Context.MODE_PRIVATE).edit().putStringSet(RECENT_HIDDEN, recentHidden).apply()
    }

    private const val HIDE_LINUX = "plus_hide_linux_notification"
    private const val CORNERS = "plus_corners"
    private const val HIDE_LABELS = "plus_hide_labels"
    private const val ICON_SHAPE = "plus_icon_shape"
    private const val GRAYSCALE = "plus_grayscale_icons"
    private const val ICON_TINT = "plus_icon_tint"
    private const val ICON_TINT_BOTTOM = "plus_icon_tint_bottom"
    private const val TINT_THEME = "plus_tint_theme"
    private const val ADAPTIVE_BACKGROUND = "plus_adaptive_background"
    private const val WIDGET_BLUR = "plus_widget_blur"
    private const val DRAWER_ALIGN_RIGHT = "plus_drawer_align_right"
    private const val SHOW_RECENTS = "plus_show_recents"
    private const val RECENT_HIDDEN = "plus_recent_hidden"
}
