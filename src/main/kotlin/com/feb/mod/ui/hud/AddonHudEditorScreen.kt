package com.feb.mod.ui.hud

import com.feb.mod.manager.ConfigManager
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.network.chat.Component
import kotlin.math.roundToInt

class AddonHudEditorScreen(
    private val ownerId: String
) : Screen(Component.literal("Addon HUD Editor")) {

    private var dragging = false
    private var dragOffsetX = 0
    private var dragOffsetY = 0
    private var selectedId: String? = null

    override fun isPauseScreen(): Boolean = false

    override fun init() {
        super.init()
        FebModHud.editorOpen = true
        selectedId = AddonHudManager.getForOwner(ownerId).firstOrNull()?.fullId
    }

    override fun extractRenderState(
        graphics: GuiGraphicsExtractor,
        mouseX: Int,
        mouseY: Int,
        partialTick: Float
    ) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick)
        graphics.fill(0, 0, width, height, 0x80000000.toInt())

        val huds = AddonHudManager.getForOwner(ownerId)

        if (huds.isEmpty()) {
            val message = "This addon has no registered HUDs"
            graphics.text(
                font,
                message,
                (width - font.width(message)) / 2,
                height / 2,
                0xFFFFFFFF.toInt(),
                true
            )
        }

        huds.forEach { hud ->
            val config = AddonHudManager.configFor(hud)
            val scale = config.scale.coerceIn(0.5f, 2.0f)
            val (hudWidth, hudHeight) =
                AddonHudRenderer.getScaledHudSize(hud, scale)

            AddonHudRenderer.renderScaledAt(
                graphics, hud, config.x, config.y, scale
            )

            val selected = hud.fullId == selectedId
            val color = if (selected) {
                0xFFFFFFFF.toInt()
            } else {
                0xFFAAAAAA.toInt()
            }

            graphics.outline(
                config.x - 2,
                config.y - 2,
                hudWidth + 4,
                hudHeight + 4,
                color
            )

            graphics.text(
                font,
                hud.displayName,
                config.x,
                (config.y - 12).coerceAtLeast(4),
                color,
                true
            )
        }

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

        val mouseX = event.x().toInt()
        val mouseY = event.y().toInt()

        val hud = AddonHudManager.getForOwner(ownerId)
            .asReversed()
            .firstOrNull { candidate ->
                val config = AddonHudManager.configFor(candidate)
                val scale = config.scale.coerceIn(0.5f, 2.0f)
                val (w, h) =
                    AddonHudRenderer.getScaledHudSize(candidate, scale)

                mouseX >= config.x && mouseX <= config.x + w &&
                        mouseY >= config.y && mouseY <= config.y + h
            } ?: return super.mouseClicked(event, doubleClick)

        val config = AddonHudManager.configFor(hud)
        selectedId = hud.fullId
        dragging = true
        dragOffsetX = mouseX - config.x
        dragOffsetY = mouseY - config.y

        return true
    }

    override fun mouseDragged(
        event: MouseButtonEvent,
        deltaX: Double,
        deltaY: Double
    ): Boolean {
        if (!dragging || event.button() != 0) {
            return super.mouseDragged(event, deltaX, deltaY)
        }

        val hud = AddonHudManager.getAll()
            .firstOrNull { it.fullId == selectedId }
            ?: return false

        val config = AddonHudManager.configFor(hud)
        val (w, h) = AddonHudRenderer.getScaledHudSize(
            hud,
            config.scale.coerceIn(0.5f, 2.0f)
        )

        config.x = (event.x().toInt() - dragOffsetX)
            .coerceIn(0, (width - w).coerceAtLeast(0))

        config.y = (event.y().toInt() - dragOffsetY)
            .coerceIn(0, (height - h).coerceAtLeast(0))

        return true
    }

    override fun mouseScrolled(
        mouseX: Double,
        mouseY: Double,
        scrollX: Double,
        scrollY: Double
    ): Boolean {
        if (scrollY == 0.0) {
            return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY)
        }

        val hud = AddonHudManager.getForOwner(ownerId)
            .asReversed()
            .firstOrNull { candidate ->
                val config = AddonHudManager.configFor(candidate)
                val (w, h) = AddonHudRenderer.getScaledHudSize(
                    candidate,
                    config.scale.coerceIn(0.5f, 2.0f)
                )

                mouseX >= config.x && mouseX <= config.x + w &&
                        mouseY >= config.y && mouseY <= config.y + h
            } ?: return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY)

        val config = AddonHudManager.configFor(hud)
        val change = if (scrollY > 0.0) 0.1f else -0.1f

        config.scale = ((config.scale.coerceIn(0.5f, 2.0f) + change) * 10f)
            .roundToInt()
            .coerceIn(5, 20) / 10f

        selectedId = hud.fullId
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
