package ecs.components

import base.util.Maf
import org.joml.Vector2f
import org.joml.Vector2i

class GridComponent {
    var edgeWidthPercentage = 0.3f
        private set
    var screenHeight = 0.9f
        private set

    var grid : Array<BooleanArray> = arrayOf(BooleanArray(1))
        private set
    var occupationGird :  Array<IntArray> =  arrayOf(IntArray(1) {-1})
            private set


    fun setGrid(width: Int, height: Int) : GridComponent{
        return setGrid ( Array(height) { BooleanArray(width) { true } } )
    }
    fun setGrid(g : Array<BooleanArray>) : GridComponent{
        grid = g
        reCalculateAttributes()
        return this
    }
    fun setScreenHeight(height : Float) : GridComponent{
        screenHeight = if(height > 0) height else 0f
        reCalculateAttributes()
        return this
    }
    fun setEdgeWidthPercentage( perc : Float ) : GridComponent{
        edgeWidthPercentage = if(perc > 0) {
            if(perc <= 1) perc
            else 1f
        }else 0f
        reCalculateAttributes()
        return this
    }

    var borderWidth = (screenHeight * 2 / grid.size) * edgeWidthPercentage
        private set
    var blockSize = (screenHeight * 2 / grid.size) - (borderWidth * 2 / grid.size)
        private set

    private fun reCalculateAttributes(){
        val tempValue = screenHeight * 2 / grid.size
        borderWidth = tempValue * edgeWidthPercentage //the total width of the wall (no the true visual with, that's borderEdgeWidth - borderSpacing)
        blockSize = tempValue - (borderWidth * 2 / grid.size) //the size of a cube in the grid
        occupationGird = Array(grid.size) { IntArray(grid[0].size) {-1} }
    }


    fun canAddGLC(comp : GridLockedComponent, leftIndex : Int, topIndex: Int) : Boolean{ //it's the left- Top-Index because some gridLockedComponents can have a bigger size then 1
        if(occupationGird.size < topIndex+comp.height) return false
        if(occupationGird[0].size < leftIndex+comp.width) return false

        for(horIndex in leftIndex until leftIndex+comp.width){
            for(verIndex in topIndex until topIndex+comp.height){
                if(!grid[verIndex][horIndex]) return false
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

    //todo: these 2 functions dont take in account the transformation of the camera, like scale and translation (as specially the translation is inportant)

    fun getGLCLeftTopIndex(gLPos: Vector2f, comp: GridLockedComponent ) : Vector2i {
        val mostLeft = (-blockSize * grid[0].size) / 2
        val mostTop = screenHeight - borderWidth
        val newLeft = mostLeft + (blockSize/2)*(comp.width-1)
        val newTop = mostTop - (blockSize/2)*(comp.height-1)
        val gridPos = Vector2f( Maf.clamp(gLPos.x , newLeft, -newLeft- blockSize), Maf.clamp(gLPos.y , -newTop + blockSize, newTop) )

        val horizontalPercentage = (gridPos.x - newLeft) / (-mostLeft*2)
        val verticalPercentage = (gridPos.y - newTop) / (-mostTop*2)
        val relativeHorizontalIndex = (horizontalPercentage * grid[0].size +0.00001f).toInt()//this is not the real horizontal index, the amount of indexes gets decided by the amount of possible horizontal arrangement of this size block
        val relativeVerticalIndex = (verticalPercentage * grid.size  +0.00001f).toInt()      //this is not the real vertical index, the amount of indexes gets decided by the amount of possible vertical arrangement of this size block

        return Vector2i(relativeHorizontalIndex, relativeVerticalIndex)
    }
    fun getGLCGirdTransform(leftTop: Vector2i, comp: GridLockedComponent ) : Vector2f {
        val mostLeft = (-blockSize * grid[0].size) / 2
        val mostTop = screenHeight - borderWidth
        return Vector2f(mostLeft+ (blockSize/2)*comp.width + blockSize*leftTop.x, mostTop - (blockSize/2)*comp.height - blockSize*leftTop.y)
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