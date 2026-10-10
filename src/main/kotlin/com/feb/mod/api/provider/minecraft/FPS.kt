package com.feb.mod.api.provider.minecraft

object FPS {
    fun get(): Int {
        return FPSProvider.get()
    }
}