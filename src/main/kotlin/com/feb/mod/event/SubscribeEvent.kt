package com.feb.mod.event

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class SubscribeEvent(
    val priority: Event.Priority = Event.Priority.MEDIUM,
    val receiveCancelled: Boolean = false,
    val once: Boolean = false
)