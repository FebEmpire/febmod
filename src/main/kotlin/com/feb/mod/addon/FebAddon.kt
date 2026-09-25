package com.feb.mod.addon

import com.feb.mod.ui.gui.FebModGui
import com.feb.mod.ui.gui.tabs.FebTab

interface FebAddon {
    fun initialize(context: AddonContext)
    fun unload() {}
    fun createTab(screen: FebModGui): FebTab
}