package ecs.components.hitbox

import org.joml.Vector2f

interface IHitBox {
    fun isInside(point : Vector2f) : Boolean
}