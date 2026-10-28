package com.feb.mod.ui.hud

import com.feb.mod.manager.ConfigManager
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.network.chat.Component
import kotlin.math.roundToInt

class HudEditorScreen : Screen(Component.literal("FebMod HUD Editor")) {

    private data class HudBounds(
        val x: Int,
        val y: Int,
        val width: Int,
        val height: Int
    )

    private var dragging = false
    private var draggingHudId: String? = null
    private var selectedHudId = BUILTIN_HUD_ID
    private var dragOffsetX = 0
    private var dragOffsetY = 0

    override fun isPauseScreen(): Boolean = false

    override fun init() {
        super.init()
        FebModHud.editorOpen = true
    }

    private fun builtinBounds(): HudBounds {
        val config = ConfigManager.current.hud
        val scale = config.scale.coerceIn(0.5f, 2.0f)
        val (w, h) = FebModHud.getScaledHudSize(scale)
        val x = if (config.x < 0) width - w - 10 else config.x

        return HudBounds(x, config.y, w, h)
    }

    private fun addonBounds(hud: AddonHud): HudBounds {
        val config = AddonHudManager.configFor(hud)
        val scale = config.scale.coerceIn(0.5f, 2.0f)
        val (w, h) = AddonHudRenderer.getScaledHudSize(hud, scale)

        return HudBounds(config.x, config.y, w, h)
    }

    private fun contains(bounds: HudBounds, mouseX: Double, mouseY: Double): Boolean {
        return mouseX >= bounds.x && mouseX <= bounds.x + bounds.width &&
                mouseY >= bounds.y && mouseY <= bounds.y + bounds.height
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
        val builtin = builtinBounds()

        FebModHud.renderScaledAt(graphics, builtin.x, builtin.y, scale)

        graphics.outline(
            builtin.x - 2,
            builtin.y - 2,
            builtin.width + 4,
            builtin.height + 4,
            if (selectedHudId == BUILTIN_HUD_ID) {
                0xFFFFFFFF.toInt()
            } else {
                0xFFAAAAAA.toInt()
            }
        )

        val builtinLabel = "FebMod HUD - ${"%.1f".format(java.util.Locale.ROOT, scale)}x"
        graphics.text(
            font,
            builtinLabel,
            builtin.x,
            (builtin.y - 14).coerceAtLeast(4),
            0xFFFFFFFF.toInt(),
            true
        )

        AddonHudManager.getAll().forEach { hud ->
            val addonConfig = AddonHudManager.configFor(hud)
            val addonScale = addonConfig.scale.coerceIn(0.5f, 2.0f)
            val bounds = addonBounds(hud)

            AddonHudRenderer.renderScaledAt(
                graphics,
                hud,
                bounds.x,
                bounds.y,
                addonScale
            )

            val selected = selectedHudId == hud.fullId

            graphics.outline(
                bounds.x - 2,
                bounds.y - 2,
                bounds.width + 4,
                bounds.height + 4,
                if (selected) 0xFFFFFFFF.toInt() else 0xFFAAAAAA.toInt()
            )

            val label = "${hud.displayName} - ${
                "%.1f".format(java.util.Locale.ROOT, addonScale)
            }x"

            graphics.text(
                font,
                label,
                bounds.x,
                (bounds.y - 14).coerceAtLeast(4),
                0xFFFFFFFF.toInt(),
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

        val addon = AddonHudManager.getAll()
            .asReversed()
            .firstOrNull { contains(addonBounds(it), mouseX.toDouble(), mouseY.toDouble()) }

        if (addon != null) {
            val bounds = addonBounds(addon)

            selectedHudId = addon.fullId
            draggingHudId = addon.fullId
            dragging = true
            dragOffsetX = mouseX - bounds.x
            dragOffsetY = mouseY - bounds.y

            val addonConfig = AddonHudManager.configFor(addon)
            addonConfig.x = bounds.x
            addonConfig.y = bounds.y

            return true
        }

        val builtin = builtinBounds()

        if (contains(builtin, mouseX.toDouble(), mouseY.toDouble())) {
            selectedHudId = BUILTIN_HUD_ID
            draggingHudId = null
            dragging = true
            dragOffsetX = mouseX - builtin.x
            dragOffsetY = mouseY - builtin.y

            val config = ConfigManager.current.hud
            config.x = builtin.x
            config.y = builtin.y

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

        val addonId = draggingHudId

        if (addonId != null) {
            val hud = AddonHudManager.getAll()
                .firstOrNull { it.fullId == addonId }
                ?: return false

            val addonConfig = AddonHudManager.configFor(hud)
            val bounds = addonBounds(hud)

            addonConfig.x = (event.x().toInt() - dragOffsetX)
                .coerceIn(0, (width - bounds.width).coerceAtLeast(0))

            addonConfig.y = (event.y().toInt() - dragOffsetY)
                .coerceIn(0, (height - bounds.height).coerceAtLeast(0))
        } else {
            val config = ConfigManager.current.hud
            val bounds = builtinBounds()

            config.x = (event.x().toInt() - dragOffsetX)
                .coerceIn(0, (width - bounds.width).coerceAtLeast(0))

            config.y = (event.y().toInt() - dragOffsetY)
                .coerceIn(0, (height - bounds.height).coerceAtLeast(0))
        }

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

        val addon = AddonHudManager.getAll()
            .asReversed()
            .firstOrNull { contains(addonBounds(it), mouseX, mouseY) }

        val change = if (scrollY > 0.0) 0.1f else -0.1f

        if (addon != null) {
            val addonConfig = AddonHudManager.configFor(addon)

            addonConfig.scale = (
                    (addonConfig.scale.coerceIn(0.5f, 2.0f) + change) * 10f
                    ).roundToInt().coerceIn(5, 20) / 10f

            selectedHudId = addon.fullId
            ConfigManager.save()

            return true
        }

        val builtin = builtinBounds()

        if (contains(builtin, mouseX, mouseY)) {
            val config = ConfigManager.current.hud
            val scale = config.scale.coerceIn(0.5f, 2.0f)

            config.scale = ((scale + change) * 10f)
                .roundToInt()
                .coerceIn(5, 20) / 10f

            selectedHudId = BUILTIN_HUD_ID
            ConfigManager.save()

            return true
        }

        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY)
    }

    override fun mouseReleased(event: MouseButtonEvent): Boolean {
        if (event.button() == 0 && dragging) {
            dragging = false
            draggingHudId = null
            ConfigManager.save()
            return true
        }

        return super.mouseReleased(event)
    }

    override fun onClose() {
        dragging = false
        draggingHudId = null
        ConfigManager.save()
        FebModHud.editorOpen = false
        super.onClose()
    }

    override fun removed() {
        FebModHud.editorOpen = false
        super.removed()
    }

    companion object {
        private const val BUILTIN_HUD_ID = "__febmod_builtin__"
    }
}
