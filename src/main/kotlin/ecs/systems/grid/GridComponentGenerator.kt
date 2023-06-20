package ecs.systems.grid

import base.util.Colors
import ecs.ECSController
import ecs.components.GridLockedComponent
import ecs.components.TransformComponent
import ecs.components.clickBox.ClickBoxComponent
import ecs.components.clickBox.RectangleClickBox
import ecs.components.mesh.FlatMeshComponent
import ecs.singletons.GridSettings
import ecs.components.mesh.OpenMeshComponent
import ecs.components.mesh.customTemplates.FlatCurvedBoxMesh
import ecs.components.mesh.customTemplates.FlatCustomCurveMesh
import ecs.components.mesh.customTemplates.FlatOuterCurveMesh
import org.joml.Vector2f

class GridComponentGenerator(controller: ECSController) {
    private val gridBackgroundID = controller.createEntity()
    private val gridBackgroundMesh = controller.assign<OpenMeshComponent>(gridBackgroundID)
    private val gridBackgroundTransform = controller.assign<TransformComponent>(gridBackgroundID)

    private val shadowID = controller.createEntity()
    private val shadowMesh = controller.assign<FlatMeshComponent>(shadowID)
    private val shadowGLC = controller.assign<GridLockedComponent>(shadowID).setWidth(1).setHeight(1)
    private val shadowTransform = controller.assign<TransformComponent>(shadowID)

    private val viewBoxID = controller.createEntity()
    private val viewClickBox = controller.assign<ClickBoxComponent>(viewBoxID)
    private val currentClickBox : RectangleClickBox? = null

    private lateinit var gridSettings : GridSettings

    fun createShadow(glc : GridLockedComponent){
        val width = glc.width
        val height = glc.height
        val blockShortening =  gridSettings.blockEdgeShorteningPercentage
        shadowMesh.addMesh(
                FlatCurvedBoxMesh(
                        Vector2f(-0.5f * width + blockShortening, 0.5f * height - blockShortening),
                        Vector2f(0.5f * width - blockShortening, -0.5f * height + blockShortening),
                        gridSettings.getCornerRadius() / gridSettings.blockSize, 3)
        )
        shadowMesh.setColor(1f,1f,1f,0.1f)
        shadowMesh.create()
        shadowMesh.depth = 0.05f
        shadowGLC.setHeight(height).setWidth(width)
        shadowTransform.setScale(0f)
    }
    fun hideShadow(){
        shadowTransform.setScale(0f)
    }
    fun clearShadow(){
        shadowMesh.clear()
    }
    fun showShadow(gLMouse : Vector2f){

        shadowTransform.setScale(gridSettings.getScale() * gridSettings.blockSize)

        val leftTop = shadowGLC.getGLCLeftTopIndex(gLMouse, gridSettings)
        val transform = shadowGLC.getGLCGirdTransform(leftTop, gridSettings)
        shadowTransform.setPosition(transform)
        if(gridSettings.canAddGLC(shadowGLC,leftTop.x, leftTop.y))
            shadowMesh.setColor(1f,1f,1f,0.1f)
        else shadowMesh.setColor(1f,0f,0f,0.1f)
    }

