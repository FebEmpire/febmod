package com.feb.mod.ui.hud.entries

import com.feb.mod.api.provider.crypto.BTC
import com.feb.mod.ui.hud.HudEntry
import java.util.Locale

object BtcEntry : HudEntry {
    override val id = "btc"
    override val displayName = "BTC Price"

    override fun getText(): String {
        val price = BTC.get()

        if (price <= 0.0) {
            return "BTC: Loading"
        }

        return String.format(Locale.US, "BTC: $%.2f", price)
    }
}