package com.feb.mod.manager

import com.feb.mod.command.*
import com.feb.mod.command.FebCommand
import com.feb.mod.command.PingCommand

object CommandManager {

    fun initialize() {
        registerCommands()
        DotCommands.initialize()
    }

    private fun registerCommands() {
        FebCommand.register()
        PingCommand.register()
        CoinFlipCommand.register()
    }
}
