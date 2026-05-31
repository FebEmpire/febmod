package com.feb.mod.command

object CommandRegistry {
    internal val handlers = mutableMapOf<String, (List<String>) -> Unit>()

    fun register(command: String, handler: (List<String>) -> Unit) {
        handlers[command.lowercase()] = handler
    }
}