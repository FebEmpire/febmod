package com.feb.mod.ui.hud

import com.feb.mod.ui.hud.entries.FpsEntry

object HudManager {
    private val entries = mutableListOf<HudEntry>()

    fun register(entry: HudEntry) {
        entries.removeIf { it.id == entry.id }
        entries.add(entry)
    }

    fun registerDefaults() {
        register(FpsEntry)
    }

    fun getEntries(): List<HudEntry> {
        return entries
    }

    fun get(id: String): HudEntry? {
        return entries.firstOrNull { it.id == id }
    }

    fun clear() {
        entries.clear()
    }
}