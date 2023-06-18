package ecs.components

import base.util.Maf
import ecs.singletons.GridSettings
import org.joml.Vector2f
import org.joml.Vector2i

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

    fun getGLCLeftTopIndex(gLMousePos: Vector2f, gs : GridSettings) : Vector2i {
        val zoom = gs.getScale()
        val scaledBlockSize = zoom * gs.blockSize
        // getting the absolute left/top position of the grid blocks (so without the border)
        val mostLeft = (if(gs.lockYaxis) -(scaledBlockSize * gs.gridWidth) / 2 else (-1f + gs.borderWidth) * zoom)
        val mostTop = (if(gs.lockYaxis) (1f - gs.borderWidth) * zoom else (scaledBlockSize * gs.gridHeight) / 2)

        //when it goes over every full number, the index should be increased, the newLeft and newTop make sure that this border between indexes is always in the center of placements
        val newLeft = mostLeft + (scaledBlockSize/2)*(width-1)
        val newTop = mostTop - (scaledBlockSize/2)*(height-1)
        val gridPos = Vector2f(
                Maf.clamp(gLMousePos.x- gs.gridPosition.x , newLeft, -newLeft- scaledBlockSize) ,
                Maf.clamp(gLMousePos.y- gs.gridPosition.y , -newTop + scaledBlockSize, newTop) )

        val horizontalPercentage = (gridPos.x - newLeft) / (-mostLeft*2)
        val verticalPercentage = (gridPos.y - newTop) / (-mostTop*2)

        // it's a relative index, because blocks that are bigger then 1x1 cant have 1 index, but this relative index is just the most left and most top index
        val relativeHorizontalIndex = (horizontalPercentage * gs.gridWidth +0.00001f).toInt()
        val relativeVerticalIndex = (verticalPercentage * gs.gridHeight +0.00001f).toInt()

        return Vector2i(relativeHorizontalIndex, relativeVerticalIndex)
    }
    fun getGLCGirdTransform(leftTopIndex: Vector2i, gs : GridSettings) : Vector2f {
        val zoom = gs.getScale()
        val scaledBlockSize = gs.blockSize * zoom
        // getting the absolute left/top position of the grid blocks (so without the border)
        val mostLeft = (if(gs.lockYaxis) -(scaledBlockSize * gs.gridWidth) / 2 else (-1f + gs.borderWidth) * zoom)
        val mostTop = (if(gs.lockYaxis) (1f - gs.borderWidth) * zoom else (scaledBlockSize * gs.gridHeight) / 2)

        //calculating the exact position the block should be placed by:
        // 1. taking the most outer position
        // 2. moving the block to the appropriate index
        // 3. center the block (because the position is from the center, not the left/top)
        // 4. adding the translation of the grid itself
        //                  1                      2                          3                         4
        val transformX = mostLeft + scaledBlockSize*leftTopIndex.x + (scaledBlockSize/2)*width + gs.gridPosition.x
        val transformY = mostTop - scaledBlockSize*leftTopIndex.y - (scaledBlockSize/2)*height + gs.gridPosition.y
        return Vector2f(transformX, transformY)
    }

}