    fun setGridPosition(pos : Vector2f){
        gridBackgroundTransform.setPosition(pos)
    }
    fun generateGridBackground() {
        clearBackground()

        gridBackgroundTransform.setScale( gridSettings.getScale())
        setGridPosition(gridSettings.gridPosition)


        val zIndex = 1f //the lvl height index of the tiles or something (at least it's not that important because it's the height of only this openMesh, so it's not that important)
        val height = gridSettings.gridHeight
        val width = gridSettings.gridWidth

        val tempPerBlock =  2f / height //temporaryValue
        val borderSpacing = tempPerBlock * gridSettings.innerBorderEdgeShortening //pure visual, te amount of space between the walls and the tiles
        val borderWidth = gridSettings.borderWidth  //the total width of the wall (no the true visual with, that's borderEdgeWidth - borderSpacing)
        val blockSize = gridSettings.blockSize      //the size of a cube in the grid
        //val borderWidth = tempPerBlock * component.edgeWidthPercentage
        //val perBlock = tempPerBlock - (borderWidth * 2 / grid.size)
        val perBlockSpacing = blockSize * gridSettings.blockEdgeShorteningPercentage  //pure visual, the amount that the background will show through on the edge of every tile

        val mostRight = if(gridSettings.lockYaxis) blockSize * width / 2 else 1f - borderWidth
        val mostTop = if(gridSettings.lockYaxis) 1f - borderWidth else blockSize * height /2

        val cornerRadius = gridSettings.getCornerRadius()
        val nonRadBorder = borderWidth - cornerRadius

        //sides
        gridBackgroundMesh.addQuad(
            -mostRight - nonRadBorder, mostTop + borderWidth,
            mostRight + nonRadBorder, mostTop + borderSpacing, Colors.GRAY_NORMAL.get, zIndex
        )
        gridBackgroundMesh.addQuad(
            -mostRight - nonRadBorder, -mostTop - borderWidth,
            mostRight + nonRadBorder, -mostTop - borderSpacing, Colors.GRAY_NORMAL.get, zIndex
        )
        gridBackgroundMesh.addQuad(
            -mostRight - borderSpacing, mostTop + nonRadBorder,
            -mostRight - borderWidth, -mostTop - nonRadBorder, Colors.GRAY_NORMAL.get, zIndex
        )
        gridBackgroundMesh.addQuad(
            mostRight + borderSpacing, mostTop + nonRadBorder,
            mostRight + borderWidth, -mostTop - nonRadBorder, Colors.GRAY_NORMAL.get, zIndex
        )

        //outerCorners
        val outerRes = 5
        gridBackgroundMesh.addMesh(
            FlatOuterCurveMesh(
                Vector2f(-mostRight - nonRadBorder, mostTop + nonRadBorder),
                -90f, 0f, cornerRadius, outerRes
            ).setColor(Colors.GRAY_NORMAL.get), zIndex
        )
        gridBackgroundMesh.addMesh(
            FlatOuterCurveMesh(
                Vector2f(mostRight + nonRadBorder, mostTop + nonRadBorder),
                0f, 90f, cornerRadius, outerRes
            ).setColor(Colors.GRAY_NORMAL.get), zIndex
        )
        gridBackgroundMesh.addMesh(
            FlatOuterCurveMesh(
                Vector2f(mostRight + nonRadBorder, -mostTop - nonRadBorder),
                90f, 180f, cornerRadius, outerRes
            ).setColor(Colors.GRAY_NORMAL.get), zIndex
        )
        gridBackgroundMesh.addMesh(
            FlatOuterCurveMesh(
                Vector2f(-mostRight - nonRadBorder, -mostTop - nonRadBorder),
                180f, 270f, cornerRadius, outerRes
            ).setColor(Colors.GRAY_NORMAL.get), zIndex
        )

        //innerCorners
        val innerRes = 3
        gridBackgroundMesh.addMesh(
            FlatCustomCurveMesh(
                Vector2f(-mostRight + cornerRadius - borderSpacing, mostTop - cornerRadius + borderSpacing),
                Vector2f(-mostRight - borderSpacing, mostTop + borderSpacing),
                -90f,
                0f,
                cornerRadius,
                innerRes
            ).setColor(Colors.GRAY_NORMAL.get), zIndex
        )
        gridBackgroundMesh.addMesh(
            FlatCustomCurveMesh(
                Vector2f(mostRight - cornerRadius + borderSpacing, mostTop - cornerRadius + borderSpacing),
                Vector2f(mostRight + borderSpacing, mostTop + borderSpacing),
                0f,
                90f,
                cornerRadius,
                innerRes
            ).setColor(Colors.GRAY_NORMAL.get), zIndex
        )
        gridBackgroundMesh.addMesh(
            FlatCustomCurveMesh(
                Vector2f(mostRight - cornerRadius + borderSpacing, -mostTop + cornerRadius - borderSpacing),
                Vector2f(mostRight + borderSpacing, -mostTop - borderSpacing),
                90f,
                180f,
                cornerRadius,
                innerRes
            ).setColor(Colors.GRAY_NORMAL.get), zIndex
        )
        gridBackgroundMesh.addMesh(
            FlatCustomCurveMesh(
                Vector2f(-mostRight + cornerRadius - borderSpacing, -mostTop + cornerRadius - borderSpacing),
                Vector2f(-mostRight - borderSpacing, -mostTop - borderSpacing),
                180f,
                270f,
                cornerRadius,
                innerRes
            ).setColor(Colors.GRAY_NORMAL.get), zIndex
        )

        //for these blocks we don't need to take in account the scale, that's because its already getting scaled  by the background
        for (i in 0 until height) {
            val y=  mostTop - blockSize * i
            for (j in 0 until width) {
                val x = -mostRight + blockSize * j
                gridBackgroundMesh.addMesh(
                    FlatCurvedBoxMesh(
                        Vector2f( x + perBlockSpacing,y  - perBlockSpacing),
                        Vector2f(x + blockSize - perBlockSpacing, y - blockSize + perBlockSpacing),
                        cornerRadius, 3
                    ).setColor(Colors.GRAY_DARK.get), zIndex
                )
            }
        }
        gridBackgroundMesh.create()
        gridBackgroundMesh.depth = -1f
    }

    fun clearBackground(){
        if(currentClickBox != null ) viewClickBox.removeClickBox(currentClickBox)
        gridBackgroundMesh.clear()
    }

    fun setSettings(settings : GridSettings) : GridComponentGenerator{
        this.gridSettings = settings
        return this
    }
}