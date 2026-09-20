package com.feb.mod.command

import com.feb.mod.api.chat.ModMessage
import kotlin.random.Random

object CoinFlipCommand {

    fun register() {
        DotCommands.register("coin") {
            execute()
        }
    }

    fun execute() {
        val result = listOf("Heads", "Tails").random()

        ModMessage.send("Landed on $result")
    }
}