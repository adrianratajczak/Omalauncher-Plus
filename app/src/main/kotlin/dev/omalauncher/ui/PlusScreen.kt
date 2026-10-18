package dev.omalauncher.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import dev.omalauncher.Runtime
import dev.omalauncher.theme.BundledTheme

/**
 * Omalauncher+'s own settings, in one place so they never collide with
 * upstream's: icons, day & night, home, drawer, labels, search, widgets,
 * notifications, and corners.
 */
@Composable
fun PlusScreen(
    runtime: Runtime,
    onDismiss: () -> Unit,
    onIconPacks: () -> Unit,
    onIconSize: () -> Unit,
    onSearch: () -> Unit,
) {
    val context = LocalContext.current
    val packSubtitle =
        when (runtime.iconPacks.chosen) {
            null -> "the apps' own icons"
            else -> runtime.iconPacks.chosen?.label.orEmpty()
        }
    OverlayPanel("omalauncher+", onDismiss, contentWidth = ReadingWidth) {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            item(key = "icons-label") {
                SectionLabel("icons", Modifier.padding(start = 8.dp, top = 6.dp))
            }
            item(key = "icon-pack") {
                PlusRow("icon pack", packSubtitle, onIconPacks)
            }
            item(key = "icon-size") {
                PlusRow("icon size", IconSize.label, onIconSize)
            }
            item(key = "icon-backdrop") {
                PlusSwitch(
                    title = "adaptive background",
                    subtitle = "off draws logo-only icons without their backdrop",
                    on = PlusPrefs.adaptiveBackground,
                    onToggle = {
                        PlusPrefs.setAdaptiveBackground(context, !PlusPrefs.adaptiveBackground)
                        runtime.redrawAllIcons()
                    },
                )
            }
            item(key = "icon-shape") {
                val shape = PlusPrefs.iconShape
                Column(
                    Modifier
                        .fillMaxWidth()
                        .clip(LauncherStyle.shape(12.dp))
                        .background(Palette.surface)
                        .padding(14.dp),
                ) {
                    BasicText("icon shape", style = Mono.copy(fontSize = TextSize.Item))
                    Box(Modifier.padding(top = 10.dp).horizontalScroll(rememberScrollState())) {
                        Row(Modifier.frame(1.dp, Palette.border, radius = 12.dp)) {
                            IconShape.entries.forEach { option ->
                                val active = option == shape
                                BasicText(
                                    option.label,
                                    Modifier
                                        .background(if (active) Palette.accent else Color.Transparent)
                                        .clickable {
                                            PlusPrefs.setIconShape(context, option)
                                            runtime.redrawAllIcons()
                                        }.padding(horizontal = 14.dp, vertical = 8.dp),
                                    style = Mono.copy(color = if (active) Palette.background else Palette.foreground),
                                    softWrap = false,
                                )
                            }
                        }
                    }
                }
            }
            item(key = "icon-gray") {
                PlusSwitch(
                    title = "grayscale icons",
                    subtitle = "drains every app icon of color",
                    on = PlusPrefs.grayscaleIcons,
                    onToggle = {
                        PlusPrefs.setGrayscaleIcons(context, !PlusPrefs.grayscaleIcons)
                        runtime.redrawAllIcons()
                    },
                )
            }
            item(key = "icon-tint") {
                val source = PlusPrefs.tintSource
                Column(
                    Modifier
                        .fillMaxWidth()
                        .clip(LauncherStyle.shape(12.dp))
                        .background(Palette.surface)
                        .padding(14.dp),
                ) {
                    BasicText("icon tint", style = Mono.copy(fontSize = TextSize.Item))
                    BasicText(
                        "recolors every app icon · theme takes the accent",
                        style = Mono.copy(color = Palette.muted, fontSize = TextSize.Small),
                    )
                    Box(Modifier.padding(top = 10.dp).horizontalScroll(rememberScrollState())) {
                        Row(Modifier.frame(1.dp, Palette.border, radius = 12.dp)) {
                            TintSource.entries.forEach { option ->
                                val active = option == source
                                BasicText(
                                    option.label,
                                    Modifier
                                        .background(if (active) Palette.accent else Color.Transparent)
                                        .clickable {
                                            PlusPrefs.setTintSource(context, option)
                                            runtime.redrawAllIcons()
                                        }.padding(horizontal = 14.dp, vertical = 8.dp),
                                    style = Mono.copy(color = if (active) Palette.background else Palette.foreground),
                                    softWrap = false,
                                )
                            }
                        }
                    }
                    if (source == TintSource.Custom) {
                        val (top, bottom) = PlusPrefs.tintTop to PlusPrefs.tintBottom
                        BasicText(
                            "top · alone it multiplies, with bottom it shades a gradient",
                            Modifier.padding(top = 10.dp),
                            style = Mono.copy(color = Palette.muted, fontSize = TextSize.Small),
                        )
                        TintRow(top, { PlusPrefs.setIconTint(context, it); runtime.redrawAllIcons() })
                        BasicText(
                            "bottom · empty keeps the single tint",
                            Modifier.padding(top = 6.dp),
                            style = Mono.copy(color = Palette.muted, fontSize = TextSize.Small),
                        )
                        TintRow(bottom, { PlusPrefs.setIconTintBottom(context, it); runtime.redrawAllIcons() })
                    }
                }
            }
            item(key = "daynight-label") {
                SectionLabel("day & night", Modifier.padding(start = 8.dp, top = 6.dp))
            }
            item(key = "daynight-auto") {
                PlusSwitch(
                    title = "automatic themes",
                    subtitle = "switches day/night themes at the set hours · auto wins while on",
                    on = PlusPrefs.dayNightAuto,
                    onToggle = {
                        PlusPrefs.setDayNightAuto(context, !PlusPrefs.dayNightAuto)
                        runtime.syncDayNight()
                    },
                )
            }
            if (PlusPrefs.dayNightAuto) {
                item(key = "daynight-day") {
                    ThemePicker(
                        title = "day theme",
                        subtitle = "none keeps whatever shows",
                        selected = PlusPrefs.dayTheme,
                        choices = runtime.themes.choices,
                        onPick = {
                            PlusPrefs.setDayTheme(context, it)
                            runtime.syncDayNight()
                        },
                    )
                }
                item(key = "daynight-day-hour") {
                    HourPicker(
                        title = "day starts",
                        selected = PlusPrefs.dayHour,
                        onPick = {
                            PlusPrefs.setDayHour(context, it)
                            runtime.syncDayNight()
                        },
                    )
                }
                item(key = "daynight-night") {
                    ThemePicker(
                        title = "night theme",
                        subtitle = "none keeps whatever shows",
                        selected = PlusPrefs.nightTheme,
                        choices = runtime.themes.choices,
                        onPick = {
                            PlusPrefs.setNightTheme(context, it)
                            runtime.syncDayNight()
                        },
                    )
                }
                item(key = "daynight-night-hour") {
                    HourPicker(
                        title = "night starts",
                        selected = PlusPrefs.nightHour,
                        onPick = {
                            PlusPrefs.setNightHour(context, it)
                            runtime.syncDayNight()
                        },
                    )
                }
            }
            item(key = "home-label") {
                SectionLabel("home", Modifier.padding(start = 8.dp, top = 6.dp))
            }
            item(key = "labels-label") {
                SectionLabel("labels", Modifier.padding(start = 8.dp, top = 6.dp))
            }
            item(key = "hide-home-labels") {
                PlusSwitch(
                    title = "hide home labels",
                    subtitle = "no names under icons on home",
                    on = PlusPrefs.hideHomeLabels,
                    onToggle = { PlusPrefs.setHideHomeLabels(context, !PlusPrefs.hideHomeLabels) },
                )
            }
            item(key = "gridless") {
                PlusSwitch(
                    title = "gridless",
                    subtitle = "free placement, no snapping, widgets can overlap",
                    on = PlusPrefs.gridless,
                    onToggle = { PlusPrefs.setGridless(context, !PlusPrefs.gridless) },
                )
            }
            item(key = "hide-drawer-labels") {
                PlusSwitch(
                    title = "hide drawer labels",
                    subtitle = "no names under icons in the drawer",
                    on = PlusPrefs.hideDrawerLabels,
                    onToggle = { PlusPrefs.setHideDrawerLabels(context, !PlusPrefs.hideDrawerLabels) },
                )
            }
            item(key = "drawer-label") {
                SectionLabel("drawer", Modifier.padding(start = 8.dp, top = 6.dp))
            }
            item(key = "drawer-align") {
                PlusSwitch(
                    title = "align apps right",
                    subtitle = "pins drawer rows to the letter rail · off keeps them left",
                    on = PlusPrefs.drawerAlignRight,
                    onToggle = { PlusPrefs.setDrawerAlignRight(context, !PlusPrefs.drawerAlignRight) },
                )
            }
            item(key = "drawer-hidden-label") {
                SectionLabel("hidden from recents", Modifier.padding(start = 8.dp, top = 6.dp))
            }
            run {
                val hidden = PlusPrefs.recentHidden.sorted()
                if (hidden.isEmpty()) {
                    item(key = "drawer-hidden-empty") {
                        BasicText(
                            "nothing hidden · long-press a recent app to hide it",
                            Modifier.padding(start = 8.dp, top = 2.dp, bottom = 2.dp),
                            style = Mono.copy(color = Palette.muted, fontSize = TextSize.Small),
                        )
                    }
                } else {
                    hidden.forEach { key ->
                        item(key = "drawer-hidden:$key") {
                            PlusRow(
                                appHitByKey(runtime, key)?.label ?: key,
                                "tap to show in recents again",
                                onClick = { PlusPrefs.setRecentHidden(context, key, false) },
                            )
                        }
                    }
                }
            }
            item(key = "search-label") {
                SectionLabel("search", Modifier.padding(start = 8.dp, top = 6.dp))
            }
            item(key = "search") {
                PlusRow("search", Search.label, onSearch)
            }
            item(key = "widgets-label") {
                SectionLabel("widgets", Modifier.padding(start = 8.dp, top = 6.dp))
            }
            item(key = "widget-blur") {
                PlusSwitch(
                    title = "blurred background",
                    subtitle = "frosts the wallpaper behind widget tiles · android 12+",
                    on = PlusPrefs.widgetBlur,
                    onToggle = { PlusPrefs.setWidgetBlur(context, !PlusPrefs.widgetBlur) },
                )
            }
            item(key = "notifications-label") {
                SectionLabel("notifications", Modifier.padding(start = 8.dp, top = 6.dp))
            }
            item(key = "hide-linux") {
                PlusSwitch(
                    title = "hide linux notification",
                    subtitle = "gone while idle · still shows installs and errors",
                    on = PlusPrefs.hideLinuxNotification,
                    onToggle = {
                        PlusPrefs.setHideLinuxNotification(context, !PlusPrefs.hideLinuxNotification)
                        runtime.refreshLinuxService()
                    },
                )
            }
            item(key = "corners-label") {
                SectionLabel("corners", Modifier.padding(start = 8.dp, top = 6.dp))
            }
            item(key = "corners") {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .clip(LauncherStyle.shape(12.dp))
                        .background(Palette.surface)
                        .padding(14.dp),
                ) {
                    val px = PlusPrefs.cornerPx
                    val options = listOf("theme", "custom")
                    Box(Modifier.horizontalScroll(rememberScrollState())) {
                        Row(Modifier.frame(1.dp, Palette.border, radius = 12.dp)) {
                            options.forEachIndexed { i, label ->
                                val active = (i == 0) == (px == null)
                                BasicText(
                                    label,
                                    Modifier
                                        .background(if (active) Palette.accent else Color.Transparent)
                                        .clickable { PlusPrefs.setCornerPx(context, if (i == 0) null else px ?: 16f) }
                                        .padding(horizontal = 14.dp, vertical = 8.dp),
                                    style = Mono.copy(color = if (active) Palette.background else Palette.foreground),
                                    softWrap = false,
                                )
                            }
                        }
                    }
                    if (px != null) {
                        BasicText(
                            "${px.toInt()}px",
                            Modifier.padding(top = 10.dp),
                            style = Mono.copy(color = Palette.accent, fontSize = TextSize.Item),
                        )
                        PxSlider(px) { PlusPrefs.setCornerPx(context, it) }
                    } else {
                        BasicText(
                            "the theme's corners.scale, as upstream styles it",
                            Modifier.padding(top = 10.dp),
                            style = Mono.copy(color = Palette.muted, fontSize = TextSize.Small),
                        )
                    }
                }
            }
        }
    }
}

