package ecs.components.hitbox

import org.joml.Vector2f

class HitBoxComponent {
    private val hitBoxes : MutableList<IHitBox> = mutableListOf()

    fun addHitBox(box : IHitBox){
        hitBoxes.add(box)
    }
    fun removeHitBox(box : IHitBox) : Boolean{
        return hitBoxes.remove(box)
    }

    fun isInside(point: Vector2f): Boolean {

        for(box in hitBoxes)
            if( box.isInside(point) )
                return true


        return false
    }
}