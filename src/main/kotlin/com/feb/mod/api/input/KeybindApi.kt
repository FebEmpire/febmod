package com.feb.mod.api.input

import com.mojang.blaze3d.platform.InputConstants
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper
import net.minecraft.client.KeyMapping
import net.minecraft.resources.Identifier

object KeybindApi {

    private val keybinds = mutableMapOf<String, KeyMapping>()

    val category: KeyMapping.Category =
        KeyMapping.Category.register(
            Identifier.fromNamespaceAndPath("febmod", "main")
        )

    fun register(
        owner: String,
        id: String,
        name: String,
        defaultKey: Int
    ): KeyMapping {
        val fullId = "$owner.$id"

        keybinds[fullId]?.let {
            return it
        }

        val keybind = KeyMapping(
            "key.$fullId",
            InputConstants.Type.KEYSYM,
            defaultKey,
            category
        )

        KeyMappingHelper.registerKeyMapping(keybind)
        keybinds[fullId] = keybind

        return keybind
    }

    fun get(owner: String, id: String): KeyMapping? =
        keybinds["$owner.$id"]

    fun isDown(owner: String, id: String): Boolean =
        get(owner, id)?.isDown == true

    fun wasPressed(owner: String, id: String): Boolean =
        get(owner, id)?.consumeClick() == true

    fun unpress(owner: String, id: String) {
        get(owner, id)?.isDown = false
    }

    fun remove(owner: String, id: String) {
        keybinds.remove("$owner.$id")
    }

    fun clear(owner: String) {
        keybinds.keys
            .filter { it.startsWith("$owner.") }
            .toList()
            .forEach(keybinds::remove)
    }
}