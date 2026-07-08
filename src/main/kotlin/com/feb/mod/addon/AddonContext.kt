package com.feb.mod.addon

import com.feb.mod.manager.AddonConfigManager

class AddonContext(val id: String) {
    fun <T : Any> config(default: T, clazz: Class<T>): AddonConfig<T> =
        AddonConfig(id, default, clazz)

    inline fun <reified T : Any> config(default: T): AddonConfig<T> =
        config(default, T::class.java)
}

class AddonConfig<T : Any>(
    private val addonId: String,
    private val default: T,
    private val clazz: Class<T>
) {
    var current: T = AddonConfigManager.load(addonId, default, clazz)
        private set

    fun save() = AddonConfigManager.save(addonId, current)

    fun update(block: (T) -> Unit) {
        block(current)
        save()
    }

    fun reload() {
        current = AddonConfigManager.load(addonId, default, clazz)
    }
}