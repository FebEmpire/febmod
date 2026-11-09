package com.feb.mod.api.sound

import com.feb.mod.internal.sound.SoundManager
import java.io.File

class SoundPlayerApi(
    private val owner: String
) {

    init {
        require(owner.isNotBlank())
    }

    fun register(name: String, file: File): Boolean {
        return SoundManager.register(owner, name, file)
    }

    fun unregister(name: String): Boolean {
        return SoundManager.unregister(owner, name)
    }

    fun play(name: String): Boolean {
        return SoundManager.play(owner, name)
    }

    fun stop(name: String): Boolean {
        return SoundManager.stop(owner, name)
    }

    fun stopAll() {
        SoundManager.stopAll(owner)
    }

    fun clear() {
        SoundManager.clear(owner)
    }
}
