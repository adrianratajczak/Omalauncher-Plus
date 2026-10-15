package dev.omalauncher.plus.lockscreen

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/** The blocks the lock screen can show, toggled in the setup. The clock stays first. */
enum class Block(
    val id: String,
    val label: String,
) {
    Clock("clock", "clock"),
    Date("date", "date"),
    Battery("battery", "battery"),
    Actions("actions", "flashlight and camera"),
}

/** Which blocks show, saved per block; everything is on until the setup says otherwise. */
object Blocks {
    var enabled by mutableStateOf(setOf(Block.Clock, Block.Date, Block.Battery, Block.Actions))
        private set

    /** Reads the saved blocks; called when the lock screen and the setup show. */
    fun load(context: Context) {
        val prefs = context.getSharedPreferences("lockscreen", Context.MODE_PRIVATE)
        val saved = prefs.getStringSet(ENABLED, null)
        enabled = saved?.mapNotNull { id -> Block.entries.firstOrNull { it.id == id } }?.toSet() ?: enabled
    }

    /** Flips [block] and saves the set. */
    fun toggle(
        context: Context,
        block: Block,
    ) {
        enabled = if (block in enabled) enabled - block else enabled + block
        context.getSharedPreferences("lockscreen", Context.MODE_PRIVATE).edit().putStringSet(ENABLED, enabled.map { it.id }.toSet()).apply()
    }

    private const val ENABLED = "blocks"
}
