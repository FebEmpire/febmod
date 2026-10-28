package com.feb.mod.ui.hud

import com.feb.mod.manager.ConfigManager
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.resources.Identifier

object AddonHudRenderer {
    private val HUD_ID =
        Identifier.fromNamespaceAndPath("febmod", "addon_huds")

    fun register() {
        HudElementRegistry.addLast(HUD_ID) { graphics, _ ->
            render(graphics)
        }
    }

    fun render(graphics: GuiGraphicsExtractor) {
        if (FebModHud.editorOpen) return

        AddonHudManager.getAll().forEach { hud ->
            val config = AddonHudManager.configFor(hud)

            if (!config.enabled) return@forEach

            renderScaledAt(
                graphics,
                hud,
                config.x,
                config.y,
                config.scale.coerceIn(0.5f, 2.0f)
            )
        }
    }

    fun getHudSize(hud: AddonHud): Pair<Int, Int> {
        val font = Minecraft.getInstance().font
        val lines = hud.getLines()
        val padding = 8
        val spacing = 2
        val content = if (lines.isEmpty()) listOf(hud.displayName) else lines
        val textWidth = content.maxOf { font.width(it) }
        val height = content.size * (font.lineHeight + spacing) + padding * 2

        return Pair(textWidth + padding * 2, height)
    }

    fun getScaledHudSize(
        hud: AddonHud,
        scale: Float
    ): Pair<Int, Int> {
        val (width, height) = getHudSize(hud)
        return Pair((width * scale).toInt(), (height * scale).toInt())
    }

    fun renderScaledAt(
        graphics: GuiGraphicsExtractor,
        hud: AddonHud,
        x: Int,
        y: Int,
        scale: Float
    ) {
        graphics.pose().pushMatrix()
        graphics.pose().translate(x.toFloat(), y.toFloat())
        graphics.pose().scale(scale, scale)
        renderAt(graphics, hud, 0, 0)
        graphics.pose().popMatrix()
    }

    fun renderAt(
        graphics: GuiGraphicsExtractor,
        hud: AddonHud,
        x: Int,
        y: Int
    ) {
        val font = Minecraft.getInstance().font
        val lines = hud.getLines()
        val content = if (lines.isEmpty()) listOf(hud.displayName) else lines
        val (boxWidth, boxHeight) = getHudSize(hud)
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

        content.forEachIndexed { index, line ->
            graphics.text(
                font,
                line,
                x + padding,
                y + padding + index * (font.lineHeight + spacing),
                0xFF000000.toInt(),
                false
            )
        }
    }
}
