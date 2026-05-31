package com.feb.mod.addon

import net.minecraft.client.gui.screens.Screen
import com.feb.mod.ui.gui.FebModGui
import com.feb.mod.ui.gui.tabs.FebTab

interface FebAddon {
    val name: String
    val version: String
    fun initialize()
    fun createSettingsScreen(parent: Screen): Screen? = null
    fun createTab(screen: FebModGui): FebTab? = null
}