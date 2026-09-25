package com.feb.mod.manager

import com.feb.mod.addon.AddonContext
import com.feb.mod.addon.AddonMetadata
import com.feb.mod.addon.FebAddon
import com.google.gson.Gson
import net.fabricmc.loader.api.FabricLoader
import org.slf4j.LoggerFactory
import java.io.File
import java.net.URLClassLoader

object AddonManager {
    private val logger = LoggerFactory.getLogger("febmod/addons")
    private val gson = Gson()
    private val loadedAddons = mutableListOf<LoadedAddon>()
    private val usedIds = mutableSetOf<String>()

    fun loadAddons() {
        val addonsDir = FabricLoader.getInstance()
            .gameDir
            .resolve("febmod/addons")
            .toFile()

        if (!addonsDir.exists()) {
            addonsDir.mkdirs()
            return
        }

        val jars = addonsDir.listFiles { file ->
            file.isFile && file.extension == "jar"
        } ?: return

        jars.forEach(::loadAddon)
    }

    fun unloadAddon(id: String): Boolean {
        val addon = loadedAddons.firstOrNull { it.metadata.id == id } ?: return false

        runCatching {
            addon.instance.unload()
        }.onFailure {
            logger.error("Failed to unload addon: $id", it)
        }

        runCatching {
            addon.context.cleanup()
        }.onFailure {
            logger.error("Failed to clean up addon: $id", it)
        }

        loadedAddons.remove(addon)
        usedIds.remove(id)

        runCatching {
            addon.classLoader.close()
        }.onFailure {
            logger.error("Failed to close classloader for addon: $id", it)
        }

        logger.info("Unloaded addon: $id")

        return true
    }

    fun unloadAll() {
        loadedAddons
            .map { it.metadata.id }
            .toList()
            .forEach(::unloadAddon)
    }

    fun getAddons(): List<FebAddon> =
        loadedAddons.map { it.instance }

    fun getAddonEntries(): List<Pair<AddonMetadata, FebAddon>> =
        loadedAddons.map { it.metadata to it.instance }

    fun getMetadata(id: String): AddonMetadata? =
        loadedAddons
            .firstOrNull { it.metadata.id == id }
            ?.metadata

    fun getAddon(id: String): FebAddon? =
        loadedAddons
            .firstOrNull { it.metadata.id == id }
            ?.instance

    private fun loadAddon(jar: File) {
        val classLoader = URLClassLoader(
            arrayOf(jar.toURI().toURL()),
            Thread.currentThread().contextClassLoader
        )

        try {
            val metadata = classLoader.getResourceAsStream("febmod.addon.json")
                ?.use { stream ->
                    gson.fromJson(stream.reader(), AddonMetadata::class.java)
                }

            if (metadata == null) {
                logger.warn("${jar.name} has no febmod.addon.json, we are NOT loading this")
                classLoader.close()
                return
            }

            if (!metadata.id.matches(Regex("[a-zA-Z0-9_-]+"))) {
                logger.warn("${jar.name} has an invalid 'id' (${metadata.id}), we are NOT loading this")
                classLoader.close()
                return
            }

            if (!usedIds.add(metadata.id)) {
                logger.warn("${jar.name} uses id '${metadata.id}' which is already taken by another addon, we are NOT loading this")
                classLoader.close()
                return
            }

            val clazz = classLoader.loadClass(metadata.entrypoint)
            val instance = clazz.getDeclaredConstructor().newInstance()

            if (instance !is FebAddon) {
                logger.warn("${jar.name} entrypoint does not implement FebAddon, we are NOT loading this")
                usedIds.remove(metadata.id)
                classLoader.close()
                return
            }

            val context = AddonContext(metadata.id)

            runCatching {
                instance.initialize(context)
            }.onFailure {
                usedIds.remove(metadata.id)

                runCatching {
                    context.cleanup()
                }.onFailure { cleanupError ->
                    logger.error("Failed to clean up failed addon: ${metadata.id}", cleanupError)
                }

                classLoader.close()
                throw it
            }

            loadedAddons.add(
                LoadedAddon(
                    metadata = metadata,
                    instance = instance,
                    context = context,
                    classLoader = classLoader
                )
            )

            logger.info(
                "Loaded addon: ${metadata.name} v${metadata.version} (id=${metadata.id})"
            )
        } catch (e: Exception) {
            logger.error("Failed to load addon from ${jar.name}", e)

            if (loadedAddons.none { it.classLoader == classLoader }) {
                runCatching {
                    classLoader.close()
                }
            }
        }
    }

    private data class LoadedAddon(
        val metadata: AddonMetadata,
        val instance: FebAddon,
        val context: AddonContext,
        val classLoader: URLClassLoader
    )
}