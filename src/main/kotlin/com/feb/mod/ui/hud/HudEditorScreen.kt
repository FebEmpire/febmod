package com.feb.mod.ui.hud

import com.feb.mod.manager.ConfigManager
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.network.chat.Component
import kotlin.math.roundToInt

class HudEditorScreen : Screen(Component.literal("FebMod HUD Editor")) {

    private var dragging = false
    private var dragOffsetX = 0
    private var dragOffsetY = 0

    override fun isPauseScreen(): Boolean = false

    override fun init() {
        super.init()
        FebModHud.editorOpen = true
    }

    override fun extractRenderState(
        graphics: GuiGraphicsExtractor,
        mouseX: Int,
        mouseY: Int,
        partialTick: Float
    ) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick)

        graphics.fill(0, 0, width, height, 0x80000000.toInt())

        val config = ConfigManager.current.hud
        val scale = config.scale.coerceIn(0.5f, 2.0f)
        val (hudWidth, hudHeight) = FebModHud.getScaledHudSize(scale)

        val x = if (config.x < 0) width - hudWidth - 10 else config.x
        val y = config.y

        FebModHud.renderScaledAt(graphics, x, y, scale)

        graphics.outline(
            x - 2,
            y - 2,
            hudWidth + 4,
            hudHeight + 4,
            0xFFFFFFFF.toInt()
        )

        val scaleText = "HUD Scale: ${"%.1f".format(java.util.Locale.ROOT, scale)}x"
        val scaleTextX = x + (hudWidth - font.width(scaleText)) / 2
        val scaleTextY = (y - 14).coerceAtLeast(4)

        graphics.text(
            font,
            scaleText,
            scaleTextX,
            scaleTextY,
            0xFFFFFFFF.toInt(),
            true
        )

        val instructions = "Drag to move | Scroll to resize | ESC to save"

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
        val scale = config.scale.coerceIn(0.5f, 2.0f)
        val (hudWidth, hudHeight) = FebModHud.getScaledHudSize(scale)

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
        val (hudWidth, hudHeight) = FebModHud.getScaledHudSize()

        config.x = (event.x().toInt() - dragOffsetX)
            .coerceIn(0, (width - hudWidth).coerceAtLeast(0))

        config.y = (event.y().toInt() - dragOffsetY)
            .coerceIn(0, (height - hudHeight).coerceAtLeast(0))

        return true
    }

    override fun mouseScrolled(
        mouseX: Double,
        mouseY: Double,
        scrollX: Double,
        scrollY: Double
    ): Boolean {
        val config = ConfigManager.current.hud
        val scale = config.scale.coerceIn(0.5f, 2.0f)
        val (hudWidth, hudHeight) = FebModHud.getScaledHudSize(scale)

        val x = if (config.x < 0) width - hudWidth - 10 else config.x
        val y = config.y

        val hoveringHud =
            mouseX >= x && mouseX <= x + hudWidth &&
                    mouseY >= y && mouseY <= y + hudHeight

        if (!hoveringHud || scrollY == 0.0) {
            return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY)
        }

        val change = if (scrollY > 0.0) 0.1f else -0.1f

        config.scale = ((scale + change) * 10f)
            .roundToInt()
            .coerceIn(5, 20) / 10f

        ConfigManager.save()

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
        FebModHud.editorOpen = false
        super.onClose()
    }

    override fun removed() {
        FebModHud.editorOpen = false
        super.removed()
    }
}
