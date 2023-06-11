package base.util

import org.joml.Vector2f

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

}