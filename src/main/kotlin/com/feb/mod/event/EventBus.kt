package com.feb.mod.event

import java.lang.invoke.MethodHandles
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicLong
import org.slf4j.LoggerFactory

object EventBus {
    private val logger = LoggerFactory.getLogger("febmod/events")
    private val listeners = CopyOnWriteArrayList<Listener>()
    private val cache = ConcurrentHashMap<Class<*>, Array<Listener>>()
    private val idCounter = AtomicLong(0)
    private val failureCounts = ConcurrentHashMap<String, Int>()
    private const val MAX_FAILURES_BEFORE_DISABLE = 10

    class Subscription internal constructor(private val id: Long, private val owner: String) {
        fun cancel() {
            if (listeners.removeIf { it.id == id }) {
                cache.clear()
            }
        }

        fun owner(): String = owner
    }

    private class Listener(
        val id: Long,
        val owner: String,
        val eventType: Class<out Event>,
        val priority: Event.Priority,
        val receiveCancelled: Boolean,
        val once: Boolean,
        val label: String,
        val invoke: (Event) -> Unit
    )

    fun register(owner: String, target: Any): List<Subscription> {
        val toAdd = target.javaClass.declaredMethods.mapNotNull { method ->
            val annotation = method.getAnnotation(SubscribeEvent::class.java) ?: return@mapNotNull null
            val params = method.parameterTypes
            if (params.size != 1 || !Event::class.java.isAssignableFrom(params[0])) {
                logger.warn("Skipping ${target.javaClass.name}#${method.name}, invalid @SubscribeEvent signature")
                return@mapNotNull null
            }

            val handle = try {
                method.trySetAccessible()
                MethodHandles.privateLookupIn(target.javaClass, MethodHandles.lookup())
                    .unreflect(method)
                    .bindTo(target)
            } catch (e: Exception) {
                logger.warn("Could not bind ${target.javaClass.name}#${method.name}: ${e.message}")
                return@mapNotNull null
            }

            Listener(
                id = idCounter.incrementAndGet(),
                owner = owner,
                eventType = params[0].asSubclass(Event::class.java),
                priority = annotation.priority,
                receiveCancelled = annotation.receiveCancelled,
                once = annotation.once,
                label = "${target.javaClass.simpleName}#${method.name}",
                invoke = { event -> handle.invoke(event) }
            )
        }

        if (toAdd.isEmpty()) return emptyList()
        listeners.addAll(toAdd)
        cache.clear()
        return toAdd.map { Subscription(it.id, it.owner) }
    }

    inline fun <reified T : Event> register(
        owner: String,
        priority: Event.Priority = Event.Priority.MEDIUM,
        receiveCancelled: Boolean = false,
        once: Boolean = false,
        noinline handler: (T) -> Unit
    ): Subscription = registerHandler(owner, T::class.java, priority, receiveCancelled, once, handler)

    fun <T : Event> registerHandler(
        owner: String,
        eventType: Class<T>,
        priority: Event.Priority,
        receiveCancelled: Boolean,
        once: Boolean,
        handler: (T) -> Unit
    ): Subscription {
        val id = idCounter.incrementAndGet()
        @Suppress("UNCHECKED_CAST")
        val listener = Listener(
            id = id,
            owner = owner,
            eventType = eventType,
            priority = priority,
            receiveCancelled = receiveCancelled,
            once = once,
            label = "<lambda:${eventType.simpleName}>",
            invoke = handler as (Event) -> Unit
        )
        listeners.add(listener)
        cache.clear()
        return Subscription(id, owner)
    }

    fun unregister(owner: String) {
        if (listeners.removeIf { it.owner == owner }) {
            cache.clear()
        }
        failureCounts.remove(owner)
    }

    fun isOwnerRegistered(owner: String): Boolean = listeners.any { it.owner == owner }

    fun <T : Event> post(event: T): T {
        val matched = cache.computeIfAbsent(event.javaClass) { cls ->
            listeners.filter { it.eventType.isAssignableFrom(cls) }
                .sortedBy { it.priority.ordinal }
                .toTypedArray()
        }

        val toRemove = mutableListOf<Listener>()

        for (listener in matched) {
            if (failureCounts.getOrDefault(listener.owner, 0) >= MAX_FAILURES_BEFORE_DISABLE) continue
            if (event is Event.Cancellable && event.cancelled && !listener.receiveCancelled) continue

            try {
                listener.invoke(event)
            } catch (t: Throwable) {
                val count = failureCounts.merge(listener.owner, 1, Int::plus) ?: 1
                logger.error("Error dispatching ${event.javaClass.simpleName} to ${listener.owner} (${listener.label})", t)
                if (count == MAX_FAILURES_BEFORE_DISABLE) {
                    logger.error("Owner '${listener.owner}' disabled after $MAX_FAILURES_BEFORE_DISABLE consecutive failures")
                }
            }

            if (listener.once) {
                toRemove.add(listener)
            }
        }

        if (toRemove.isNotEmpty()) {
            listeners.removeAll(toRemove.toSet())
            cache.clear()
        }

        return event
    }

    fun snapshot(): List<String> =
        listeners.map { "${it.owner} -> ${it.eventType.simpleName} (${it.label}, priority=${it.priority})" }
}