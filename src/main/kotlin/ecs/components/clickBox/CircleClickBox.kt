package ecs.components.clickBox

import org.joml.Vector2f

class CircleClickBox(private val center: Vector2f, private val radius : Float) : IClickBox {
    override fun isInside(point: Vector2f): Boolean {
        return center.distance(point) < radius
    }

    override fun getBoxOutline(): Pair<FloatArray, IntArray> {
        return Pair(
            floatArrayOf(),
            intArrayOf()
        )
    }
}