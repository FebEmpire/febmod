package com.feb.mod.manager

import com.feb.mod.api.command.CommandApi
import com.feb.mod.command.*

object FebModManager {

    private val commands = CommandApi("febmod")

    fun init() {
        ConfigManager.load()

        FebCommand.register(commands)
        CoinFlipCommand.register(commands)
        PingCommand.register(commands)
        DiceCommand.register(commands)

        AddonManager.loadAddons()
    }
}