package com.feb.mod.api.event

abstract class Event {
    enum class Priority { HIGHEST, HIGH, MEDIUM, LOW, LOWEST }

    interface Cancellable {
        var cancelled: Boolean
    }
}