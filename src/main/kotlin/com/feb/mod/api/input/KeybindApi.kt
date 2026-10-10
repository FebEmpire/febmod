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
        defaultKey: Int
    ): KeyMapping {
        require(owner.isNotBlank())
        require(id.isNotBlank())

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

    fun remove(owner: String, id: String): Boolean {
        val keybind = keybinds.remove("$owner.$id") ?: return false
        keybind.isDown = false
        return true
    }

    fun clear(owner: String) {
        keybinds.entries
            .filter { it.key.startsWith("$owner.") }
            .forEach {
                it.value.isDown = false
                keybinds.remove(it.key)
            }
    }

    fun getAll(owner: String): List<KeyMapping> =
        keybinds
            .filterKeys { it.startsWith("$owner.") }
            .values
            .toList()
}