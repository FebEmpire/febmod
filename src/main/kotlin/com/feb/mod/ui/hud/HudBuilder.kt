package com.feb.mod.ui.hud

class HudBuilder {
    private val lines = mutableListOf<() -> String>()

    fun text(value: String) {
        lines.add { value }
    }

    fun text(value: () -> String) {
        lines.add(value)
    }

    internal fun build(): List<() -> String> = lines.toList()
}
