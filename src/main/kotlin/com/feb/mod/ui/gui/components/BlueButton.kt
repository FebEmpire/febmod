package com.feb.mod.ui.gui.components

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.components.Button
import net.minecraft.network.chat.Component

class BlueButton private constructor(
    x: Int, y: Int, w: Int, h: Int,
    text: Component,
    onPress: OnPress
) : Button(x, y, w, h, text, onPress, DEFAULT_NARRATION) {

    companion object {
        @JvmStatic
        fun of(x: Int, y: Int, w: Int, h: Int, text: String, onPress: OnPress): BlueButton {
            return BlueButton(x, y, w, h, Component.literal(text), onPress)
        }
    }

    override fun extractContents(graphics: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, delta: Float) {
        val hovered = isHovered
        val bg = if (hovered) 0xFF2288dd.toInt() else 0xFF0d3a5c.toInt()
        val border = if (hovered) 0xFF66ccff.toInt() else 0xFF1a5a88.toInt()

        graphics.fill(x, y, x + width, y + height, border)
        graphics.fill(x + 1, y + 1, x + width - 1, y + height - 1, bg)

        val textWidth = Minecraft.getInstance().font.width(message)
        graphics.text(
            Minecraft.getInstance().font, message.string,
            x + (width - textWidth) / 2, y + (height - 9) / 2,
            0xFFFFFFFF.toInt(), false
        )
    }
}