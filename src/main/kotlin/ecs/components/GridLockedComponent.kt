package ecs.components

class GridLockedComponent {

    var width  = 1
        private set
    var height = 1
        private set

    fun setWidth(w : Int) : GridLockedComponent{
        width = w
        return this
    }
    fun setHeight(h : Int) : GridLockedComponent{
        height = h
        return this
    }
}