package com.feb.mod.ui.hud.entries

import com.feb.mod.api.provider.crypto.SOL
import com.feb.mod.ui.hud.HudEntry
import java.util.Locale

object SolEntry : HudEntry {
    override val id = "Sol"
    override val displayName = "Sol Price"

    override fun getText(): String {
        val price = SOL.get()

        if (price <= 0.0) {
            return "SOl: Loading"
        }

        return String.format(Locale.US, "SOL: $%.2f", price)
    }
}