package com.feb.mod.feature

import com.feb.mod.api.input.KeybindApi
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.KeyMapping
import org.lwjgl.glfw.GLFW
import kotlin.math.pow

/*
* Zooooooooooooooooooooooooooooooom by THE februari10
* this is like half broken rn i'll fix it another day
* Like it works but like it's not like particularly like good like yk
* Februari10 out ✌️
 */

object Zoom {

    private lateinit var zoomKey: KeyMapping

    private const val ZOOM_FOV = 20f
    private const val TRANSITION_TIME = 120f

    private var zooming = false
    private var transitionStart = System.nanoTime()
    private var transitionFrom = 0f
    private var transitionTo = 0f
    private var currentFov = 0f

    fun initialize() {
        zoomKey = KeybindApi.register(
            owner = "febmod",
            id = "zoom",
            name = "Zoom",
            defaultKey = GLFW.GLFW_KEY_C,
        )

        ClientTickEvents.END_CLIENT_TICK.register {
            val down = zoomKey.isDown

            if (down != zooming) {
                zooming = down
                transitionStart = System.nanoTime()
                transitionFrom = currentFov
                transitionTo = if (zooming) ZOOM_FOV else 0f
            }
        }
    }

    fun modifyFov(baseFov: Float): Float {
        if (currentFov == 0f) {
            currentFov = baseFov
            transitionFrom = baseFov
            transitionTo = if (zooming) ZOOM_FOV else baseFov
            transitionStart = System.nanoTime()
        }

        if (!zooming && transitionTo == 0f) {
            transitionTo = baseFov
        }

        if (!zooming && transitionTo != baseFov) {
            transitionStart = System.nanoTime()
            transitionFrom = currentFov
            transitionTo = baseFov
        }

        val elapsed = (System.nanoTime() - transitionStart) / 1_000_000f
        val progress = (elapsed / TRANSITION_TIME).coerceIn(0f, 1f)
        val eased = 1f - (1f - progress).pow(3f)

        currentFov = transitionFrom + (transitionTo - transitionFrom) * eased

        if (progress >= 1f) {
            currentFov = transitionTo
        }

        return if (zooming || currentFov != transitionTo) {
            currentFov
        } else {
            baseFov
        }
    }

    fun isZooming(): Boolean {
        return zooming
    }
}