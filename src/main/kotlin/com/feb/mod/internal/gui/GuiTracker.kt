package com.feb.mod.internal.gui

import com.feb.mod.api.event.EventBus
import com.feb.mod.api.event.events.ContainerUpdateEvent
import com.feb.mod.api.event.events.GameTickEvent
import com.feb.mod.api.event.events.ScreenCloseEvent
import com.feb.mod.api.event.events.ScreenOpenEvent
import com.feb.mod.api.gui.ContainerItem
import com.feb.mod.api.gui.ContainerView
import com.feb.mod.api.gui.ScreenView
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.core.registries.BuiltInRegistries

object GuiTracker {
    private var initialized = false
    private var currentScreen: Screen? = null
    private var currentScreenView: ScreenView? = null
    private var currentContainer: ContainerView? = null
    private var previousSignature: List<Pair<String, Int>>? = null

    fun initialize() {
        if (initialized) return
        initialized = true

        EventBus.register<GameTickEvent>("febmod:gui_tracker") {
            update()
        }
    }

    fun getCurrentScreen(): ScreenView? = currentScreenView

    fun getCurrentContainer(): ContainerView? = currentContainer

    private fun update() {
        val screen = Minecraft.getInstance().screen

        if (screen !== currentScreen) {
            currentScreenView?.let {
                EventBus.post(ScreenCloseEvent(it))
            }

            currentScreen = screen
            currentScreenView = screen?.let {
                ScreenSnapshot(it.title.string)
            }

            currentScreenView?.let {
                EventBus.post(ScreenOpenEvent(it))
            }

            currentContainer = null
            previousSignature = null
        }

        val containerScreen = screen as? AbstractContainerScreen<*>

        if (containerScreen == null) {
            currentContainer = null
            previousSignature = null
            return
        }

        val menu = containerScreen.menu
        val signature = menu.slots.map { slot ->
            val stack = slot.item
            BuiltInRegistries.ITEM.getKey(stack.item).toString() to stack.count
        }

        if (signature == previousSignature && currentContainer != null) return

        val snapshot = ContainerSnapshot(
            title = containerScreen.title.string,
            items = menu.slots.map { slot ->
                val stack = slot.item
                ContainerItemSnapshot(
                    itemId = BuiltInRegistries.ITEM.getKey(stack.item).toString(),
                    count = stack.count,
                    isEmpty = stack.isEmpty
                )
            }
        )

        previousSignature = signature
        currentContainer = snapshot
        EventBus.post(ContainerUpdateEvent(snapshot))
    }

    private data class ScreenSnapshot(
        override val title: String
    ) : ScreenView

    private data class ContainerSnapshot(
        override val title: String,
        private val items: List<ContainerItemSnapshot>
    ) : ContainerView {
        override val slotCount: Int
            get() = items.size

        override fun getStack(slot: Int): ContainerItem? =
            items.getOrNull(slot)
    }

    private data class ContainerItemSnapshot(
        override val itemId: String,
        override val count: Int,
        override val isEmpty: Boolean
    ) : ContainerItem
}