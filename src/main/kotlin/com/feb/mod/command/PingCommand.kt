package com.feb.mod.command

import com.feb.mod.api.chat.ModMessage
import com.feb.mod.api.command.CommandApi
import net.minecraft.client.Minecraft

object PingCommand {

    fun register(commands: CommandApi) {
        commands.register("ping") {
            execute()
        }
    }

    private fun execute() {
        val mc = Minecraft.getInstance()
        val player = mc.player ?: return

        val ping = mc.connection
            ?.getPlayerInfo(player.uuid)
            ?.latency ?: -1

        ModMessage.send("Your ping is ${ping}ms")
    }
}