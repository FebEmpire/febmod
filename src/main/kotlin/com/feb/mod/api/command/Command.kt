package com.feb.mod.api.command

data class Command(
    val names: Set<String>,
    val handler: (CommandContext) -> Unit
)