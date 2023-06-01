package ecs.components.hitbox

import org.joml.Vector2f

class BoxHitBox(private val leftTop: Vector2f, private val rightBot : Vector2f) : IHitBox {
    override fun isInside(point: Vector2f): Boolean {
        return point.x >= leftTop.x && point.x <= rightBot.x &&
                point.y >= rightBot.y && point.y <= leftTop.y
    }
}