package com.feb.mod.api.input

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.world.inventory.ContainerInput

object SlotClickApi {
    private val minecraft: Minecraft
        get() = Minecraft.getInstance()

    fun click(
        slot: Int,
        button: Int = 0,
        input: ContainerInput = ContainerInput.PICKUP
    ): Boolean {
        val player = minecraft.player ?: return false
        val screen = minecraft.screen as? AbstractContainerScreen<*> ?: return false
        val menu = screen.menu
        val gameMode = minecraft.gameMode ?: return false

        if (slot !in menu.slots.indices) return false

        gameMode.handleContainerInput(
            menu.containerId,
            slot,
            button,
            input,
            player
        )

        return true
    }
}