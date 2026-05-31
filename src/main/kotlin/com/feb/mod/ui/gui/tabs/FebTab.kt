package com.feb.mod.ui.gui.tabs

import com.feb.mod.ui.gui.FebModGui
import net.minecraft.client.gui.GuiGraphicsExtractor

interface FebTab {
    val displayName: String
    fun init(screen: FebModGui)
    fun render(graphics: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, delta: Float)
    fun clear()
}