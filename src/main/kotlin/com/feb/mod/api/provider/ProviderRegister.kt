package com.feb.mod.api.provider

import com.feb.mod.api.provider.crypto.BTCProvider
import com.feb.mod.api.provider.crypto.ETHProvider
import com.feb.mod.api.provider.crypto.LTCProvider
import com.feb.mod.api.provider.crypto.SOLProvider
import com.feb.mod.api.provider.minecraft.FPSProvider

object ProviderRegister {
    fun registerAll() {
        ProviderManager.register(FPSProvider)
        ProviderManager.register(LTCProvider)
        ProviderManager.register(BTCProvider)
        ProviderManager.register(ETHProvider)
        ProviderManager.register(SOLProvider)
    }
}