package com.feb.mod.ui.hud

import com.feb.mod.manager.ConfigManager
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.network.chat.Component

class HudEditorScreen : Screen(Component.literal("FebMod HUD Editor")) {

    private var dragging = false
    private var dragOffsetX = 0
    private var dragOffsetY = 0

    override fun isPauseScreen(): Boolean = false

    override fun extractRenderState(
        graphics: GuiGraphicsExtractor,
        mouseX: Int,
        mouseY: Int,
        partialTick: Float
    ) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick)

        graphics.fill(0, 0, width, height, 0x80000000.toInt())

        val config = ConfigManager.current.hud
        val (hudWidth, hudHeight) = FebModHud.getHudSize()

        val x = if (config.x < 0) width - hudWidth - 10 else config.x
        val y = config.y

        FebModHud.renderAt(graphics, x, y)

        graphics.outline(
            x - 2,
            y - 2,
            hudWidth + 4,
            hudHeight + 4,
            0xFFFFFFFF.toInt()
        )

        val instructions = "Drag HUD to move | ESC to save"

        graphics.text(
            font,
            instructions,
            (width - font.width(instructions)) / 2,
            20,
            0xFFFFFFFF.toInt(),
            true
        )
    }

    override fun mouseClicked(
        event: MouseButtonEvent,
        doubleClick: Boolean
    ): Boolean {
        if (event.button() != 0) {
            return super.mouseClicked(event, doubleClick)
        }

        val config = ConfigManager.current.hud
        val (hudWidth, hudHeight) = FebModHud.getHudSize()

        val x = if (config.x < 0) width - hudWidth - 10 else config.x
        val y = config.y
        val mouseX = event.x().toInt()
        val mouseY = event.y().toInt()

        if (
            mouseX >= x && mouseX <= x + hudWidth &&
            mouseY >= y && mouseY <= y + hudHeight
        ) {
            dragging = true
            dragOffsetX = mouseX - x
            dragOffsetY = mouseY - y
            config.x = x
            config.y = y
            return true
        }

        return super.mouseClicked(event, doubleClick)
    }

    override fun mouseDragged(
        event: MouseButtonEvent,
        deltaX: Double,
        deltaY: Double
    ): Boolean {
        if (!dragging || event.button() != 0) {
            return super.mouseDragged(event, deltaX, deltaY)
        }

        val config = ConfigManager.current.hud
        val (hudWidth, hudHeight) = FebModHud.getHudSize()

        config.x = (event.x().toInt() - dragOffsetX)
            .coerceIn(0, (width - hudWidth).coerceAtLeast(0))

        config.y = (event.y().toInt() - dragOffsetY)
            .coerceIn(0, (height - hudHeight).coerceAtLeast(0))

        return true
    }

    override fun mouseReleased(event: MouseButtonEvent): Boolean {
        if (event.button() == 0 && dragging) {
            dragging = false
            ConfigManager.save()
            return true
        }

        return super.mouseReleased(event)
    }

    override fun onClose() {
        dragging = false
        ConfigManager.save()
        super.onClose()
    }
}
