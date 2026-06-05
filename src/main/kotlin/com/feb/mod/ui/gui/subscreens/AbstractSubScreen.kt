package com.feb.mod.ui.gui.subscreens

import com.feb.mod.ui.gui.FebModGui
import com.feb.mod.ui.gui.components.FebButton
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.network.chat.Component

/*
* Will be used in the future type shi
* Yo what is up youtube
* I made this for like the future yk
* +rep @februari10
*/

abstract class AbstractSubScreen(protected val parent: FebModGui) {

    protected open val contentX: Int get() = FebModGui.CONTENT_X_FEBMOD
    protected val widgets = mutableListOf<AbstractWidget>()
    private var backButton: FebButton? = null
    private var currentY = FebModGui.TOP_BAR_HEIGHT + 44

    protected abstract fun initializeContent()
    abstract fun renderContent(graphics: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, delta: Float)
    protected abstract fun getTitle(): String

    fun init() {
        clear()
        currentY = FebModGui.TOP_BAR_HEIGHT + 44
        createBackButton()
        initializeContent()
    }

    fun render(graphics: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, delta: Float) {
        graphics.text(
            parent.font,
            Component.literal(getTitle()),
            contentX + 10,
            FebModGui.TOP_BAR_HEIGHT + 10,
            0xFF55FFFF.toInt(),
            true
        )
        renderContent(graphics, mouseX, mouseY, delta)
    }

    private fun createBackButton() {
        backButton = FebButton(
            contentX + 10,
            parent.height - 40,
            80,
            24,
            Component.literal("← Back"),
            parent.font
        ) { parent.closeSubScreen() }
        backButton?.let { addWidget(it) }
    }

    protected fun addWidget(widget: AbstractWidget) {
        widgets.add(widget)
        parent.addWidget(widget)
    }

    protected fun removeWidget(widget: AbstractWidget) {
        widgets.remove(widget)
        parent.removeWidget(widget)
    }

    fun clear() {
        widgets.forEach { parent.removeWidget(it) }
        widgets.clear()
        backButton = null
        currentY = FebModGui.TOP_BAR_HEIGHT + 44
    }

    protected open fun onClose() {}

    internal fun handleClose() {
        onClose()
        clear()
    }

    protected fun createToggleSetting(
        subtitle: String,
        getCurrentValue: () -> Boolean,
        onValueChange: (Boolean) -> Unit,
        spacing: Int = 35
    ): Pair<FebButton, FebButton> {
        val buttonWidth = 80
        val buttonHeight = 20
        val buttonX = contentX + 10
        var enableButton: FebButton? = null
        var disableButton: FebButton? = null

        enableButton = FebButton(buttonX, currentY + 20, buttonWidth, buttonHeight, Component.literal("Enable"), parent.font) {
            onValueChange(true)
            updateToggleButtons(enableButton!!, disableButton!!, true)
        }

        disableButton = FebButton(buttonX + buttonWidth + 10, currentY + 20, buttonWidth, buttonHeight, Component.literal("Disable"), parent.font) {
            onValueChange(false)
            updateToggleButtons(enableButton!!, disableButton!!, false)
        }

        addWidget(enableButton)
        addWidget(disableButton)
        updateToggleButtons(enableButton, disableButton, getCurrentValue())
        currentY += spacing
        return Pair(enableButton, disableButton)
    }

    protected fun createCycleSetting(
        subtitle: String,
        options: List<String>,
        getCurrentIndex: () -> Int,
        onValueChange: (Int) -> Unit,
        spacing: Int = 35
    ): List<FebButton> {
        val buttonWidth = 80
        val buttonHeight = 20
        val buttonX = contentX + 10
        val buttons = mutableListOf<FebButton>()

        options.forEachIndexed { index, label ->
            val button = FebButton(buttonX + index * (buttonWidth + 10), currentY + 20, buttonWidth, buttonHeight, Component.literal(label), parent.font) {
                onValueChange(index)
                updateCycleButtons(buttons, index)
            }
            buttons.add(button)
            addWidget(button)
        }

        updateCycleButtons(buttons, getCurrentIndex())
        currentY += spacing
        return buttons
    }

    private fun updateCycleButtons(buttons: List<FebButton>, selectedIndex: Int) {
        buttons.forEachIndexed { index, button ->
            button.selected = index == selectedIndex
        }
    }

    private fun updateToggleButtons(enableButton: FebButton, disableButton: FebButton, isEnabled: Boolean) {
        enableButton.selected = isEnabled
        disableButton.selected = !isEnabled
    }
    // Line 147 is tuff :pray:

    protected fun renderSubtitle(graphics: GuiGraphicsExtractor, subtitle: String, yOffset: Int) {
        graphics.text(
            parent.font,
            Component.literal(subtitle),
            contentX + 10,
            yOffset,
            0xFFFFFFFF.toInt(),
            false
        )
    }

    protected fun getCurrentY(): Int = currentY
    protected fun incrementY(amount: Int = 35) { currentY += amount }
}