package dev.omalauncher.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

/**
 * Where the bar's actions search: the web engine (Google, DuckDuckGo, Bing or a custom URL with
 * {q} for the query), and the app store (Play or F-Droid). The rows above the bar offer both;
 * Enter in the bar still asks the agent.
 */
@Composable
fun SearchSettingsScreen(onDismiss: () -> Unit) {
    val context = LocalContext.current
    OverlayPanel("search", onDismiss, contentWidth = ReadingWidth) {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            item(key = "web-label") {
                SectionLabel("web", Modifier.padding(start = 8.dp, top = 6.dp))
            }
            items(Search.Engine.entries, key = { it.id }) { engine ->
                val picked = Search.engine == engine
                SearchRow(
                    label = engine.label,
                    subtitle = if (engine == Search.Engine.Custom) Search.customTemplate.ifBlank { "a url with {q} for the query" } else engine.template,
                    picked = picked,
                    onPick = { Search.chooseEngine(context, engine) },
                )
            }
            if (Search.engine == Search.Engine.Custom) {
                item(key = "custom-url") {
                    var template by remember(Search.customTemplate) { mutableStateOf(Search.customTemplate) }
                    SearchField(
                        query = template,
                        onChange = { template = it },
                        onGo = { Search.saveTemplate(context, template) },
                        placeholder = "https://search.example/?q={q}",
                        modifier = Modifier.fillMaxWidth().padding(top = 2.dp),
                    )
                }
            }
            item(key = "store-label") {
                SectionLabel("app store", Modifier.padding(start = 8.dp, top = 6.dp))
            }
            items(Search.Store.entries, key = { it.id }) { store ->
                val picked = Search.store == store
                SearchRow(
                    label = store.label,
                    subtitle = if (store == Search.Store.Play) "the play app, its site without it" else "f-droid.org",
                    picked = picked,
                    onPick = { Search.chooseStore(context, store) },
                )
            }
        }
    }
}

/** One engine or store: its name and URL, marked when picked. */
@Composable
private fun SearchRow(
    label: String,
    subtitle: String,
    picked: Boolean,
    onPick: () -> Unit,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(LauncherStyle.shape(12.dp))
            .background(if (picked) Palette.accent.copy(alpha = 0.12f) else Palette.surface)
            .clickable(onClick = onPick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            BasicText(label, style = Mono.copy(color = if (picked) Palette.accent else Palette.foreground, fontSize = TextSize.Item))
            BasicText(subtitle, style = Mono.copy(color = Palette.muted, fontSize = TextSize.Small), maxLines = 1)
        }
        BasicText(if (picked) "● " else "  ", style = Mono.copy(color = Palette.accent))
    }
}
