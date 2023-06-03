package ecs.components.clickBox

import org.joml.Vector2f

interface IClickBox {
    fun isInside(point : Vector2f) : Boolean

    fun getBoxOutline() : Pair<FloatArray, IntArray>
}