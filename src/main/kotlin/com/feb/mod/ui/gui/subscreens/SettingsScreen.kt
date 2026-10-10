package com.feb.mod.ui.gui.subscreens

import com.feb.mod.ui.gui.FebModGui
import com.feb.mod.ui.gui.components.FebButton
import com.feb.mod.ui.gui.components.FebCheckBox
import com.feb.mod.ui.hud.HudManager
import com.feb.mod.ui.hud.HudSettings
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.network.chat.Component

class SettingsScreen(parent: FebModGui) : AbstractSubScreen(parent) {

    private lateinit var hudButton: FebButton
    private val entryCheckboxes = mutableListOf<FebCheckBox>()

    override fun getTitle(): String = "Settings"

    override fun initializeContent() {
        hudButton = FebButton(
            contentX + 10,
            FebModGui.TOP_BAR_HEIGHT + 50,
            160,
            20,
            Component.literal(
                if (HudSettings.isHudEnabled()) "HUD: ON" else "HUD: OFF"
            ),
            parent.font
        ) {
            val enabled = !HudSettings.isHudEnabled()

            HudSettings.setHudEnabled(enabled)

            hudButton.message = Component.literal(
                if (enabled) "HUD: ON" else "HUD: OFF"
            )

            hudButton.selected = enabled
        }

        hudButton.selected = HudSettings.isHudEnabled()
        addWidget(hudButton)

        var y = FebModGui.TOP_BAR_HEIGHT + 82

        HudManager.getAllEntries().forEach { entry ->
            val labelWidth = parent.font.width(entry.displayName)
            val checkboxX = contentX + 10 + labelWidth + 6

            val checkbox = FebCheckBox(
                checkboxX,
                y,
                18,
                HudSettings.isEnabled(entry.id)
            ) { enabled ->
                HudSettings.setEnabled(entry.id, enabled)
            }

            entryCheckboxes.add(checkbox)
            addWidget(checkbox)

            y += 26
        }
    }

    override fun renderContent(
        graphics: GuiGraphicsExtractor,
        mouseX: Int,
        mouseY: Int,
        delta: Float
    ) {
        graphics.text(
            parent.font,
            "HUD",
            contentX + 10,
            FebModGui.TOP_BAR_HEIGHT + 38,
            0xFFFFFFFF.toInt(),
            true
        )

        var y = FebModGui.TOP_BAR_HEIGHT + 87

        HudManager.getAllEntries().forEach { entry ->
            graphics.text(
                parent.font,
                entry.displayName,
                contentX + 10,
                y,
                0xFFFFFFFF.toInt(),
                false
            )

            y += 26
        }
    }

    override fun onClose() {
        entryCheckboxes.clear()
    }
}