package com.feb.mod.internal.sound

import java.io.File

object SoundDirectory {
    val directory = File("febmod/config/sounds")

    fun initialize() {
        if (!directory.exists()) {
            directory.mkdirs()
        }
    }

    fun getSound(name: String): File? {
        val file = File(directory, "$name.wav")
        return file.takeIf { it.exists() }
    }
}