/** One tint color row: none, the swatches, and a #rrggbb field. */
@Composable
private fun TintRow(
    selected: Int?,
    onPick: (Int?) -> Unit,
) {
    Row(
        Modifier
            .padding(top = 10.dp)
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BasicText(
            "none",
            Modifier
                .clip(LauncherStyle.shape(16.dp))
                .background(if (selected == null) Palette.accent else Color.Transparent)
                .clickable { onPick(null) }
                .padding(horizontal = 14.dp, vertical = 8.dp),
            style = Mono.copy(color = if (selected == null) Palette.background else Palette.foreground),
        )
        for (color in TintColors) {
            val active = selected == color.toArgb()
            Box(
                Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(color)
                    .then(if (active) Modifier.border(2.dp, Palette.accent, CircleShape) else Modifier)
                    .clickable { onPick(color.toArgb()) },
            )
        }
    }
    var hex by remember(selected) { mutableStateOf("") }
    SearchField(
        query = hex,
        onChange = { hex = it },
        onGo = {
            hexColor(hex)?.let {
                onPick(it.toArgb())
                hex = ""
            }
        },
        placeholder = "#rrggbb",
        modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
    )
}

/** The tint swatches the icons section offers, besides none and any #rrggbb below. */
private val TintColors =
    listOf(
        Color(0xFFF44336),
        Color(0xFFFF9800),
        Color(0xFFFFEB3B),
        Color(0xFF4CAF50),
        Color(0xFF009688),
        Color(0xFF03A9F4),
        Color(0xFF2196F3),
        Color(0xFF9C27B0),
        Color(0xFFE91E63),
        Color(0xFFFFFFFF),
    )

