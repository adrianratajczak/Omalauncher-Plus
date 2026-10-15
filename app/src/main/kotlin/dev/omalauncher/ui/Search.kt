package dev.omalauncher.ui

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.net.URLEncoder

/**
 * Where the bar's query goes besides apps and the agent: the web in the default browser, and the
 * app store. Both are picked in settings; the engine offers a custom URL too.
 */
object Search {
    /** The web engines the settings offer, Google first as the default. */
    enum class Engine(
        val id: String,
        val label: String,
        val template: String,
    ) {
        Google("google", "google", "https://www.google.com/search?q={q}"),
        DuckDuckGo("duckduckgo", "duckduckgo", "https://duckduckgo.com/?q={q}"),
        Bing("bing", "bing", "https://www.bing.com/search?q={q}"),
        Custom("custom", "custom", ""),
        ;

        companion object {
            fun of(id: String?) = entries.firstOrNull { it.id == id } ?: Google
        }
    }

    /** Where "search store" looks: the Play app (its site when it's absent), or F-Droid's site. */
    enum class Store(
        val id: String,
        val label: String,
    ) {
        Play("play", "play store"),
        FDroid("fdroid", "f-droid"),
        ;

        companion object {
            fun of(id: String?) = entries.firstOrNull { it.id == id } ?: Play
        }
    }

    var engine by mutableStateOf(Engine.Google)
        private set

    /** The custom engine's URL with {q} (or %s) for the query; used only with [Engine.Custom]. */
    var customTemplate by mutableStateOf("")
        private set

    var store by mutableStateOf(Store.Play)
        private set

    /** "google · play store", for the settings row. */
    val label: String get() = "${engine.label} · ${store.label}"

    /** Reads the saved engine and store; called once when the launcher starts. */
    fun load(context: Context) {
        val prefs = context.getSharedPreferences("launcher", Context.MODE_PRIVATE)
        engine = Engine.of(prefs.getString(ENGINE, null))
        customTemplate = prefs.getString(CUSTOM, "").orEmpty()
        store = Store.of(prefs.getString(STORE, null))
    }

    /** Picks [engine] and saves it; a custom one without a URL falls back to Google until it has one. */
    fun chooseEngine(
        context: Context,
        engine: Engine,
    ) {
        this.engine = engine
        context.getSharedPreferences("launcher", Context.MODE_PRIVATE).edit().putString(ENGINE, engine.id).apply()
    }

    /** Saves the custom engine's [template]; blank keeps the old one. */
    fun saveTemplate(
        context: Context,
        template: String,
    ) {
        if (template.isBlank()) return
        customTemplate = template.trim()
        context.getSharedPreferences("launcher", Context.MODE_PRIVATE).edit().putString(CUSTOM, customTemplate).apply()
    }

    /** Picks [store] and saves it. */
    fun chooseStore(
        context: Context,
        store: Store,
    ) {
        this.store = store
        context.getSharedPreferences("launcher", Context.MODE_PRIVATE).edit().putString(STORE, store.id).apply()
    }

    /** The web search URL for [query], opened in the default browser. */
    fun webUrl(query: String): String {
        val q = encoded(query)
        val template = if (engine == Engine.Custom) customTemplate else engine.template
        return when {
            "{q}" in template -> template.replace("{q}", q)
            "%s" in template -> template.replace("%s", q)
            template.isBlank() -> Engine.Google.template.replace("{q}", q)
            else -> template + q
        }
    }

    /** The store search for [query]: a market:// link for Play, an https URL otherwise. */
    fun storeUrl(query: String): String {
        val q = encoded(query)
        return when (store) {
            Store.Play -> "market://search?q=$q"
            Store.FDroid -> "https://f-droid.org/en/search?q=$q"
        }
    }

    /** The store search as a web URL, when no app opens the market:// link. */
    fun storeWebUrl(query: String): String {
        val q = encoded(query)
        return "https://play.google.com/store/search?q=$q&c=apps"
    }

    private fun encoded(query: String): String {
        val q = query.trim()
        return runCatching { URLEncoder.encode(q, "UTF-8") }.getOrDefault(q)
    }

    private const val ENGINE = "search_engine"
    private const val CUSTOM = "search_engine_custom"
    private const val STORE = "search_store"
}
