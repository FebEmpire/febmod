package com.feb.mod.command

import net.minecraft.client.Minecraft
import com.feb.mod.api.chat.ModMessage

object PingCommand {

    fun report() {
        val mc = Minecraft.getInstance()
        val player = mc.player ?: return

        val ping = mc.connection
            ?.getPlayerInfo(player.uuid)
            ?.latency ?: -1

        ModMessage.send("Your ping is " + ping + "ms")
    }
}