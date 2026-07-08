package com.feb.mod.manager

import com.google.gson.GsonBuilder
import net.fabricmc.loader.api.FabricLoader
import java.io.File

object AddonConfigManager {
    private val gson = GsonBuilder().setPrettyPrinting().create()

    private fun fileFor(addonId: String): File {
        val safeId = addonId.replace(Regex("[^a-zA-Z0-9_-]"), "_")
        val dir = File(FabricLoader.getInstance().configDir.toFile(), "febmod/addons/$safeId")
        if (!dir.exists()) dir.mkdirs()
        return File(dir, "config.json")
    }

    fun <T> load(addonId: String, default: T, clazz: Class<T>): T {
        val file = fileFor(addonId)
        if (!file.exists()) {
            save(addonId, default)
            return default
        }
        return runCatching {
            gson.fromJson(file.readText(), clazz) ?: default
        }.getOrDefault(default)
    }

    fun <T> save(addonId: String, config: T) {
        fileFor(addonId).writeText(gson.toJson(config))
    }
}