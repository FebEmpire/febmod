package com.feb.mod.api.provider

import com.feb.mod.api.provider.minecraft.FPSProvider

object ProviderRegister {
    fun registerAll() {
        ProviderManager.register(FPSProvider)
    }
}