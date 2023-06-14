package base.util

import ecs.components.TransformComponent
import org.joml.Vector2f
import kotlin.math.cos
import kotlin.math.sin

object Maf {

    fun clamp(value: Float, min: Float, max: Float): Float {
        if (value < min) {
            return min
        } else if (value > max) {
            return max
        }
        return value
    }
    fun clamp(value: Int, min: Int, max: Int): Int {
        if (value < min) {
            return min
        } else if (value > max) {
            return max
        }
        return value
    }

    fun pixelToGLCords(pixelX: Float, pixelY: Float, aspect : Float = 1f): Vector2f {
        val x = ((pixelX / Window.getWidth()) * 2f - 1f) * aspect
        val y = -(pixelY / Window.getHeight()) * 2f + 1f
        return Vector2f(x,y)
    }

    fun revertTransform(point: Vector2f, transform: TransformComponent? ): Vector2f {
        if(transform == null) return point
        //translation
        var newPoint = Vector2f(point).add(Vector2f(transform.getPosition()).mul(-1f))
        //rotate
        val angleRad = transform.getRotation(true)
        val cos = cos(angleRad.toDouble()).toFloat()
        val sin = sin(angleRad.toDouble()).toFloat()
        newPoint = Vector2f(newPoint.x * cos - newPoint.y * sin,  newPoint.x * sin + newPoint.y * cos)
        //scale
        return newPoint.mul(Vector2f(1f).div(transform.getScale()))
    }

}