package dev.omalauncher.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
                val hide = PlusPrefs.hideLinuxNotification
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clip(LauncherStyle.shape(12.dp))
                        .background(Palette.surface)
                        .clickable {
                            PlusPrefs.setHideLinuxNotification(context, !hide)
                            runtime.refreshLinuxService()
                        }.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(Modifier.weight(1f)) {
                        BasicText("hide linux notification", style = Mono.copy(fontSize = TextSize.Item))
                        BasicText(
                            "silent, no status bar icon · progress stays in settings › linux",
                            style = Mono.copy(color = Palette.muted, fontSize = TextSize.Small),
                        )
                    }
                    BasicText(
                        if (hide) "on" else "off",
                        style = Mono.copy(color = if (hide) Palette.accent else Palette.muted),
                    )
                }
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
                    BasicText("rounds every frame in the launcher, over what the theme sets", style = Mono.copy(color = Palette.muted, fontSize = TextSize.Small))
                    val options = PlusPrefs.cornerOptions
                    val selected = options.indexOfFirst { it.first == PlusPrefs.corners }
                    Box(Modifier.padding(top = 10.dp).horizontalScroll(rememberScrollState())) {
                        Row(Modifier.frame(1.dp, Palette.border, radius = 12.dp)) {
                            options.forEachIndexed { i, (scale, label) ->
                                val active = i == selected
                                BasicText(
                                    label,
                                    Modifier
                                        .background(if (active) Palette.accent else Color.Transparent)
                                        .clickable { PlusPrefs.setCorners(context, scale) }
                                        .padding(horizontal = 14.dp, vertical = 8.dp),
                                    style = Mono.copy(color = if (active) Palette.background else Palette.foreground),
                                    softWrap = false,
                                )
                            }
                        }
                    }
                }
            }
        }
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