/** One theme picker: none, then every installed theme. */
@Composable
private fun ThemePicker(
    title: String,
    subtitle: String,
    selected: String?,
    choices: List<BundledTheme>,
    onPick: (String?) -> Unit,
) {
    Column(
        Modifier
            .fillMaxWidth()
            .clip(LauncherStyle.shape(12.dp))
            .background(Palette.surface)
            .padding(14.dp),
    ) {
        BasicText(title, style = Mono.copy(fontSize = TextSize.Item))
        BasicText(subtitle, style = Mono.copy(color = Palette.muted, fontSize = TextSize.Small))
        Box(Modifier.padding(top = 10.dp).horizontalScroll(rememberScrollState())) {
            Row(Modifier.frame(1.dp, Palette.border, radius = 12.dp)) {
                BasicText(
                    "none",
                    Modifier
                        .background(if (selected == null) Palette.accent else Color.Transparent)
                        .clickable { onPick(null) }
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    style = Mono.copy(color = if (selected == null) Palette.background else Palette.foreground),
                    softWrap = false,
                )
                choices.forEach { choice ->
                    val active = choice.name == selected
                    BasicText(
                        choice.title,
                        Modifier
                            .background(if (active) Palette.accent else Color.Transparent)
                            .clickable { onPick(choice.name) }
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        style = Mono.copy(color = if (active) Palette.background else Palette.foreground),
                        softWrap = false,
                    )
                }
            }
        }
    }
}

