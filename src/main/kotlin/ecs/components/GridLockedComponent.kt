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

    //todo: these 2 functions dont take in account the transformation of the camera, like scale and translation (as specially the translation is inportant)


    fun getGLCLeftTopIndex(gLMousePos: Vector2f, gs : GridSettings) : Vector2i {
        val scaledBlockSize = gs.blockSize * gs.scale
        val mostLeft = -(scaledBlockSize * gs.gridWidth) / 2
        val mostTop = gs.scale - gs.borderWidth * gs.scale

        val newLeft = mostLeft + (scaledBlockSize/2)*(width-1)
        val newTop = mostTop - (scaledBlockSize/2)*(height-1)
        val gridPos = Vector2f( Maf.clamp(gLMousePos.x , newLeft, -newLeft- scaledBlockSize), Maf.clamp(gLMousePos.y , -newTop + scaledBlockSize, newTop) )

        val horizontalPercentage = (gridPos.x - newLeft) / (-mostLeft*2)
        val verticalPercentage = (gridPos.y - newTop) / (-mostTop*2)
        val relativeHorizontalIndex = (horizontalPercentage * gs.gridWidth +0.00001f).toInt()//this is not the real horizontal index, the amount of indexes gets decided by the amount of possible horizontal arrangement of this size block
        val relativeVerticalIndex = (verticalPercentage * gs.gridHeight +0.00001f).toInt()      //this is not the real vertical index, the amount of indexes gets decided by the amount of possible vertical arrangement of this size block

        return Vector2i(relativeHorizontalIndex, relativeVerticalIndex)
    }
    fun getGLCGirdTransform(leftTopIndex: Vector2i, gs : GridSettings) : Vector2f {
        val scaledBlockSize = gs.blockSize * gs.scale
        val mostLeft = -(scaledBlockSize * gs.gridWidth) / 2
        val mostTop = gs.scale - gs.borderWidth * gs.scale
        return Vector2f(mostLeft+ (scaledBlockSize/2)*width + scaledBlockSize*leftTopIndex.x,
            mostTop - (scaledBlockSize/2)*height - scaledBlockSize*leftTopIndex.y)
    }

}