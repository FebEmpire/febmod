package com.feb.mod.ui.hud

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.resources.Identifier

object FebModHud {

    var enabled = true

    private val HUD_ID = Identifier.fromNamespaceAndPath(
        "febmod",
        "hud"
    )

    fun register() {
        HudManager.registerDefaults()

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
        val entries = HudManager.getEntries()

        if (entries.isEmpty()) {
            return
        }

        val padding = 8
        val spacing = 2
        val textWidth = entries.maxOf { font.width(it.getText()) }
        val boxWidth = textWidth + padding * 2
        val boxHeight = entries.size * (font.lineHeight + spacing) + padding * 2

        val boxX = client.window.guiScaledWidth - boxWidth - 10
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

        entries.forEachIndexed { index, entry ->
            graphics.text(
                font,
                entry.getText(),
                boxX + padding,
                boxY + padding + index * (font.lineHeight + spacing),
                0xFF000000.toInt(),
                false
            )
        }
    }
}