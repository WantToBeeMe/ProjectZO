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


    fun getGLCLeftTopIndex(gLMousePos: Vector2f, gridSettings : GridSettings) : Vector2i {
        val mostLeft = (-gridSettings.blockSize * gridSettings.gridWidth) / 2
        val mostTop = gridSettings.scale - gridSettings.borderWidth
        val newLeft = mostLeft + (gridSettings.blockSize/2)*(width-1)
        val newTop = mostTop - (gridSettings.blockSize/2)*(height-1)
        val gridPos = Vector2f( Maf.clamp(gLMousePos.x , newLeft, -newLeft- gridSettings.blockSize), Maf.clamp(gLMousePos.y , -newTop + gridSettings.blockSize, newTop) )

        val horizontalPercentage = (gridPos.x - newLeft) / (-mostLeft*2)
        val verticalPercentage = (gridPos.y - newTop) / (-mostTop*2)
        val relativeHorizontalIndex = (horizontalPercentage * gridSettings.gridWidth +0.00001f).toInt()//this is not the real horizontal index, the amount of indexes gets decided by the amount of possible horizontal arrangement of this size block
        val relativeVerticalIndex = (verticalPercentage * gridSettings.gridHeight +0.00001f).toInt()      //this is not the real vertical index, the amount of indexes gets decided by the amount of possible vertical arrangement of this size block

        return Vector2i(relativeHorizontalIndex, relativeVerticalIndex)
    }
    fun getGLCGirdTransform(leftTopIndex: Vector2i, gridSettings : GridSettings) : Vector2f {
        val mostLeft = (-gridSettings.blockSize * gridSettings.gridWidth) / 2
        val mostTop = gridSettings.scale - gridSettings.borderWidth
        return Vector2f(mostLeft+ (gridSettings.blockSize/2)*width + gridSettings.blockSize*leftTopIndex.x, mostTop - (gridSettings.blockSize/2)*height - gridSettings.blockSize*leftTopIndex.y)
    }

}