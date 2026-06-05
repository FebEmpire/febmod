package com.feb.mod.ui.gui.particles

import net.minecraft.client.gui.GuiGraphicsExtractor
import kotlin.math.sin

class ParticleManager {

    enum class Style { WINTER, SPRING, SUMMER, AUTUMN }

    private val particles = mutableListOf<Particle>()
    private var style: Style = Style.WINTER
    private var screenWidth = 0
    private var screenHeight = 0

    fun initialize(width: Int, height: Int, newStyle: Style = style) {
        screenWidth = width
        screenHeight = height
        style = newStyle
        particles.clear()
        val count = when (style) {
            Style.WINTER -> 50
            Style.SPRING -> 60
            Style.SUMMER -> 30
            Style.AUTUMN -> 35
        }
        repeat(count) { particles.add(spawnParticle(randomY = true)) }
    }

    fun setStyle(newStyle: Style, width: Int, height: Int) {
        initialize(width, height, newStyle)
    }

    private fun spawnParticle(randomY: Boolean = false): Particle {
        val x = (Math.random() * screenWidth).toFloat()
        val y = if (randomY) (Math.random() * screenHeight).toFloat() else -10f

        return when (style) {

            Style.WINTER -> Particle(
                x      = x,
                y      = y,
                size   = (Math.random() * 2.5 + 0.5).toFloat(),
                speedY = (Math.random() * 0.8 + 0.4).toFloat(),
                speedX = (Math.random() * 0.6 - 0.3).toFloat(),
                alpha  = (Math.random() * 0.5 + 0.4).toFloat(),
                color  = if (Math.random() > 0.7) 0xE0F0FF else 0xFFFFFF,
                phase  = 0.0
            )

            Style.SPRING -> Particle(
                x      = x,
                y      = y,
                size   = 1f,
                speedY = (Math.random() * 3.0 + 4.0).toFloat(),
                speedX = (Math.random() * 0.5 - 0.8).toFloat(),
                alpha  = (Math.random() * 0.3 + 0.3).toFloat(),
                color  = if (Math.random() > 0.5) 0xAADDFF else 0x88BBFF,
                phase  = 0.0
            )

            Style.SUMMER -> Particle(
                x      = (Math.random() * screenWidth).toFloat(),
                y      = (Math.random() * screenHeight).toFloat(),
                size   = (Math.random() * 1.5 + 1.0).toFloat(),
                speedY = (Math.random() * 0.2 - 0.1).toFloat(),
                speedX = (Math.random() * 0.2 - 0.1).toFloat(),
                alpha  = (Math.random() * 0.4 + 0.2).toFloat(),
                color  = if (Math.random() > 0.5) 0xFFEE66 else 0xFFCC33,
                phase  = Math.random() * Math.PI * 2
            )

            Style.AUTUMN -> Particle(
                x      = x,
                y      = y,
                size   = (Math.random() * 3.0 + 2.0).toFloat(),
                speedY = (Math.random() * 0.6 + 0.3).toFloat(),
                speedX = (Math.random() * 1.2 - 0.6).toFloat(),
                alpha  = (Math.random() * 0.5 + 0.4).toFloat(),
                color  = LEAF_COLORS.random(),
                phase  = Math.random() * Math.PI * 2
            )
        }
    }

    fun update(width: Int, height: Int) {
        screenWidth = width
        screenHeight = height

        particles.forEach { p ->
            when (style) {

                Style.WINTER -> {
                    p.y += p.speedY
                    p.x += p.speedX
                    p.alpha += (Math.random() * 0.08 - 0.04).toFloat()
                    p.alpha = p.alpha.coerceIn(0.3f, 0.9f)
                    if (p.y > height + 10) { p.y = -10f; p.x = (Math.random() * width).toFloat() }
                    if (p.x < -10) p.x = width.toFloat() + 10
                    if (p.x > width + 10) p.x = -10f
                }

                Style.SPRING -> {
                    p.y += p.speedY
                    p.x += p.speedX
                    if (p.y > height + 10) {
                        p.y = -10f
                        p.x = (Math.random() * width).toFloat()
                    }
                }

                Style.SUMMER -> {
                    p.phase += 0.04
                    p.alpha = (0.3f + (sin(p.phase) * 0.35f).toFloat()).coerceIn(0.05f, 0.95f)
                    p.x += p.speedX + (Math.random() * 0.3 - 0.15).toFloat()
                    p.y += p.speedY + (Math.random() * 0.3 - 0.15).toFloat()
                    if (p.x < -5) p.x = width.toFloat() + 5
                    if (p.x > width + 5) p.x = -5f
                    if (p.y < -5) p.y = height.toFloat() + 5
                    if (p.y > height + 5) p.y = -5f
                }

                Style.AUTUMN -> {
                    p.phase += 0.03
                    p.x += (sin(p.phase) * 0.8f).toFloat()
                    p.y += p.speedY
                    p.alpha += (Math.random() * 0.06 - 0.03).toFloat()
                    p.alpha = p.alpha.coerceIn(0.3f, 0.9f)
                    if (p.y > height + 10) {
                        p.y = -10f
                        p.x = (Math.random() * width).toFloat()
                    }
                }
            }
        }
    }

    fun render(graphics: GuiGraphicsExtractor) {
        particles.forEach { p ->
            val a = (p.alpha * 255).toInt().coerceIn(0, 255)
            val baseColor = (a shl 24) or (p.color and 0xFFFFFF)

            when (style) {

                Style.WINTER -> {
                    val size = p.size.toInt()
                    graphics.fill(p.x.toInt(), p.y.toInt(), p.x.toInt() + size, p.y.toInt() + size, baseColor)
                    val glowA = ((p.alpha * 0.3f * 255).toInt()).coerceIn(0, 255)
                    val glowColor = (glowA shl 24) or (p.color and 0xFFFFFF)
                    graphics.fill(p.x.toInt() - 1, p.y.toInt() - 1, p.x.toInt() + size + 1, p.y.toInt() + size + 1, glowColor)
                }

                Style.SPRING -> {
                    graphics.fill(p.x.toInt(), p.y.toInt(), p.x.toInt() + 1, p.y.toInt() + 5, baseColor)
                }

                Style.SUMMER -> {
                    val size = p.size.toInt().coerceAtLeast(1)
                    graphics.fill(p.x.toInt(), p.y.toInt(), p.x.toInt() + size, p.y.toInt() + size, baseColor)
                    val glowA = ((p.alpha * 0.25f * 255).toInt()).coerceIn(0, 255)
                    val glowColor = (glowA shl 24) or (p.color and 0xFFFFFF)
                    graphics.fill(p.x.toInt() - 2, p.y.toInt() - 2, p.x.toInt() + size + 2, p.y.toInt() + size + 2, glowColor)
                }

                Style.AUTUMN -> {
                    val size = p.size.toInt().coerceAtLeast(2)
                    graphics.fill(p.x.toInt(), p.y.toInt(), p.x.toInt() + size, p.y.toInt() + size, baseColor)
                }
            }
        }
    }

    data class Particle(
        var x: Float,
        var y: Float,
        val size: Float,
        val speedY: Float,
        val speedX: Float,
        var alpha: Float,
        val color: Int,
        var phase: Double
    )

    companion object {
        private val LEAF_COLORS = listOf(
            0xCC4400,
            0xDD6600,
            0xBB3300,
            0xEE8800,
            0xFFAA00,
            0x997700
        )
    }
}