package com.feb.mod.addon

import com.feb.mod.ui.gui.FebModGui
import com.feb.mod.ui.gui.tabs.FebTab

interface FebAddon {
    val name: String
    val version: String

    fun initialize(context: AddonContext)
    fun createTab(screen: FebModGui): FebTab
}