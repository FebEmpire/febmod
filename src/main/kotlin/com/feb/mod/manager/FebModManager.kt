package com.feb.mod.manager

import com.feb.mod.manager.*

object FebModManager {

    fun init() {
        CommandManager.initialize()
        ConfigManager.load()
        AddonManager.loadAddons()
    }

}