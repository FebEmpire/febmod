package com.feb.mod.api.chat

import com.feb.mod.api.event.EventBus
import com.feb.mod.api.event.events.ChatReceivedEvent

class ChatApi(
    private val owner: String
) {

    fun onReceived(handler: (ChatMessage) -> Unit) {
        EventBus.register<ChatReceivedEvent>(owner) { event ->
            handler(
                ChatMessage(
                    text = event.text,
                    isSystem = event.isSystem
                )
            )
        }
    }

    fun notify(message: String) {
        ModMessage.send(message)
    }
}

data class ChatMessage(
    val text: String,
    val isSystem: Boolean
)