package com.feb.mod.event.events

import com.feb.mod.event.Event

class ChatReceivedEvent(
    val message: String,
    val system: Boolean
) : Event(), Event.Cancellable {
    override var cancelled: Boolean = false
}