package com.feb.mod.ui.hud

import com.feb.mod.manager.ConfigManager

object HudSettings {
    fun isEnabled(id: String): Boolean {
        return ConfigManager.current.hud.entries[id] ?: true
    }

    fun setEnabled(id: String, enabled: Boolean) {
        ConfigManager.current.hud.entries[id] = enabled
        ConfigManager.save()
    }

    fun isHudEnabled(): Boolean {
        return ConfigManager.current.hud.enabled
    }

    fun setHudEnabled(enabled: Boolean) {
        ConfigManager.current.hud.enabled = enabled
        ConfigManager.save()
    }
}