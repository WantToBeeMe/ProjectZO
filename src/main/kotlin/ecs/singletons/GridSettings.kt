package ecs.singletons

import ecs.components.GridLockedComponent
import org.joml.Vector2f
import org.joml.Vector2i

class GridSettings(val gridWidth : Int = 1, val gridHeight : Int = 1) {
    val innerBorderEdgeShortening : Float = 0.08f
    val cornerPercentage : Float = 0.48f
    val blockEdgeShorteningPercentage : Float = 0.05f

    var borderWidthPercentage = 0.3f
        private set
    var zoom = 0.98f
        private set


    //first height,  second width
    private var occupationGird :  Array<IntArray> =  Array(gridHeight) { IntArray(gridWidth) {-1} }
    fun getOccupationGird() : Array<IntArray>{
        return occupationGird.clone()
    }

    var borderWidth = (2f / gridHeight) * borderWidthPercentage
        private set
    var blockSize = (2f /gridHeight) - (borderWidth * 2 / gridHeight)
        private set

    var viewBoxLeftTop = Vector2f(-1f, 1f)
        private set
    var viewBoxRightBot = Vector2f(1f , -1f)
        private set
    var gridPosition = Vector2f(0f )
        private set
    var lockYaxis = true
        private set

    fun setPosition(newPos : Vector2f){
        gridPosition = newPos
    }

     fun setViewBox(leftTop: Vector2f, rightBot : Vector2f ) : GridSettings {
        viewBoxLeftTop = leftTop
        viewBoxRightBot = rightBot
        reCalculateAttributes()
        return this
    }
    fun setLocKYaxis(value : Boolean) : GridSettings {
        lockYaxis = value
        reCalculateAttributes()
        return this
    }

    fun setScale(s : Float) : GridSettings {
        zoom = if(s > 0) s else 0f
        reCalculateAttributes()
        return this
    }
    fun setBorderWidthPercentage(percentage : Float ) : GridSettings {
        borderWidthPercentage = if(percentage > 0) {
            if(percentage <= 1) percentage
            else 1f
        }else 0f
        reCalculateAttributes()
        return this
    }


    fun getScale() : Float{
        return if(lockYaxis) (viewBoxLeftTop.y-viewBoxRightBot.y) * zoom /2
        else (viewBoxRightBot.x-viewBoxLeftTop.x) * zoom /2
    }
    fun getCornerRadius() : Float{
        val borderSpacing = 2f / gridHeight  * innerBorderEdgeShortening
        return if (borderWidth * cornerPercentage > borderWidth - borderSpacing) borderWidth - borderSpacing
            else borderWidth * cornerPercentage
    }
    fun getCenterViewBox() : Vector2f{
        return Vector2f(viewBoxLeftTop.x + viewBoxRightBot.x , viewBoxLeftTop.y + viewBoxRightBot.y).mul(0.5f)
    }


    private fun reCalculateAttributes(){
        val tempValue = if(lockYaxis) 2f / gridHeight else  2f / gridWidth
        borderWidth = tempValue * borderWidthPercentage //the total width of the wall (no the true visual with, that's borderEdgeWidth - borderSpacing)
        blockSize = if(lockYaxis) tempValue - (borderWidth * 2 / gridHeight) else tempValue - (borderWidth * 2 / gridWidth) //the size of a cube in the grid
        gridPosition = getCenterViewBox()
    }

    fun canAddGLC(comp : GridLockedComponent, leftIndex : Int, topIndex: Int) : Boolean{ //it's the left- Top-Index because some gridLockedComponents can have a bigger size then 1
        if(occupationGird.size < topIndex+comp.height) return false
        if(occupationGird[0].size < leftIndex+comp.width) return false

        for(horIndex in leftIndex until leftIndex+comp.width){
            for(verIndex in topIndex until topIndex+comp.height){
                if(occupationGird[verIndex][horIndex] != -1) return false
            }
        }
        return true
    }
    fun addGLC(id: Int, comp : GridLockedComponent, leftIndex : Int, topIndex: Int){
        if(!canAddGLC(comp, leftIndex, topIndex)) return
        for(horIndex in leftIndex until leftIndex+comp.width) {
            for (verIndex in topIndex until topIndex + comp.height) {
                occupationGird[verIndex][horIndex] = id
            }
        }
    }
    //returns the old left- Top-index that is now removed
    fun removeGLC(id: Int) : Vector2i{
        var oldIndex = Vector2i(-1)
        for(i in occupationGird.indices){
            for(j in 0 until occupationGird[0].size){
                if(occupationGird[i][j] == id) {
                    if(oldIndex.x == -1) oldIndex =  Vector2i(j, i)
                    occupationGird[i][j] = -1
                }
            }
        }
        return oldIndex
    }




    override fun toString(): String {
        var visual = ""
        for(verticalIndex in occupationGird.indices){
            for(horizontalIndex in 0 until  occupationGird[0].size ){
                visual += "${occupationGird[verticalIndex][horizontalIndex]}, "
            }
            visual += "\n"
        }
        return visual
    }


}