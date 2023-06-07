package ecs.components

class GridComponent {
    var edgeWidthPercentage = 0.3f
        private set
    var screenHeight = 0.9f
        private set

    var grid : Array<BooleanArray> = arrayOf(BooleanArray(1))
        private set

    fun setGrid(width: Int, height: Int) : GridComponent{
        return setGrid ( Array(height) { BooleanArray(width) { true } } )
    }
    fun setGrid(g : Array<BooleanArray>) : GridComponent{
        grid = g
        return this
    }
    fun setScreenHeight(height : Float) : GridComponent{
        screenHeight = if(height > 0) height else 0f
        return this
    }
    fun setEdgeWidthPercentage( perc : Float ) : GridComponent{
        edgeWidthPercentage = if(perc > 0) {
            if(perc <= 1) perc
            else 1f
        }else 0f
        return this
    }


}