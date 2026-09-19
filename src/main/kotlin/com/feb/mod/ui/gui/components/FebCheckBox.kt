package com.feb.mod.ui.gui.components

import com.feb.mod.ui.gui.FebModGui
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.components.AbstractButton
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.client.input.InputWithModifiers
import net.minecraft.network.chat.Component

class FebCheckBox(
    x: Int,
    y: Int,
    size: Int,
    checked: Boolean = false,
    private val onChanged: (Boolean) -> Unit
) : AbstractButton(x, y, size, size, Component.empty()) {

    var checked = checked
        private set

    override fun extractContents(
        graphics: GuiGraphicsExtractor,
        mouseX: Int,
        mouseY: Int,
        delta: Float
    ) {
        val theme = FebModGui.activeGuiTheme
        val hovered = isHovered

        val background = when {
            checked -> if (hovered) brighten(theme.buttonActive) else theme.buttonActive
            hovered -> theme.buttonHover
            else -> theme.buttonIdle
        }

        val border = when {
            checked -> theme.buttonActiveBorder
            hovered -> 0xFFFFFFFF.toInt()
            else -> 0x80FFFFFF.toInt()
        }

        graphics.fill(x, y, x + width, y + height, background)
        graphics.fill(x, y, x + width, y + 1, border)
        graphics.fill(x, y + height - 1, x + width, y + height, border)
        graphics.fill(x, y, x + 1, y + height, border)
        graphics.fill(x + width - 1, y, x + width, y + height, border)

        if (hovered) {
            graphics.fill(x + 1, y + 1, x + width - 1, y + 2, 0x40FFFFFF)
        }
    }

    override fun onPress(input: InputWithModifiers) {
        checked = !checked
        onChanged(checked)
    }

    fun setChecked(value: Boolean) {
        checked = value
    }

    private fun brighten(color: Int): Int {
        val a = (color ushr 24) and 0xFF
        val r = ((color ushr 16) and 0xFF).plus(20).coerceAtMost(255)
        val g = ((color ushr 8) and 0xFF).plus(20).coerceAtMost(255)
        val b = (color and 0xFF).plus(20).coerceAtMost(255)
        return (a shl 24) or (r shl 16) or (g shl 8) or b
    }

    override fun updateWidgetNarration(builder: NarrationElementOutput) {
        defaultButtonNarrationText(builder)
    }
}