/** One hour picker: 0 to 23 as o'clock chips. */
@Composable
private fun HourPicker(
    title: String,
    selected: Int,
    onPick: (Int) -> Unit,
) {
    Column(
        Modifier
            .fillMaxWidth()
            .clip(LauncherStyle.shape(12.dp))
            .background(Palette.surface)
            .padding(14.dp),
    ) {
        BasicText(title, style = Mono.copy(fontSize = TextSize.Item))
        BasicText("$selected:00", style = Mono.copy(color = Palette.accent, fontSize = TextSize.Item))
        Box(Modifier.padding(top = 10.dp).horizontalScroll(rememberScrollState())) {
            Row(Modifier.frame(1.dp, Palette.border, radius = 12.dp)) {
                for (hour in 0..23) {
                    val active = hour == selected
                    BasicText(
                        "$hour:00",
                        Modifier
                            .background(if (active) Palette.accent else Color.Transparent)
                            .clickable { onPick(hour) }
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        style = Mono.copy(color = if (active) Palette.background else Palette.foreground),
                        softWrap = false,
                    )
                }
            }
        }
    }
}

/** One on/off row, like the main settings' rows with a switch at the end. */
@Composable
private fun PlusSwitch(
    title: String,
    subtitle: String,
    on: Boolean,
    onToggle: () -> Unit,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(LauncherStyle.shape(12.dp))
            .background(Palette.surface)
            .clickable(onClick = onToggle)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            BasicText(title, style = Mono.copy(fontSize = TextSize.Item))
            BasicText(subtitle, style = Mono.copy(color = Palette.muted, fontSize = TextSize.Small))
        }
        BasicText(
            if (on) "on" else "off",
            style = Mono.copy(color = if (on) Palette.accent else Palette.muted),
        )
    }
}

