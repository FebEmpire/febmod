package com.feb.mod.ui.hud.entries

import com.feb.mod.api.provider.crypto.LTC
import com.feb.mod.ui.hud.HudEntry
import java.util.Locale

object LtcEntry : HudEntry {
    override val id = "ltc"
    override val displayName = "LTC Price"

    override fun getText(): String {
        val price = LTC.get()

        if (price <= 0.0) {
            return "LTC: Loading"
        }

        return String.format(Locale.US, "LTC: $%.2f", price)
    }
}