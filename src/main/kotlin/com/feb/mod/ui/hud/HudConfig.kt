package com.feb.mod.ui.hud

data class HudConfig(
    var enabled: Boolean = true,
    var entries: MutableMap<String, Boolean> = mutableMapOf()
)