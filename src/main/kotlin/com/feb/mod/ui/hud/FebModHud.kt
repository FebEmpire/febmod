package com.feb.mod.ui.hud

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.resources.Identifier

object FebModHud {

    var enabled = true

    private val HUD_ID =
        Identifier.fromNamespaceAndPath(
            "febmod",
            "hud"
        )

    fun register() {
        HudElementRegistry.addLast(HUD_ID) { graphics, _ ->
            render(graphics)
        }
    }

    private fun render(graphics: GuiGraphicsExtractor) {
        if (!enabled) {
            return
        }

        val client = Minecraft.getInstance()
        val font = client.font

        val screenWidth = client.window.guiScaledWidth

        val text = "FebMod HUD"
        val padding = 8

        val boxWidth = font.width(text) + padding * 2
        val boxHeight = 20

        val boxX = screenWidth - boxWidth - 10
        val boxY = 10

        graphics.fill(
            boxX - 1,
            boxY - 1,
            boxX + boxWidth + 1,
            boxY + boxHeight + 1,
            0xFF000000.toInt()
        )

        graphics.fill(
            boxX,
            boxY,
            boxX + boxWidth,
            boxY + boxHeight,
            0xFF000FEB.toInt()
        )

        graphics.text(
            font,
            text,
            boxX + padding,
            boxY + 6,
            0xFF000000.toInt(),
            false
        )
    }
}