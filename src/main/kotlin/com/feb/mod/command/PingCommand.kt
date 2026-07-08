package com.feb.mod.command

import net.minecraft.client.Minecraft
import com.feb.mod.utils.ChatUtils

object PingCommand {

    fun report() {
        val mc = Minecraft.getInstance()
        val player = mc.player ?: return

        val ping = mc.connection
            ?.getPlayerInfo(player.uuid)
            ?.latency ?: -1

        ChatUtils.modMessage("Your ping is " + ping + "ms")
    }
}