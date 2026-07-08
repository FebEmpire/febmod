package com.feb.mod.manager

import com.feb.mod.addon.AddonContext
import com.feb.mod.addon.FebAddon
import com.google.gson.JsonParser
import net.fabricmc.loader.api.FabricLoader
import org.slf4j.LoggerFactory
import java.net.URLClassLoader

object AddonManager {
    private val logger = LoggerFactory.getLogger("febmod/addons")
    private val loadedAddons = mutableListOf<FebAddon>()
    private val usedIds = mutableSetOf<String>()

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
                val id = json.get("id")?.asString

                if (entrypoint == null) {
                    logger.warn("${jar.name} febmod.addon.json is missing 'entrypoint', we are NOT loading this")
                    continue
                }

                if (id == null) {
                    logger.warn("${jar.name} febmod.addon.json is missing 'id', we are NOT loading this")
                    continue
                }

                if (!id.matches(Regex("[a-zA-Z0-9_-]+"))) {
                    logger.warn("${jar.name} has an invalid 'id' ($id), we are NOT loading this")
                    continue
                }

                if (!usedIds.add(id)) {
                    logger.warn("${jar.name} uses id '$id' which is already taken by another addon, we are NOT loading this")
                    continue
                }

                val clazz = classLoader.loadClass(entrypoint)
                val instance = clazz.getDeclaredConstructor().newInstance()

                if (instance !is FebAddon) {
                    logger.warn("${jar.name} entrypoint does not implement FebAddon, we are NOT loading this")
                    usedIds.remove(id)
                    continue
                }

                instance.initialize(AddonContext(id))
                loadedAddons.add(instance)
                logger.info("Loaded addon: ${instance.name} v${instance.version} (id=$id)")
            } catch (e: Exception) {
                logger.error("Failed to load addon from ${jar.name}: ${e.message}")
            }
        }
    }

    fun getAddons(): List<FebAddon> = loadedAddons
}