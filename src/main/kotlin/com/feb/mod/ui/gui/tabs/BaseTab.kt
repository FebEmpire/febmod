package com.feb.mod.ui.gui.tabs

import com.feb.mod.ui.gui.FebModGui
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.components.AbstractWidget

abstract class BaseTab(protected val parent: FebModGui) {
    protected val widgets = mutableListOf<AbstractWidget>()

    abstract fun init()
    abstract fun render(graphics: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, delta: Float)

    open fun clear() {
        widgets.forEach { parent.removeWidget(it) }
        widgets.clear()
    }

    protected fun addWidget(widget: AbstractWidget) {
        widgets.add(widget)
        parent.addWidget(widget)
    }

    protected fun removeWidget(widget: AbstractWidget) {
        widgets.remove(widget)
        parent.removeWidget(widget)
    }
}