package com.feb.mod.utils

import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.Style
import net.minecraft.network.chat.TextColor
import kotlin.math.roundToInt

/*
* i did NOT make these ChatUtils
* +rep @oblongboot
* 199% of the cred goes to oblongboot.dev (oblonggoat)
* Go check out his website type shi https://oblongboot.dev/  󠁛󠀣󠀱󠁥󠀰󠀷󠀷󠀷󠀬󠀣󠀰󠁢󠀵󠀴󠀸󠀴󠁝
 */

object ChatUtils {
    var FebModPrefix = Component.literal("${ChatFormatting.BLUE}[")
        .append(buildGradient("FebMod", 2166763, 3215339)) // 210feb to 310feb
        .append(Component.literal("${ChatFormatting.BLUE}] "))

    fun modMessage(message: String) {
        if (Minecraft.getInstance().player == null) return println("failed to send message, null player")

        val prefix = Component.literal("${ChatFormatting.BLUE}[")
            .append(buildGradient("FebMod", 2166763, 3215339))
            .append(Component.literal("${ChatFormatting.BLUE}] "))

        val finalMessage: Component = prefix.append(Component.literal(message))

        Minecraft.getInstance().execute {
            Minecraft.getInstance().player?.sendSystemMessage(finalMessage)
        }
    }

    fun buildGradient(text: String, startRgb: Int, endRgb: Int): MutableComponent {
        if (text.length <= 1)
            return Component.literal(text).setStyle(Style.EMPTY.withColor(TextColor.fromRgb(startRgb)))

        fun channel(rgb: Int, shift: Int) = (rgb shr shift) and 0xFF
        fun lerp(a: Int, b: Int, t: Double) = (a + t * (b - a)).roundToInt()
        fun coloredChar(char: Char, rgb: Int) =
            Component.literal(char.toString()).setStyle(Style.EMPTY.withColor(TextColor.fromRgb(rgb)))

        return text.foldIndexed(Component.empty()) { i, acc, char ->
            val t = i.toDouble() / (text.length - 1)
            val rgb = lerp(channel(startRgb, 16), channel(endRgb, 16), t) shl 16 or
                    (lerp(channel(startRgb,  8), channel(endRgb,  8), t) shl  8) or
                    lerp(channel(startRgb,  0), channel(endRgb,  0), t)
            acc.append(coloredChar(char, rgb))
        }
    }
}