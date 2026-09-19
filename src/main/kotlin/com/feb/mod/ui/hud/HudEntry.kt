package com.feb.mod.ui.hud

interface HudEntry {
    val id: String
    val displayName: String
    fun getText(): String
}