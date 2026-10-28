package com.feb.mod.manager

import com.google.gson.GsonBuilder
import net.minecraft.client.Minecraft
import java.io.File

object ConfigManager {
    private val gson = GsonBuilder().setPrettyPrinting().create()

    private val configFile: File
        get() {
            val dir = File(Minecraft.getInstance().gameDirectory, "config/febmod")
            if (!dir.exists()) dir.mkdirs()
            return File(dir, "settings.json")
        }

    data class HudConfig(
        var enabled: Boolean = true,
        var entries: MutableMap<String, Boolean> = mutableMapOf(),
        var x: Int = -1,
        var y: Int = 10,
        var scale: Float = 1.0f
    )

    data class AddonHudConfig(
        var enabled: Boolean = true,
        var x: Int = 10,
        var y: Int = 10,
        var scale: Float = 1.0f
    )

    data class Config(
        var discordRpc: Boolean = true,
        var theme: String = "WINTER",
        var hud: HudConfig = HudConfig(),
        var customTitleScreen: Boolean = false,
        var addonHuds: MutableMap<String, AddonHudConfig> = mutableMapOf()
    )

    var current = Config()

    fun save() {
        configFile.writeText(gson.toJson(current))
    }

    fun load() {
        if (!configFile.exists()) {
            save()
            return
        }

        runCatching {
            current = gson.fromJson(
                configFile.readText(),
                Config::class.java
            ) ?: Config()

            current.hud.scale = current.hud.scale
                .takeIf { it.isFinite() && it > 0f }
                ?.coerceIn(0.5f, 2.0f)
                ?: 1.0f

            current.addonHuds.values.forEach { config ->
                config.scale = config.scale
                    .takeIf { it.isFinite() && it > 0f }
                    ?.coerceIn(0.5f, 2.0f)
                    ?: 1.0f
            }
        }
    }
}
