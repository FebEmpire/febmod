
package com.feb.mod.internal.sound

import org.slf4j.LoggerFactory
import java.io.File
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.Clip
import javax.sound.sampled.LineEvent

internal object SoundManager {

    private val logger = LoggerFactory.getLogger("febmod/config/sounds")

    private data class SoundKey(
        val owner: String,
        val name: String
    )

    private val sounds = ConcurrentHashMap<SoundKey, File>()

    private val activeClips =
        ConcurrentHashMap<SoundKey, MutableSet<Clip>>()

    fun register(
        owner: String,
        name: String,
        file: File
    ): Boolean {
        require(owner.isNotBlank())
        require(name.isNotBlank())

        if (!file.isFile || !file.canRead()) {
            logger.warn("Cannot register sound '$name': file is unavailable")
            return false
        }

        val key = SoundKey(owner, name)

        return sounds.putIfAbsent(key, file) == null
    }

    fun unregister(
        owner: String,
        name: String
    ): Boolean {
        val key = SoundKey(owner, name)

        val removed = sounds.remove(key) != null

        stop(owner, name)

        return removed
    }

    fun play(
        owner: String,
        name: String
    ): Boolean {
        val key = SoundKey(owner, name)
        val file = sounds[key] ?: return false

        Thread({
            var clip: Clip? = null

            try {
                val audio = AudioSystem.getAudioInputStream(file)

                try {
                    clip = AudioSystem.getClip()
                    clip.open(audio)
                } finally {
                    audio.close()
                }

                val currentClip = clip

                activeClips
                    .computeIfAbsent(key) {
                        ConcurrentHashMap.newKeySet()
                    }
                    .add(currentClip)

                val cleaned = AtomicBoolean(false)

                fun cleanup() {
                    if (!cleaned.compareAndSet(false, true)) return

                    activeClips[key]?.let { clips ->
                        clips.remove(currentClip)

                        if (clips.isEmpty()) {
                            activeClips.remove(key, clips)
                        }
                    }

                    currentClip.close()
                }

                currentClip.addLineListener { event ->
                    if (event.type == LineEvent.Type.STOP) {
                        cleanup()
                    }
                }

                currentClip.start()

            } catch (e: Exception) {
                logger.error(
                    "Failed to play sound '$name' for owner '$owner'",
                    e
                )

                clip?.let {
                    activeClips[key]?.remove(it)
                    it.close()
                }
            }

        }, "FebMod-Sound-$name").apply {
            isDaemon = true
            start()
        }

        return true
    }

    fun stop(
        owner: String,
        name: String
    ): Boolean {
        val key = SoundKey(owner, name)
        val clips = activeClips.remove(key) ?: return false

        clips.forEach { clip ->
            runCatching {
                clip.stop()
                clip.close()
            }
        }

        return true
    }

    fun stopAll(owner: String) {
        activeClips.keys
            .filter { it.owner == owner }
            .forEach { key ->
                stop(key.owner, key.name)
            }
    }

    fun clear(owner: String) {
        stopAll(owner)

        sounds.keys
            .filter { it.owner == owner }
            .forEach(sounds::remove)
    }
}
