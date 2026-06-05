package com.feb.mod.ui.gui

import com.feb.mod.ui.gui.components.FebButton
import com.feb.mod.ui.gui.particles.ParticleManager
import com.feb.mod.ui.gui.tabs.AddonsTab
import com.feb.mod.ui.gui.tabs.FebModTab
import com.feb.mod.ui.gui.tabs.FebTab
import com.feb.mod.ui.gui.subscreens.AbstractSubScreen
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import com.feb.mod.ModInfo
import com.feb.mod.manager.ConfigManager
import com.feb.mod.ui.gui.theme.GuiTheme
import com.feb.mod.ui.gui.theme.GuiThemes

class FebModGui(initialTab: TopTab = TopTab.FEBMOD) : Screen(Component.literal("${ModInfo.MOD_NAME} ${ModInfo.VERSION}")) {

    enum class TopTab(val displayName: String) {
        FEBMOD("FebMod"),
        ADDONS("Addons")
    }

    private var selectedTopTab: TopTab = initialTab
    private val topTabButtons = mutableListOf<FebButton>()
    private val particles = ParticleManager()
    private val logoTexture = Identifier.fromNamespaceAndPath("febmod", "textures/gui/feb_penguin.png")

    private val febModTab = FebModTab(this)
    private val addonsTab = AddonsTab(this)

    private var activeAddonTab: FebTab? = null
    var currentSubScreen: AbstractSubScreen? = null

    var activeTheme: ParticleManager.Style = ParticleManager.Style.WINTER
    var currentGuiTheme: GuiTheme = GuiThemes.WINTER
        private set

    companion object {
        const val SIDEBAR_WIDTH = 120
        const val ADDON_LIST_WIDTH = 180
        const val TOP_BAR_HEIGHT = 36
        const val NAV_BUTTON_WIDTH = 80
        const val NAV_BUTTON_HEIGHT = 20
        const val NAV_BUTTON_SPACING = 6
        val CONTENT_X_ADDONS get() = SIDEBAR_WIDTH + ADDON_LIST_WIDTH + 1
        val CONTENT_X_FEBMOD get() = SIDEBAR_WIDTH + 1
        var activeGuiTheme: GuiTheme = GuiThemes.WINTER
    }

    override fun init() {
        super.init()
        activeTheme = ConfigManager.current.theme
            .let { runCatching { ParticleManager.Style.valueOf(it) }.getOrDefault(ParticleManager.Style.WINTER) }
        currentGuiTheme = GuiThemes.fromStyle(activeTheme)
        activeGuiTheme = currentGuiTheme
        particles.initialize(width, height, activeTheme)
        buildTopNav()
        if (currentSubScreen != null) currentSubScreen?.init()
        else getCurrentTab().init()
    }

    fun applyTheme(style: ParticleManager.Style) {
        activeTheme = style
        currentGuiTheme = GuiThemes.fromStyle(style)
        activeGuiTheme = currentGuiTheme
        particles.setStyle(style, width, height)
        ConfigManager.current.theme = style.name
        ConfigManager.save()
    }

    private fun buildTopNav() {
        topTabButtons.forEach { removeWidget(it) }
        topTabButtons.clear()
        val navY = (TOP_BAR_HEIGHT - NAV_BUTTON_HEIGHT) / 2
        val navStartX = SIDEBAR_WIDTH + 10

        TopTab.entries.forEachIndexed { index, tab ->
            val btn = FebButton(
                navStartX + index * (NAV_BUTTON_WIDTH + NAV_BUTTON_SPACING),
                navY,
                NAV_BUTTON_WIDTH,
                NAV_BUTTON_HEIGHT,
                Component.literal(tab.displayName),
                font
            ) { selectTopTab(tab) }
            btn.selected = tab == selectedTopTab
            topTabButtons.add(btn)
            addRenderableWidget(btn)
        }
    }

    private fun selectTopTab(tab: TopTab) {
        if (currentSubScreen != null) {
            currentSubScreen?.handleClose()
            currentSubScreen = null
        }
        getCurrentTab().clear()
        closeAddonTab()
        selectedTopTab = tab
        topTabButtons.forEach { it.selected = false }
        topTabButtons.getOrNull(TopTab.entries.indexOf(tab))?.selected = true
        getCurrentTab().init()
    }

    private fun getCurrentTab() = when (selectedTopTab) {
        TopTab.FEBMOD -> febModTab
        TopTab.ADDONS -> addonsTab
    }

    fun openSubScreen(subScreen: AbstractSubScreen) {
        currentSubScreen?.handleClose()
        getCurrentTab().clear()
        closeAddonTab()
        currentSubScreen = subScreen
        currentSubScreen?.init()
    }

    fun closeSubScreen() {
        currentSubScreen?.handleClose()
        currentSubScreen = null
        getCurrentTab().clear()
        getCurrentTab().init()
    }

    fun openAddonTab(tab: FebTab) {
        activeAddonTab?.clear()
        activeAddonTab = tab
        activeAddonTab?.init(this)
    }

    fun closeAddonTab() {
        activeAddonTab?.clear()
        activeAddonTab = null
    }

    override fun extractRenderState(graphics: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, delta: Float) {
        graphics.fill(0, 0, width, height, currentGuiTheme.background)
        particles.update(width, height)
        particles.render(graphics)

        graphics.fill(0, TOP_BAR_HEIGHT, width, TOP_BAR_HEIGHT + 1, currentGuiTheme.divider)
        graphics.fill(0, TOP_BAR_HEIGHT + 1, SIDEBAR_WIDTH, height, currentGuiTheme.sidebar)
        graphics.fill(SIDEBAR_WIDTH, 0, SIDEBAR_WIDTH + 1, height, currentGuiTheme.divider)

        if (selectedTopTab == TopTab.ADDONS) {
            graphics.fill(SIDEBAR_WIDTH + 1, TOP_BAR_HEIGHT + 1, SIDEBAR_WIDTH + ADDON_LIST_WIDTH, height, currentGuiTheme.addonPanel)
            graphics.fill(SIDEBAR_WIDTH + ADDON_LIST_WIDTH, TOP_BAR_HEIGHT + 1, SIDEBAR_WIDTH + ADDON_LIST_WIDTH + 1, height, currentGuiTheme.divider)
        }

        super.extractRenderState(graphics, mouseX, mouseY, delta)
        getCurrentTab().render(graphics, mouseX, mouseY, delta)
        activeAddonTab?.render(graphics, mouseX, mouseY, delta)
        currentSubScreen?.render(graphics, mouseX, mouseY, delta)
        renderLogoAndTitle(graphics)
    }

    private fun renderLogoAndTitle(graphics: GuiGraphicsExtractor) {
        val logoSize = 16
        val logoY = (TOP_BAR_HEIGHT - logoSize) / 2
        val textY = logoY + (logoSize / 2) - (font.lineHeight / 2)
        graphics.text(font, title, 10 + (SIDEBAR_WIDTH - 20 - font.width(title)) / 2, textY, 0xFFFFFFFF.toInt(), true)
    }

    override fun isPauseScreen() = false

    fun addWidget(widget: AbstractWidget) { addRenderableWidget(widget) }
    fun removeWidget(widget: AbstractWidget) { super.removeWidget(widget) }
}