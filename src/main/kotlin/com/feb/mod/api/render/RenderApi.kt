package com.feb.mod.api.render

import net.minecraft.core.BlockPos
import net.minecraft.gizmos.GizmoStyle
import net.minecraft.gizmos.Gizmos
import net.minecraft.util.ARGB
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import java.awt.Color

object RenderApi {

    fun drawBlockPos(
        pos: BlockPos,
        color: Color,
        esp: Boolean = false
    ) {
        val box = AABB(
            pos.x.toDouble(),
            pos.y.toDouble(),
            pos.z.toDouble(),
            pos.x + 1.0,
            pos.y + 1.0,
            pos.z + 1.0
        )

        drawBox(box, color, esp)
    }

    fun drawBox(
        box: AABB,
        color: Color,
        esp: Boolean = false
    ) {
        val strokeColor = ARGB.color(
            color.alpha,
            color.red,
            color.green,
            color.blue
        )

        val fillColor = ARGB.color(
            150,
            color.red,
            color.green,
            color.blue
        )

        val style = GizmoStyle.strokeAndFill(
            strokeColor,
            2.5f,
            fillColor
        )

        val props = Gizmos.cuboid(box, style)

        if (esp) {
            props.setAlwaysOnTop()
        }
    }

    fun drawLine(
        start: Vec3,
        end: Vec3,
        color: Color,
        esp: Boolean = false,
        thickness: Float = 1f
    ) {
        val argbColor = ARGB.color(
            color.alpha,
            color.red,
            color.green,
            color.blue
        )

        val props = Gizmos.line(
            start,
            end,
            argbColor,
            thickness
        )

        if (esp) {
            props.setAlwaysOnTop()
        }
    }
}