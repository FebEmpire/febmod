package com.feb.mod.api.command

import com.feb.mod.api.chat.ModMessage
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents
import org.slf4j.LoggerFactory

class CommandApi(
    private val owner: String
) {

    companion object {
        private val logger = LoggerFactory.getLogger("febmod/commands")
        private val commands = mutableMapOf<String, RegisteredCommand>()
        private var initialized = false

        private fun initialize() {
            if (initialized) return
            initialized = true

            ClientSendMessageEvents.ALLOW_CHAT.register { message ->
                if (!message.startsWith(".")) {
                    return@register true
                }

                handle(message)
                false
            }
        }

        @JvmStatic
        fun getAllCommands(): Set<String> =
            commands.keys

        private fun handle(message: String) {
            val input = message
                .substring(1)
                .trim()

            if (input.isBlank()) {
                return
            }

            val parts = input.split(Regex("\\s+"))
            val commandName = parts[0].lowercase()
            val args = parts.drop(1)

            val registered = commands[commandName] ?: return

            runCatching {
                registered.command.handler(
                    CommandContext(
                        command = commandName,
                        args = args
                    )
                )
            }.onFailure {
                logger.error(
                    "Command '$commandName' failed for owner '${registered.owner}'",
                    it
                )

                ModMessage.error("An error occurred while running .$commandName")
            }
        }

        fun clear(owner: String) {
            commands.entries
                .filter { it.value.owner == owner }
                .map { it.key }
                .toList()
                .forEach(commands::remove)
        }

        fun getCommands(owner: String): List<Command> =
            commands.values
                .filter { it.owner == owner }
                .map { it.command }
                .distinct()
    }

    init {
        require(owner.isNotBlank())
        initialize()
    }

    fun register(
        vararg names: String,
        handler: (CommandContext) -> Unit
    ): Command {
        require(names.isNotEmpty())

        val normalized = names
            .map { it.trim().lowercase() }
            .toSet()

        require(normalized.none { it.isBlank() })

        normalized.forEach { name ->
            val existing = commands[name]

            if (existing != null && existing.owner != owner) {
                throw IllegalArgumentException(
                    "Command '$name' is already registered by owner '${existing.owner}'"
                )
            }
        }

        val command = Command(
            names = normalized,
            handler = handler
        )

        normalized.forEach { name ->
            commands[name] = RegisteredCommand(
                owner = owner,
                command = command
            )
        }

        return command
    }

    fun unregister(name: String): Boolean {
        val key = name.trim().lowercase()
        val registered = commands[key] ?: return false

        if (registered.owner != owner) {
            return false
        }

        commands.remove(key)
        return true
    }

    fun clear() {
        clear(owner)
    }

    fun getCommands(): List<Command> =
        getCommands(owner)

    private data class RegisteredCommand(
        val owner: String,
        val command: Command
    )
}