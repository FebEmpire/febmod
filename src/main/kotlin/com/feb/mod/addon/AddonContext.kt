package com.feb.mod.addon

import com.feb.mod.event.Event
import com.feb.mod.event.EventBus
import com.feb.mod.manager.AddonConfigManager

class AddonContext(val id: String) {
    val events = AddonEvents(id)

    fun <T : Any> config(default: T, clazz: Class<T>): AddonConfig<T> =
        AddonConfig(id, default, clazz)

    inline fun <reified T : Any> config(default: T): AddonConfig<T> =
        config(default, T::class.java)
}

class AddonEvents(@PublishedApi internal val owner: String) {
    fun register(listener: Any) = EventBus.register(owner, listener)

    inline fun <reified T : Event> on(
        priority: Event.Priority = Event.Priority.MEDIUM,
        receiveCancelled: Boolean = false,
        once: Boolean = false,
        noinline handler: (T) -> Unit
    ): EventBus.Subscription = EventBus.register(owner, priority, receiveCancelled, once, handler)
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