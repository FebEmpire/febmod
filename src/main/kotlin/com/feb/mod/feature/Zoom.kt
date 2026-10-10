package com.feb.mod.feature

import com.feb.mod.api.input.KeybindApi
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.KeyMapping
import org.lwjgl.glfw.GLFW
import kotlin.math.pow

object Zoom {

    private lateinit var zoomKey: KeyMapping

    private const val ZOOM_FACTOR = 0.3f
    private const val MIN_ZOOM_FOV = 5f
    private const val TRANSITION_MS = 120f

    private var zooming = false
    private var progress = 0f
    private var lastNanos = System.nanoTime()

    fun initialize() {
        zoomKey = KeybindApi.register(
            owner = "febmod",
            id = "zoom",
            defaultKey = GLFW.GLFW_KEY_C,
        )

        ClientTickEvents.END_CLIENT_TICK.register {
            zooming = zoomKey.isDown
        }
    }

    fun modifyFov(baseFov: Float): Float {
        val now = System.nanoTime()
        val dtMs = ((now - lastNanos) / 1_000_000f).coerceAtMost(100f)
        lastNanos = now

        val step = dtMs / TRANSITION_MS
        progress = (if (zooming) progress + step else progress - step).coerceIn(0f, 1f)

        if (progress == 0f) return baseFov

        val eased = 1f - (1f - progress).pow(3f)
        val targetFov = (baseFov * ZOOM_FACTOR).coerceAtLeast(MIN_ZOOM_FOV)
        return baseFov + (targetFov - baseFov) * eased
    }

    fun isZooming(): Boolean = zooming

    fun zoomProgress(): Float = progress
}