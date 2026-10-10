package com.feb.mod.api.gui

interface ContainerView {
    val title: String
    val slotCount: Int

    fun getStack(slot: Int): ContainerItem?
}

interface ContainerItem {
    val itemId: String
    val count: Int
    val isEmpty: Boolean
}