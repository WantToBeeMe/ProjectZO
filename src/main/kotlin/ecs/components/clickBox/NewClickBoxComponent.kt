package ecs.components.clickBox

import ecs.components.TransformComponent
import org.joml.Vector2f
import org.lwjgl.opengl.GL45.*
import kotlin.math.cos
import kotlin.math.sin

class NewClickBoxComponent {
    private val clickBoxes : MutableList<IClickBox> = mutableListOf()

    var onEnterEvent: (( Vector2f) -> Unit)? = null
        private set
    var onLeaveEvent: (( Vector2f) -> Unit)? = null
        private set
    var onClickEvent: (( Vector2f, Int) -> Unit)? = null
        private set
    var onReleaseEvent: (( Vector2f, Int) -> Unit)? = null
        private set

    var hovering = false

    fun addClickBox(box : IClickBox) : NewClickBoxComponent{
        clickBoxes.add(box)
        return this
    }
    fun removeClickBox(box : IClickBox) : Boolean {
        return clickBoxes.remove(box)
    }

    fun setOnEnter(event :  ((Vector2f) -> Unit)? ) : NewClickBoxComponent{
        onEnterEvent = event
        return this
    }
    fun setOnLeave(event :  ((Vector2f) -> Unit)?): NewClickBoxComponent{
        onLeaveEvent = event
        return this
    }
    fun setOnClick(event :  ((Vector2f, Int) -> Unit)?) : NewClickBoxComponent{
        onClickEvent = event
        return this
    }
    fun setOnRelease(event :  ((Vector2f, Int) -> Unit)?) : NewClickBoxComponent{
        onReleaseEvent = event
        return this
    }

    fun isInside(point: Vector2f): Boolean {
        for(box in clickBoxes)
            if( box.isInside(point) )
                return true
        return false
    }
}