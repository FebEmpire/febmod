package com.feb.mod.api.chat

import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.Style
import net.minecraft.network.chat.TextColor
import kotlin.math.roundToInt

/*
 * I did NOT make these ChatUtils.
 * +rep @oblongboot
 * 199% of the cred goes to oblongboot.dev (oblonggoat)
 * Go check out his website type shi https://oblongboot.dev/
 */

object ModMessage {

    private val prefix = Component.literal("${ChatFormatting.BLUE}[")
        .append(buildGradient("FebMod", 2166763, 3215339))
        .append(Component.literal("${ChatFormatting.BLUE}] "))

    fun send(message: String) {
        val client = Minecraft.getInstance()

        if (client.player == null) {
            println("Failed to send ModMessage: null player")
            return
        }

        client.execute {
            client.player?.sendSystemMessage(
                prefix.copy().append(Component.literal(message))
            )
        }
    }

    private fun buildGradient(
        text: String,
        startRgb: Int,
        endRgb: Int
    ): MutableComponent {
        if (text.length <= 1) {
            return Component.literal(text)
                .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(startRgb)))
        }

        fun channel(rgb: Int, shift: Int) =
            (rgb shr shift) and 0xFF

        fun lerp(a: Int, b: Int, t: Double) =
            (a + t * (b - a)).roundToInt()

        fun coloredChar(char: Char, rgb: Int) =
            Component.literal(char.toString())
                .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(rgb)))

        return text.foldIndexed(Component.empty()) { i, acc, char ->
            val t = i.toDouble() / (text.length - 1)

            val rgb =
                (lerp(channel(startRgb, 16), channel(endRgb, 16), t) shl 16) or
                        (lerp(channel(startRgb, 8), channel(endRgb, 8), t) shl 8) or
                        lerp(channel(startRgb, 0), channel(endRgb, 0), t)

            acc.append(coloredChar(char, rgb))
        }
    }
}