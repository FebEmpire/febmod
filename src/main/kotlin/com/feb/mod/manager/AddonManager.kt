package com.feb.mod.manager

import com.feb.mod.addon.FebAddon
import com.google.gson.JsonParser
import net.fabricmc.loader.api.FabricLoader
import org.slf4j.LoggerFactory
import java.net.URLClassLoader

object AddonManager {
    private val logger = LoggerFactory.getLogger("febmod/addons")
    private val loadedAddons = mutableListOf<FebAddon>()

    fun loadAddons() {
        val addonsDir = FabricLoader.getInstance().configDir.resolve("febmod/addons").toFile()

        if (!addonsDir.exists()) {
            addonsDir.mkdirs()
            return
        }

        val jars = addonsDir.listFiles { f -> f.extension == "jar" } ?: return

        for (jar in jars) {
            try {
                val classLoader = URLClassLoader(arrayOf(jar.toURI().toURL()), Thread.currentThread().contextClassLoader)
                val manifestStream = classLoader.getResourceAsStream("febmod.addon.json")

                if (manifestStream == null) {
                    logger.warn("${jar.name} has no febmod.addon.json, we are NOT loading this")
                    continue
                }

                val json = JsonParser.parseReader(manifestStream.reader()).asJsonObject
                val entrypoint = json.get("entrypoint")?.asString

                if (entrypoint == null) {
                    logger.warn("${jar.name} febmod.addon.json is missing 'entrypoint', we are NOT loading this")
                    continue
                }

                val clazz = classLoader.loadClass(entrypoint)
                val instance = clazz.getDeclaredConstructor().newInstance()

                if (instance !is FebAddon) {
                    logger.warn("${jar.name} entrypoint does not implement FebAddon, we are NOT loading this")
                    continue
                }

                instance.initialize()
                loadedAddons.add(instance)
                logger.info("Loaded addon: ${instance.name} v${instance.version}")
            } catch (e: Exception) {
                logger.error("Failed to load addon from ${jar.name}: ${e.message}")
            }
        }
    }

    fun getAddons(): List<FebAddon> = loadedAddons
}