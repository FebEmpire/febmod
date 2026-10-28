package com.feb.mod.ui.hud

import com.feb.mod.manager.ConfigManager

data class AddonHud(
    val ownerId: String,
    val id: String,
    val displayName: String,
    val lineProviders: List<() -> String>
) {
    val fullId: String
        get() = "$ownerId:$id"

    fun getLines(): List<String> = lineProviders.map { line ->
        runCatching { line() }.getOrDefault("HUD error")
    }
}

object AddonHudManager {
    private val huds = linkedMapOf<String, AddonHud>()

    fun register(
        ownerId: String,
        id: String,
        displayName: String,
        lines: List<() -> String>
    ) {
        val hud = AddonHud(ownerId, id, displayName, lines)
        huds[hud.fullId] = hud

        configFor(hud)
    }

    fun unregister(fullId: String) {
        huds.remove(fullId)
    }

    fun unregisterOwner(ownerId: String) {
        huds.entries.removeIf { it.value.ownerId == ownerId }
    }

    fun getAll(): List<AddonHud> = huds.values.toList()

    fun getForOwner(ownerId: String): List<AddonHud> =
        huds.values.filter { it.ownerId == ownerId }

    fun configFor(hud: AddonHud): ConfigManager.AddonHudConfig {
        val configs = ConfigManager.current.addonHuds

        return configs.getOrPut(hud.fullId) {
            ConfigManager.AddonHudConfig(
                x = 10,
                y = 10 + huds.keys.indexOf(hud.fullId).coerceAtLeast(0) * 30
            )
        }
    }
}
