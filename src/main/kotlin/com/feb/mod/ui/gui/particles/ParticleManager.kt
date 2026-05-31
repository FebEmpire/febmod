package com.feb.mod.ui.gui.particles

import net.minecraft.client.gui.GuiGraphicsExtractor

/*
* +rep @februari10
* These are like tuff particles yk
* I can make more stuff in the future not just snow
* Basically a copy paste from private FebMod
*/

class ParticleManager {

    private val particles = mutableListOf<Particle>()

    fun initialize(width: Int, height: Int) {
        particles.clear()
        repeat(50) {
            particles.add(Particle(
                x      = (Math.random() * width).toFloat(),
                y      = (Math.random() * height).toFloat(),
                size   = (Math.random() * 2.5 + 0.5).toFloat(),
                speedY = (Math.random() * 0.8 + 0.4).toFloat(),
                speedX = (Math.random() * 0.6 - 0.3).toFloat(),
                alpha  = (Math.random() * 0.5 + 0.4).toFloat(),
                color  = if (Math.random() > 0.7) 0xE0F0FF else 0xFFFFFF
            ))
        }
    }

    fun update(width: Int, height: Int) {
        particles.forEach { p ->
            p.y += p.speedY
            p.x += p.speedX
            p.alpha += (Math.random() * 0.08 - 0.04).toFloat()
            p.alpha = p.alpha.coerceIn(0.3f, 0.9f)
            if (p.y > height + 10) { p.y = -10f; p.x = (Math.random() * width).toFloat() }
            if (p.x < -10) p.x = width.toFloat() + 10
            if (p.x > width + 10) p.x = -10f
        }
    }

    fun render(graphics: GuiGraphicsExtractor) {
        particles.forEach { p ->
            val color = (p.color and 0xFFFFFF) or ((p.alpha * 255).toInt() shl 24)
            val size = p.size.toInt()
            graphics.fill(p.x.toInt(), p.y.toInt(), p.x.toInt() + size, p.y.toInt() + size, color)
            val glowColor = (p.color and 0xFFFFFF) or (((p.alpha * 0.3 * 255).toInt()) shl 24)
            graphics.fill(p.x.toInt() - 1, p.y.toInt() - 1, p.x.toInt() + size + 1, p.y.toInt() + size + 1, glowColor)
        }
    }

    data class Particle(
        var x: Float, var y: Float,
        val size: Float,
        val speedY: Float, val speedX: Float,
        var alpha: Float,
        val color: Int
    )
}