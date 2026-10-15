package dev.omalauncher.ui

import androidx.compose.foundation.background
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
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import dev.omalauncher.Runtime

/**
 * Omalauncher+'s own settings, in one place so they never collide with
 * upstream's: icon packs and sizes, the Linux notification, and corners.
 */
@Composable
fun PlusScreen(
    runtime: Runtime,
    onDismiss: () -> Unit,
    onIconPacks: () -> Unit,
    onIconSize: () -> Unit,
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
            item(key = "glow-label") {
                SectionLabel("glow", Modifier.padding(start = 8.dp, top = 6.dp))
            }
            item(key = "glow") {
                PlusSwitch(
                    title = "frame glow",
                    subtitle = "a light accent halo on every border in the launcher",
                    on = PlusPrefs.glow,
                    onToggle = { PlusPrefs.setGlow(context, !PlusPrefs.glow) },
                )
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
