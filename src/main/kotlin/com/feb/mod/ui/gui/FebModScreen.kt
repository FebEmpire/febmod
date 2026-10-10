package com.feb.mod.ui.gui

import com.feb.mod.ui.gui.components.BlueButton
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.screens.ConfirmLinkScreen
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component

class FebModScreen(private val parent: Screen) : Screen(Component.literal("FebMod")) {

    override fun init() {
        val linksY = height / 2 - 20
        val btnWidth = 120
        val spacing = 30

        addRenderableWidget(
            BlueButton.of(width / 2 - btnWidth / 2, linksY, btnWidth, 20, "Discord") {
                ConfirmLinkScreen.confirmLinkNow(this, "https://discord.gg/Q9kSukTTZT", true)
            }
        )
        addRenderableWidget(
            BlueButton.of(width / 2 - btnWidth / 2, linksY + spacing, btnWidth, 20, "GitHub") {
                ConfirmLinkScreen.confirmLinkNow(this, "https://github.com/FebEmpire/febmod", true)
            }
        )
        addRenderableWidget(
            BlueButton.of(width / 2 - btnWidth / 2, linksY + spacing * 2, btnWidth, 20, "Website") {
                ConfirmLinkScreen.confirmLinkNow(this, "https://febempire.github.io/febempire-website/pages/febmod/febmod.html", true)
            }
        )

        addRenderableWidget(
            BlueButton.of(width / 2 - 100, height - 40, 200, 20, "Back") {
                minecraft.setScreen(parent)
            }
        )
    }

    override fun extractRenderState(graphics: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, delta: Float) {
        graphics.fillGradient(0, 0, width, height, 0xFF001a33.toInt(), 0xFF00121f.toInt())

        super.extractRenderState(graphics, mouseX, mouseY, delta)

        val title = "FebMod"
        graphics.text(font, title, (width - font.width(title)) / 2, 40, 0xFF66ccff.toInt(), true)

        val subtitle = "Links"
        graphics.text(font, subtitle, (width - font.width(subtitle)) / 2, height / 2 - 34, 0xFF66ccff.toInt(), false)
    }
}