package ecs.components.clickBox

import org.joml.Vector2f
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.PI

class CircleClickBox(private val center: Vector2f, private val radius: Float) : IClickBox {
    override fun isInside(point: Vector2f): Boolean {
        return center.distance(point) < radius
    }

    override fun getBoxOutline(): Pair<FloatArray, IntArray> {
        val resolution = 8
        val vertices = FloatArray(2 + (resolution*2))
        val lines = IntArray(2*resolution*2)
        var vertexIndex = 0
        var lineIndex= 0
        val angleIncrement = (2 * Math.PI / resolution)
        var angle = 0.0

        vertices[vertexIndex++] = center.x
        vertices[vertexIndex++] = center.y

        for (i in 0 until resolution) {
            vertices[vertexIndex++] = center.x + radius * cos(angle).toFloat()
            vertices[vertexIndex++] = center.y + radius * sin(angle).toFloat()
            val currentIndex = (vertexIndex/2)-1
            lines[lineIndex++] = currentIndex
            lines[lineIndex++] = 0
            lines[lineIndex++] = currentIndex
            lines[lineIndex++] = currentIndex % resolution +1

            angle += angleIncrement
        }
        return Pair(vertices, lines)
    }
}
