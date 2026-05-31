package com.feb.mod.ui.gui.tabs

import com.feb.mod.manager.AddonManager
import com.feb.mod.ui.gui.FebModGui
import com.feb.mod.ui.gui.components.FebButton
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.network.chat.Component

class AddonsTab(parent: FebModGui) : BaseTab(parent) {

    private val sidebarButtons = mutableListOf<FebButton>()
    private val addonListButtons = mutableListOf<FebButton>()
    private var selectedIndex = -1

    override fun init() {
        buildSidebar()
        buildAddonList()
    }

    private fun buildSidebar() {
        sidebarButtons.forEach { removeWidget(it) }
        sidebarButtons.clear()

        val btn = FebButton(
            10,
            FebModGui.TOP_BAR_HEIGHT + 10,
            FebModGui.SIDEBAR_WIDTH - 20,
            20,
            Component.literal("Addons"),
            parent.font
        ) {}
        btn.selected = true
        sidebarButtons.add(btn)
        addWidget(btn)
    }

    private fun buildAddonList() {
        addonListButtons.forEach { removeWidget(it) }
        addonListButtons.clear()
        parent.closeAddonTab()
        selectedIndex = -1

        AddonManager.getAddons().forEachIndexed { index, addon ->
            val btn = FebButton(
                FebModGui.SIDEBAR_WIDTH + 10,
                FebModGui.TOP_BAR_HEIGHT + 10 + index * 26,
                FebModGui.ADDON_LIST_WIDTH - 20,
                20,
                Component.literal(addon.name),
                parent.font
            ) {
                addonListButtons.forEach { it.selected = false }
                addonListButtons.getOrNull(index)?.selected = true
                selectedIndex = index
                val tab = addon.createTab(parent)
                if (tab != null) parent.openAddonTab(tab) else parent.closeAddonTab()
            }
            btn.selected = index == selectedIndex
            addonListButtons.add(btn)
            addWidget(btn)
        }
    }

    override fun render(graphics: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, delta: Float) {
        if (AddonManager.getAddons().isEmpty()) {
            graphics.text(
                parent.font,
                "You ain't got no addons installed at the moment or sum idk",
                FebModGui.SIDEBAR_WIDTH + 10,
                FebModGui.TOP_BAR_HEIGHT + 10,
                0xFFAAAAAA.toInt(),
                false
            )
        }
    }

    override fun clear() {
        super.clear()
        sidebarButtons.clear()
        addonListButtons.clear()
        parent.closeAddonTab()
    }
}