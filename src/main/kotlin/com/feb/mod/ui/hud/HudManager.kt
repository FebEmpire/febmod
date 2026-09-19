package com.feb.mod.ui.hud

import com.feb.mod.ui.hud.entries.FpsEntry
import com.feb.mod.ui.hud.entries.LtcEntry

object HudManager {
    private val entries = mutableListOf<HudEntry>()

    fun register(entry: HudEntry) {
        entries.removeIf { it.id == entry.id }
        entries.add(entry)
    }

    fun registerDefaults() {
        register(FpsEntry)
        register(LtcEntry)
    }

    fun getEntries(): List<HudEntry> {
        return entries.filter { HudSettings.isEnabled(it.id) }
    }

    fun getAllEntries(): List<HudEntry> {
        return entries
    }

    fun get(id: String): HudEntry? {
        return entries.firstOrNull { it.id == id }
    }

    fun clear() {
        entries.clear()
    }
}