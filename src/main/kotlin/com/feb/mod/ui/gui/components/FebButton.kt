package com.feb.mod.ui.gui.components

import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.components.AbstractButton
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.network.chat.Component

/*
* This is a file for a button
* It's a tuff button tho
* +rep @februari10
 */

class FebButton(
    x: Int,
    y: Int,
    width: Int,
    height: Int,
    message: Component,
    private val font: Font,
    private val onPress: () -> Unit
) : AbstractButton(x, y, width, height, message) {

    var selected = false
    var toggled: Boolean? = null

    override fun extractContents(graphics: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, delta: Float) {
        val hovered = isHovered

        val bg = when {
            toggled == true  -> if (hovered) 0xFF3E6A9F.toInt() else 0xFF2E5A8F.toInt()
            toggled == false -> if (hovered) 0xFF2E4A6F.toInt() else 0xFF1E3A5F.toInt()
            selected         -> if (hovered) 0xFF3E6A9F.toInt() else 0xFF2E5A8F.toInt()
            else             -> if (hovered) 0xFF2E4A6F.toInt() else 0xFF1E3A5F.toInt()
        }

        val border = when {
            toggled == true -> 0xFF4E7AAF.toInt()
            selected        -> 0xFF4E7AAF.toInt()
            else            -> 0x80FFFFFF.toInt()
        }

        graphics.fill(x, y, x + width, y + height, bg)
        graphics.fill(x, y, x + width, y + 1, border)
        graphics.fill(x, y + height - 1, x + width, y + height, border)
        graphics.fill(x, y, x + 1, y + height, border)
        graphics.fill(x + width - 1, y, x + width, y + height, border)

        if (hovered) graphics.fill(x + 1, y + 1, x + width - 1, y + 2, 0x40FFFFFF)

        val textColor = when {
            selected || toggled == true -> 0xFFFFFFFF.toInt()
            hovered                     -> 0xFFE0E0E0.toInt()
            else                        -> 0xFFC0C0C0.toInt()
        }

        val label = when (toggled) {
            true  -> Component.literal("${message.string}: ON")
            false -> Component.literal("${message.string}: OFF")
            null  -> message
        }

        val textX = x + width / 2 - font.width(label) / 2
        val textY = y + (height - font.lineHeight) / 2
        graphics.text(font, label, textX, textY, textColor, true)
    }

    override fun onPress(input: net.minecraft.client.input.InputWithModifiers) = onPress.invoke()
    override fun updateWidgetNarration(builder: NarrationElementOutput) = defaultButtonNarrationText(builder)
}