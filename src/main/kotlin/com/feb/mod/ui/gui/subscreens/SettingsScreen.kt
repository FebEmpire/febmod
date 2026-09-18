package com.feb.mod.ui.gui.subscreens

import com.feb.mod.ui.gui.FebModGui
import com.feb.mod.ui.gui.components.FebButton
import com.feb.mod.ui.hud.FebModHud
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.network.chat.Component

class SettingsScreen(parent: FebModGui) : AbstractSubScreen(parent) {

    private lateinit var hudButton: FebButton

    override fun getTitle(): String = "Settings"

    override fun initializeContent() {
        hudButton = FebButton(
            contentX + 10,
            FebModGui.TOP_BAR_HEIGHT + 50,
            160,
            20,
            Component.literal(
                if (FebModHud.enabled) "HUD: ON" else "HUD: OFF"
            ),
            parent.font
        ) {
            FebModHud.enabled = !FebModHud.enabled

            hudButton.message = Component.literal(
                if (FebModHud.enabled) "HUD: ON" else "HUD: OFF"
            )

            hudButton.selected = FebModHud.enabled
        }

        hudButton.selected = FebModHud.enabled

        addWidget(hudButton)
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

    }
}