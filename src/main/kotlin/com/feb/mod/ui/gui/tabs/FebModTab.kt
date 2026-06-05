package com.feb.mod.ui.gui.tabs

import com.feb.mod.ui.gui.FebModGui
import com.feb.mod.ui.gui.components.FebButton
import com.feb.mod.ui.gui.particles.ParticleManager
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.network.chat.Component

class FebModTab(parent: FebModGui) : BaseTab(parent) {
    private val sidebarButtons = mutableListOf<FebButton>()
    private val contentButtons = mutableListOf<FebButton>()
    private var selectedSection = "General"
    private val sections = listOf("General", "Themes", "About")

    private val themes = listOf(
        ParticleManager.Style.WINTER,
        ParticleManager.Style.SPRING,
        ParticleManager.Style.SUMMER,
        ParticleManager.Style.AUTUMN
    )

    override fun init() {
        buildSidebar()
        loadSection(selectedSection)
    }

    private fun buildSidebar() {
        sidebarButtons.forEach { removeWidget(it) }
        sidebarButtons.clear()
        sections.forEachIndexed { index, name ->
            val btn = FebButton(
                10,
                FebModGui.TOP_BAR_HEIGHT + 10 + index * 26,
                FebModGui.SIDEBAR_WIDTH - 20,
                20,
                Component.literal(name),
                parent.font
            ) {
                sidebarButtons.forEach { it.selected = false }
                sidebarButtons.getOrNull(index)?.selected = true
                selectedSection = name
                clearContent()
                loadSection(name)
            }
            btn.selected = name == selectedSection
            sidebarButtons.add(btn)
            addWidget(btn)
        }
    }

    private fun clearContent() {
        contentButtons.forEach { removeWidget(it) }
        contentButtons.clear()
    }

    private fun loadSection(section: String) {
        val contentX = FebModGui.CONTENT_X_FEBMOD + 10
        val startY = FebModGui.TOP_BAR_HEIGHT + 50

        when (section) {
            "Themes" -> {
                themes.forEachIndexed { index, style ->
                    val label = style.name.lowercase().replaceFirstChar { it.uppercase() }
                    val btn = FebButton(
                        contentX,
                        startY + index * 26,
                        100,
                        20,
                        Component.literal(label),
                        parent.font
                    ) {
                        parent.applyTheme(style)
                        contentButtons.forEach { it.selected = false }
                        contentButtons.getOrNull(index)?.selected = true
                    }
                    btn.selected = style == parent.activeTheme
                    contentButtons.add(btn)
                    addWidget(btn)
                }
            }
        }
    }

    override fun render(graphics: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, delta: Float) {
        val contentX = FebModGui.CONTENT_X_FEBMOD + 10
        val startY = FebModGui.TOP_BAR_HEIGHT + 10

        graphics.text(parent.font, selectedSection, contentX, startY, 0xFFFFFFFF.toInt(), true)

        when (selectedSection) {
            "General" -> {
                graphics.text(parent.font, "Maybe discord rpc in the future or sum idk", contentX, startY + 20, 0x80FFFFFF.toInt(), false)
            }
            "Themes" -> {
                graphics.text(parent.font, "Pick a theme", contentX, startY + 20, 0x80FFFFFF.toInt(), false)
            }
            "About" -> {
                graphics.text(parent.font, "FebMod by THE Februari10", contentX, startY + 20, 0xFFFFFFFF.toInt(), false)
                graphics.text(parent.font, "Pretty goated mod ngl", contentX, startY + 34, 0x80FFFFFF.toInt(), false)
                graphics.text(parent.font, "Please give me money \uD83D\uDE4F my paypal is febban2010@gmail.com", contentX, startY + 48, 0x80FFFFFF.toInt(), false)
                graphics.text(parent.font, "Join the discord https://discord.gg/Q9kSukTTZT", contentX, startY + 62, 0x80FFFFFF.toInt(), false)
            }
        }
    }

    override fun clear() {
        clearContent()
        super.clear()
        sidebarButtons.clear()
    }
}