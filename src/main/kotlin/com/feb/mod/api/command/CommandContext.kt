package com.feb.mod.api.command

data class CommandContext(
    val command: String,
    val args: List<String>
) {
    fun arg(index: Int): String? =
        args.getOrNull(index)

    fun requireArg(index: Int): String =
        args.getOrNull(index)
            ?: throw IllegalArgumentException("Missing argument at index $index")
}