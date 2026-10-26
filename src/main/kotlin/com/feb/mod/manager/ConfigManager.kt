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
        var x: Int = 0,
        var y: Int = 10
    )


    data class Config(
        var discordRpc: Boolean = true,
        var theme: String = "WINTER",
        var hud: HudConfig = HudConfig(),
        var customTitleScreen: Boolean = false
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
        }
    }
}