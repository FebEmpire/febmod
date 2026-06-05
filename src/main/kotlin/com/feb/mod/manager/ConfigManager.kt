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

    data class Config(
        var discordRpc: Boolean = true,
        var theme: String = "WINTER"
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
            current = gson.fromJson(configFile.readText(), Config::class.java) ?: Config()
        }
    }
}