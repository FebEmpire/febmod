package com.feb.mod.api.provider.minecraft

import com.feb.mod.api.provider.Provider
import net.minecraft.client.Minecraft

object FPSProvider : Provider {
    override val id = "fps"

    override fun initialize() {
    }

    fun get(): Int {
        return Minecraft.getInstance().fps
    }
}