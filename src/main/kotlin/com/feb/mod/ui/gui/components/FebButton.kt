package com.feb.mod.ui.gui.components

import com.feb.mod.ui.gui.FebModGui
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.components.AbstractButton
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.network.chat.Component
class FebButton(
    x: Int,
    y: Int,
    width: Int,
    height: Int,
    message: Component,
    private val font: Font,
    private val onRightClick: (() -> Unit)? = null,
    private val onPress: () -> Unit
) : AbstractButton(x, y, width, height, message) {

    var selected = false
    var toggled: Boolean? = null

    override fun extractContents(graphics: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, delta: Float) {
        val theme = FebModGui.activeGuiTheme
        val hovered = isHovered
        val bg = when {
            toggled == true  -> if (hovered) brighten(theme.buttonActive) else theme.buttonActive
            toggled == false -> if (hovered) theme.buttonHover else theme.buttonIdle
            selected         -> if (hovered) brighten(theme.buttonActive) else theme.buttonActive
            else             -> if (hovered) theme.buttonHover else theme.buttonIdle
        }
        val border = when {
            toggled == true -> theme.buttonActiveBorder
            selected        -> theme.buttonActiveBorder
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

    private fun brighten(color: Int): Int {
        val a = (color ushr 24) and 0xFF
        val r = ((color ushr 16) and 0xFF).plus(20).coerceAtMost(255)
        val g = ((color ushr 8) and 0xFF).plus(20).coerceAtMost(255)
        val b = (color and 0xFF).plus(20).coerceAtMost(255)
        return (a shl 24) or (r shl 16) or (g shl 8) or b
    }

    override fun mouseClicked(event: MouseButtonEvent, doubleClick: Boolean): Boolean {
        if (isHovered && event.button() == 1) {
            onRightClick?.invoke()
            return onRightClick != null
        }
        return super.mouseClicked(event, doubleClick)
    }

    override fun onPress(input: net.minecraft.client.input.InputWithModifiers) = onPress.invoke()
    override fun updateWidgetNarration(builder: NarrationElementOutput) = defaultButtonNarrationText(builder)
}