package ecs.systems.grid

import base.input.Mouse
import base.util.Colors
import base.util.Maf
import ecs.ECSController
import ecs.components.GridLockedComponent
import ecs.components.TransformComponent
import ecs.components.mesh.FlatMeshComponent
import ecs.singletons.GridSettings
import ecs.components.mesh.OpenMeshComponent
import ecs.components.mesh.customTemplates.FlatCurvedBoxMesh
import ecs.components.mesh.customTemplates.FlatCustomCurveMesh
import ecs.components.mesh.customTemplates.FlatOuterCurveMesh
import org.joml.Vector2f

class GridMeshGenerator(controller: ECSController) {
    private val gridBackgroundID = controller.createEntity()
    private val gridBackgroundMesh = controller.assign<OpenMeshComponent>(gridBackgroundID)
    private val gridBackgroundTransform = controller.assign<TransformComponent>(gridBackgroundID)

    private val shadowID = controller.createEntity()
    private val shadowMesh = controller.assign<FlatMeshComponent>(shadowID)
    private val shadowGLC = controller.assign<GridLockedComponent>(shadowID).setWidth(1).setHeight(1)
    private val shadowTransform = controller.assign<TransformComponent>(shadowID)

    private lateinit var gridSettings : GridSettings

    fun createShadow(glc : GridLockedComponent){
        val width = glc.width
        val height = glc.height
        val blockSize = gridSettings.blockSize
        val perBlockSpacing = blockSize * gridSettings.blockSpacingFactor
        shadowMesh.addMesh(
            FlatCurvedBoxMesh(
                Vector2f( -(blockSize/2)*width + perBlockSpacing, (blockSize/2)*height  - perBlockSpacing),
                Vector2f((blockSize/2)*width - perBlockSpacing, -(blockSize/2)*height  + perBlockSpacing),
                gridSettings.borderWidth* 0.48f, 3)
        )
        shadowMesh.setColor(1f,1f,1f,0.1f).setVisualClickBox(false)
        shadowMesh.create()
        shadowMesh.depth = 0.05f
        shadowGLC.setHeight(height).setWidth(width)
        shadowTransform.setScale(0f)
    }
    fun hideShadow(){
        shadowMesh.clear()
    }
    fun showShadow(gLMouse : Vector2f){
        shadowTransform.setScale(1f)
        val leftTop = shadowGLC.getGLCLeftTopIndex(gLMouse, gridSettings)
        val transform = shadowGLC.getGLCGirdTransform(leftTop, gridSettings)
        shadowTransform.setPosition(transform)
        if(gridSettings.canAddGLC(shadowGLC,leftTop.x, leftTop.y))
            shadowMesh.setColor(1f,1f,1f,0.1f)
        else shadowMesh.setColor(1f,0f,0f,0.1f)
    }

    fun generateGridBackground() {
        gridBackgroundTransform.setScale(gridSettings.scale)
        val zIndex = 1f //the lvl height index of the tiles or something (at least it's not that important because it's the height of only this openMesh, so it's not that important)
        val height = gridSettings.gridHeight
        val width = gridSettings.gridWidth

        val tempPerBlock =  2f / height //temporaryValue
        val borderSpacing = tempPerBlock * gridSettings.edgeSpacingFactor //pure visual, te amount of space between the walls and the tiles
        val borderWidth = gridSettings.borderWidth  //the total width of the wall (no the true visual with, that's borderEdgeWidth - borderSpacing)
        val blockSize = gridSettings.blockSize      //the size of a cube in the grid
        //val borderWidth = tempPerBlock * component.edgeWidthPercentage
        //val perBlock = tempPerBlock - (borderWidth * 2 / grid.size)
        val perBlockSpacing = blockSize * gridSettings.blockSpacingFactor  //pure visual, the amount that the background will show through on the edge of every tile

        val mostRight = blockSize * width / 2
        val mostTop = 1f - borderWidth
        val cornerRadius =
            if (borderWidth * gridSettings.cornerPercentage > borderWidth - borderSpacing) borderWidth - borderSpacing else borderWidth * gridSettings.cornerPercentage
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


        for (i in 0 until height) {
            for (j in 0 until width) {
                gridBackgroundMesh.addMesh(
                    FlatCurvedBoxMesh(
                        Vector2f(-mostRight + blockSize * j + perBlockSpacing, mostTop - blockSize * i - perBlockSpacing),
                        Vector2f(
                            -mostRight + blockSize * j + blockSize - perBlockSpacing,
                            mostTop - blockSize * (i + 1) + perBlockSpacing
                        ),
                        cornerRadius, 3
                    ).setColor(Colors.GRAY_DARK.get), zIndex
                )
            }
        }
        gridBackgroundMesh.create()
        gridBackgroundMesh.depth = -1f
    }

    fun clearBackground(){
        gridBackgroundMesh.clear()
    }

    fun setSettings(settings : GridSettings) : GridMeshGenerator{
        this.gridSettings = settings
        return this
    }
}