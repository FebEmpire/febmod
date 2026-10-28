package com.feb.mod.api.ui

import com.feb.mod.ui.hud.AddonHudEditorScreen
import com.feb.mod.ui.hud.AddonHudManager
import com.feb.mod.ui.hud.HudBuilder
import net.minecraft.client.Minecraft

class HudAPI(private val ownerId: String) {

    fun register(
        id: String,
        displayName: String = id,
        builder: HudBuilder.() -> Unit
    ) {
        require(id.isNotBlank()) { "HUD ID cannot be blank" }

        val hudBuilder = HudBuilder()
        hudBuilder.builder()

        AddonHudManager.register(
            ownerId,
            id,
            displayName,
            hudBuilder.build()
        )
    }

    fun unregister(id: String) {
        AddonHudManager.unregister("$ownerId:$id")
    }

    fun openEditor() {
        Minecraft.getInstance().execute {
            Minecraft.getInstance().gui.setScreen(
                AddonHudEditorScreen(ownerId)
            )
        }
    }

    fun cleanup() {
        AddonHudManager.unregisterOwner(ownerId)
    }
}
