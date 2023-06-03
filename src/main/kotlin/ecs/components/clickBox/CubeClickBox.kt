package ecs.components.clickBox

import org.joml.Vector2f

class CubeClickBox(private val leftTop: Vector2f, private val rightBot : Vector2f) : IClickBox {
    override fun isInside(point: Vector2f): Boolean {
        return point.x >= leftTop.x && point.x <= rightBot.x &&
                point.y >= rightBot.y && point.y <= leftTop.y
    }

    override fun getBoxOutline(): Pair<FloatArray, IntArray> {
        return Pair(
            floatArrayOf(leftTop.x,leftTop.y   ,rightBot.x,leftTop.y    ,rightBot.x, rightBot.y,      leftTop.x,rightBot.y),
            intArrayOf(0,1  ,1,2   ,2,3   ,3,0   ,0,2   ,1,3)
        )
    }
}