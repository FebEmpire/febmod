package com.feb.mod.ui.hud

import com.feb.mod.manager.ConfigManager
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.resources.Identifier

object FebModHud {

    var enabled = true

    private val HUD_ID = Identifier.fromNamespaceAndPath("febmod", "hud")

    fun register() {
        HudManager.registerDefaults()

        HudElementRegistry.addLast(HUD_ID) { graphics, _ ->
            render(graphics)
        }
    }

    private fun render(graphics: GuiGraphicsExtractor) {
        if (!enabled || !HudSettings.isHudEnabled()) return

        val client = Minecraft.getInstance()
        val config = ConfigManager.current.hud
        val (hudWidth, _) = getHudSize()

        val x = if (config.x < 0) {
            client.window.guiScaledWidth - hudWidth - 10
        } else {
            config.x
        }

        renderAt(graphics, x, config.y)
    }

    internal fun getHudSize(): Pair<Int, Int> {
        val font = Minecraft.getInstance().font
        val entries = HudManager.getEntries()
        val padding = 8
        val spacing = 2

        val textWidth = if (entries.isEmpty()) {
            font.width("FebMod HUD")
        } else {
            entries.maxOf { font.width(it.getText()) }
        }

        val height = if (entries.isEmpty()) {
            20
        } else {
            entries.size * (font.lineHeight + spacing) + padding * 2
        }

        return Pair(textWidth + padding * 2, height)
    }

    internal fun renderAt(
        graphics: GuiGraphicsExtractor,
        x: Int,
        y: Int
    ): Pair<Int, Int> {
        val font = Minecraft.getInstance().font
        val entries = HudManager.getEntries()
        val (boxWidth, boxHeight) = getHudSize()
        val padding = 8
        val spacing = 2

        graphics.fill(
            x - 1, y - 1,
            x + boxWidth + 1, y + boxHeight + 1,
            0xFF000000.toInt()
        )

        graphics.fill(
            x, y,
            x + boxWidth, y + boxHeight,
            0xFF000FEB.toInt()
        )

        if (entries.isEmpty()) {
            graphics.text(
                font,
                "FebMod HUD",
                x + padding,
                y + 6,
                0xFF000000.toInt(),
                false
            )
        } else {
            entries.forEachIndexed { index, entry ->
                graphics.text(
                    font,
                    entry.getText(),
                    x + padding,
                    y + padding + index * (font.lineHeight + spacing),
                    0xFF000000.toInt(),
                    false
                )
            }
        }

        return Pair(boxWidth, boxHeight)
    }
}
