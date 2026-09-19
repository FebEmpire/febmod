package com.feb.mod.ui.hud.entries

import com.feb.mod.api.provider.minecraft.FPS
import com.feb.mod.ui.hud.HudEntry

object FpsEntry : HudEntry {
    override val id = "fps"
    override val displayName = "FPS"

    override fun getText(): String {
        return "FPS: ${FPS.get()}"
    }
}