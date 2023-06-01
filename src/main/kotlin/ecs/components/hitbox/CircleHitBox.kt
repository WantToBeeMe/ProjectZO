package ecs.components.hitbox

import org.joml.Vector2f

class CircleHitBox(private val center: Vector2f, private val radius : Float) : IHitBox {
    override fun isInside(point: Vector2f): Boolean {
        return center.distance(point) < radius
    }
}