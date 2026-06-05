package com.feb.mod.manager

import com.feb.mod.manager.*

object FebModManager {

    fun init() {

        CommandManager.registerAll()
        ConfigManager.load()
        AddonManager.loadAddons()

    }

}