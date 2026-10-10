package com.feb.mod.api.gui

import com.feb.mod.api.event.EventBus
import com.feb.mod.api.event.events.ContainerUpdateEvent
import com.feb.mod.api.event.events.ScreenCloseEvent
import com.feb.mod.api.event.events.ScreenOpenEvent
import com.feb.mod.internal.gui.GuiTracker

class GuiApi(private val owner: String) {
    fun getCurrentScreen(): ScreenView? =
        GuiTracker.getCurrentScreen()

    fun getCurrentContainer(): ContainerView? =
        GuiTracker.getCurrentContainer()

    fun isScreenOpen(): Boolean =
        getCurrentScreen() != null

    fun isContainerOpen(): Boolean =
        getCurrentContainer() != null

    fun onScreenOpen(handler: (ScreenView) -> Unit): EventBus.Subscription =
        EventBus.register<ScreenOpenEvent>(owner) {
            handler(it.screen)
        }

    fun onScreenClose(handler: (ScreenView) -> Unit): EventBus.Subscription =
        EventBus.register<ScreenCloseEvent>(owner) {
            handler(it.screen)
        }

    fun onContainerUpdate(handler: (ContainerView) -> Unit): EventBus.Subscription =
        EventBus.register<ContainerUpdateEvent>(owner) {
            handler(it.container)
        }
}