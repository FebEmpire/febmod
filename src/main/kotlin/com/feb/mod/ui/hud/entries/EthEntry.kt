package com.feb.mod.ui.hud.entries

import com.feb.mod.api.provider.crypto.ETH
import com.feb.mod.ui.hud.HudEntry
import java.util.Locale

object EthEntry : HudEntry {
    override val id = "eth"
    override val displayName = "ETH Price"

    override fun getText(): String {
        val price = ETH.get()

        if (price <= 0.0) {
            return "ETH: Loading"
        }

        return String.format(Locale.US, "ETH: $%.2f", price)
    }
}