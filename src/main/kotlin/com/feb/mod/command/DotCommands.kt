package com.feb.mod.command

import com.feb.mod.ui.gui.FebModGui
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents

object DotCommands {
    private val handlers = mutableMapOf<String, (List<String>) -> Unit>()

    fun register(command: String, handler: (List<String>) -> Unit) {
        handlers[command.lowercase()] = handler
    }

    fun initialize() {
        ClientSendMessageEvents.ALLOW_CHAT.register { message ->
            if (message.startsWith(".")) {
                handle(message)
                return@register false
            }
            true
        }
    }

    private fun handle(message: String) {
        val args = message.substring(1).split(" ")
        val cmd = args[0].lowercase()
        val sub = args.getOrNull(1)?.lowercase()

        when {
            cmd in listOf("f", "feb", "febmod") && sub == "addon" -> {
                val mc = net.minecraft.client.Minecraft.getInstance()
                mc.execute { mc.setScreen(FebModGui(FebModGui.TopTab.ADDONS)) }
            }
            cmd in listOf("f", "feb", "febmod") -> {
                val mc = net.minecraft.client.Minecraft.getInstance()
                mc.execute { mc.setScreen(FebModGui()) }
            }
            else -> handlers[cmd]?.invoke(args.drop(1))
        }
    }
}