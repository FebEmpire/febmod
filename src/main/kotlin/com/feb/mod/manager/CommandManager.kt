package com.feb.mod.manager

import com.feb.mod.command.*

object CommandManager {

    fun registerAll() {
        DotCommands.initialize()
    }

}