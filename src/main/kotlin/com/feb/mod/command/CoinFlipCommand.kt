package com.feb.mod.command

import com.feb.mod.api.chat.ModMessage
import com.feb.mod.api.command.CommandApi

object CoinFlipCommand {

    fun register(commands: CommandApi) {
        commands.register("coin") {
            execute()
        }
    }

    private fun execute() {
        val result = listOf("Heads", "Tails").random()

        ModMessage.send("Landed on $result")
    }
}