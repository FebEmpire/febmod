package com.feb.mod.api.event.events

import com.feb.mod.api.event.Event

class ChatReceivedEvent(
    val text: String,
    val isSystem: Boolean
) : Event(), Event.Cancellable {

    override var cancelled: Boolean = false
}