/** A 0 to 60px slider: tap to jump, drag to slide. */
@Composable
private fun PxSlider(
    px: Float,
    onChange: (Float) -> Unit,
) {
    var widthPx by remember { mutableIntStateOf(0) }
    val density = LocalDensity.current
    Box(
        Modifier
            .fillMaxWidth()
            .height(36.dp)
            .onSizeChanged { widthPx = it.width }
            .pointerInput(widthPx) {
                fun set(x: Float) {
                    if (widthPx > 0) onChange(((x / widthPx) * 60f).coerceIn(0f, 60f))
                }
                awaitEachGesture {
                    val down = awaitFirstDown()
                    set(down.position.x)
                    do {
                        val event = awaitPointerEvent()
                        event.changes.firstOrNull { it.pressed }?.let {
                            it.consume()
                            set(it.position.x)
                        }
                    } while (event.changes.any { it.pressed })
                }
            },
        contentAlignment = Alignment.CenterStart,
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(LauncherStyle.shape(2.dp))
                .background(Palette.muted.copy(alpha = 0.35f)),
        )
        Box(
            Modifier
                .fillMaxWidth(px / 60f)
                .height(4.dp)
                .clip(LauncherStyle.shape(2.dp))
                .background(Palette.accent),
        )
        Box(
            Modifier
                .offset { IntOffset(((px / 60f) * widthPx - with(density) { 10.dp.toPx() }).toInt(), 0) }
                .size(20.dp)
                .clip(CircleShape)
                .background(Palette.accent),
        )
    }
}

/** One row opening another screen, like the main settings' rows. */
@Composable
private fun PlusRow(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(LauncherStyle.shape(12.dp))
            .background(Palette.surface)
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            BasicText(title, style = Mono.copy(fontSize = TextSize.Item))
            BasicText(subtitle, style = Mono.copy(color = Palette.muted, fontSize = TextSize.Small), maxLines = 1)
        }
    }
}
