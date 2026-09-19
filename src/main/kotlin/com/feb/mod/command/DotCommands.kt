package com.feb.mod.command

import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents

object DotCommands {
    private val handlers = mutableMapOf<String, (List<String>) -> Unit>()

    val commands: Set<String> get() = handlers.keys

    fun register(vararg names: String, handler: (List<String>) -> Unit) {
        names.forEach { name ->
            handlers[name.lowercase()] = handler
        }
    }

    fun initialize() {
        ClientSendMessageEvents.ALLOW_CHAT.register { message ->
            if (!message.startsWith(".")) {
                return@register true
            }

            handle(message)
            false
        }
    }

    private fun handle(message: String) {
        val args = message
            .substring(1)
            .trim()
            .split(Regex("\\s+"))

        if (args.isEmpty() || args[0].isBlank()) {
            return
        }

        val command = args[0].lowercase()
        val commandArgs = args.drop(1)

        handlers[command]?.invoke(commandArgs)
    }
}
