package ecs.components.clickBox

import org.joml.Vector2f

class ClickBoxComponent {
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
    var priority = 0

    fun addClickBox(box : IClickBox) : ClickBoxComponent{
        clickBoxes.add(box)
        return this
    }
    fun removeClickBox(box : IClickBox) : Boolean {
        return clickBoxes.remove(box)
    }

    fun setOnEnter(event :  ((Vector2f) -> Unit)? ) : ClickBoxComponent{
        onEnterEvent = event
        return this
    }
    fun setOnLeave(event :  ((Vector2f) -> Unit)?): ClickBoxComponent{
        onLeaveEvent = event
        return this
    }
    fun setOnClick(event :  ((Vector2f, Int) -> Unit)?) : ClickBoxComponent{
        onClickEvent = event
        return this
    }
    fun setOnRelease(event :  ((Vector2f, Int) -> Unit)?) : ClickBoxComponent{
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