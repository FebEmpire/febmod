package com.feb.mod.api.provider

import com.feb.mod.api.provider.system.CpuTemperatureProvider
import com.feb.mod.api.provider.crypto.*
import com.feb.mod.api.provider.minecraft.*

object ProviderRegister {
    fun registerAll() {
        ProviderManager.register(FPSProvider)
        ProviderManager.register(LTCProvider)
        ProviderManager.register(BTCProvider)
        ProviderManager.register(ETHProvider)
        ProviderManager.register(SOLProvider)
        ProviderManager.register(CpuTemperatureProvider)
    }
}