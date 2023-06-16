package ecs.singletons

import base.util.Maf
import ecs.components.GridLockedComponent
import org.joml.Vector2f
import org.joml.Vector2i

class GridSettings {
    val edgeSpacingFactor : Float = 0.08f
    val cornerPercentage : Float = 0.48f
    val blockSpacingFactor : Float = 0.05f

    var borderWidthPercentage = 0.3f
        private set
    var scale = 0.9f
        private set

    var gridWidth = 1
    var gridHeight = 1

    private var occupationGird :  Array<IntArray> =  arrayOf(IntArray(1) {-1})

    var borderWidth = (2f / gridHeight) * borderWidthPercentage
        private set
    var blockSize = (2f /gridHeight) - (borderWidth * 2 / gridHeight)
        private set

    fun setGrid(width: Int, height: Int) : GridSettings {
        this.gridWidth = width
        this.gridHeight= height
        reCalculateAttributes()
        return this
    }
    fun setScale(s : Float) : GridSettings {
        scale = if(s > 0) s else 0f
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

    private fun reCalculateAttributes(){
        val tempValue = 2f / gridHeight
        borderWidth = tempValue * borderWidthPercentage //the total width of the wall (no the true visual with, that's borderEdgeWidth - borderSpacing)
        blockSize = tempValue - (borderWidth * 2 / gridHeight) //the size of a cube in the grid
        occupationGird = Array(gridHeight) { IntArray(gridWidth) {-1} }
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
        for(horIndex in leftIndex until leftIndex+comp.width){
            for(verIndex in topIndex until topIndex+comp.height){
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