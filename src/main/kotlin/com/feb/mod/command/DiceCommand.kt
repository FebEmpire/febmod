package com.feb.mod.command

import com.feb.mod.api.chat.ModMessage
import com.feb.mod.api.command.CommandApi

object DiceCommand {

    fun register(commands: CommandApi) {
        commands.register("dice") {
            execute()
        }
    }

    private fun execute() {
        val result = listOf("1", "2", "3", "4", "5", "6").random()

        ModMessage.send("Landed on $result")
    }

}