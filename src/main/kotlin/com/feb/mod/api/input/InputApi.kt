package com.feb.mod.api.input

import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.client.KeyMapping
import net.minecraft.client.Minecraft

object InputApi {

    private val minecraft: Minecraft
        get() = Minecraft.getInstance()

    private fun currentKey(mapping: KeyMapping): InputConstants.Key? {
        return runCatching {
            val field = KeyMapping::class.java.getDeclaredField("key")
            field.trySetAccessible()
            field.get(mapping) as InputConstants.Key
        }.getOrNull()
    }

    fun press(mapping: KeyMapping) {
        val key = currentKey(mapping) ?: return
        KeyMapping.set(key, true)
        KeyMapping.click(key)
    }

    fun release(mapping: KeyMapping) {
        val key = currentKey(mapping) ?: return
        KeyMapping.set(key, false)
    }

    fun click(mapping: KeyMapping) {
        press(mapping)
        release(mapping)
    }

    fun releaseAll() {
        KeyMapping.releaseAll()
    }

    fun selectHotbarSlot(slot: Int) {
        if (slot !in 0..8) return
        click(minecraft.options.keyHotbarSlots[slot])
    }

    fun useItem() {
        click(minecraft.options.keyUse)
    }

    fun attack() {
        click(minecraft.options.keyAttack)
    }

    fun dropItem() {
        click(minecraft.options.keyDrop)
    }

    fun swapOffhand() {
        click(minecraft.options.keySwapOffhand)
    }

    fun openInventory() {
        click(minecraft.options.keyInventory)
    }
}