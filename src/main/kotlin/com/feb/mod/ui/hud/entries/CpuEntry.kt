package com.feb.mod.ui.hud.entries

import com.feb.mod.api.provider.system.CpuTemperatureProvider
import com.feb.mod.ui.hud.HudEntry
import java.util.Locale

object CpuEntry : HudEntry {
    override val id = "CPU"
    override val displayName = "CPU Temperature"

    override fun getText(): String {
        val temperature = CpuTemperatureProvider.temperature

        if (temperature <= 0.0) {
            return "CPU: Loading"
        }

        return String.format(Locale.US, "CPU: %.1f°C", temperature)
    }
}