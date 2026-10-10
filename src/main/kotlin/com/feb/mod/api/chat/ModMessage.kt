package com.feb.mod.api.chat

import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style

object ModMessage {

    private val prefix = Component.empty()
        .append(
            Component.literal("[")
                .withStyle(ChatFormatting.BLACK)
        )
        .append(
            Component.literal("FebMod")
                .withStyle(ChatFormatting.BLUE)
        )
        .append(
            Component.literal("]")
                .withStyle(ChatFormatting.BLACK)
        )
        .append(
            Component.literal(" ")
                .withStyle(Style.EMPTY)
        )

    fun send(message: String) {
        val client = Minecraft.getInstance()

        if (client.player == null) {
            println("Failed to send ModMessage: null player")
            return
        }

        client.execute {
            client.player?.sendSystemMessage(
                Component.empty()
                    .append(prefix)
                    .append(
                        Component.literal(message)
                            .withStyle(Style.EMPTY)
                    )
            )
        }
    }

    fun error(message: String) {
        val client = Minecraft.getInstance()

        if (client.player == null) {
            println("Failed to send ModMessage error: null player")
            return
        }

        client.execute {
            client.player?.sendSystemMessage(
                Component.empty()
                    .append(prefix)
                    .append(
                        Component.literal(message)
                            .withStyle(ChatFormatting.RED)
                    )
            )
        }
    }
}