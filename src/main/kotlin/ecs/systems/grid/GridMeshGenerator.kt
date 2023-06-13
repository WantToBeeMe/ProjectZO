package ecs.systems.grid

import base.util.Colors
import ecs.ECSController
import ecs.singletons.GridSettings
import ecs.components.mesh.OpenMeshComponent
import ecs.components.mesh.customTemplates.FlatCurvedBoxMesh
import ecs.components.mesh.customTemplates.FlatCustomCurveMesh
import ecs.components.mesh.customTemplates.FlatOuterCurveMesh
import org.joml.Vector2f

class GridMeshGenerator(controller: ECSController, private val settings : GridSettings, private val edgeSpacingFactor : Float = 0.08f, private val  cornerPercentage : Float = 0.48f, private val blockSpacingFactor : Float = 0.05f ) {
    private val backgroundID = controller.createEntity()
    private val backgroundMesh = controller.assign<OpenMeshComponent>(backgroundID)

    init {
        genGridTheMesh()
    }

    private fun genGridTheMesh() {
        val zIndex = 1f //the lvl height index of the tiles or something (at least it's not that important because it's the height of only this openMesh, so it's not that important)
        val height = settings.height
        val width = settings.width

        val tempPerBlock = settings.screenHeight * 2 / height //temporaryValue
        val borderSpacing = tempPerBlock * edgeSpacingFactor //pure visual, te amount of space between the walls and the tiles
        val borderWidth = settings.borderWidth  //the total width of the wall (no the true visual with, that's borderEdgeWidth - borderSpacing)
        val blockSize = settings.blockSize      //the size of a cube in the grid
        //val borderWidth = tempPerBlock * component.edgeWidthPercentage
        //val perBlock = tempPerBlock - (borderWidth * 2 / grid.size)
        val perBlockSpacing = blockSize * blockSpacingFactor  //pure visual, the amount that the background will show through on the edge of every tile

        val mostRight = blockSize * width / 2
        val mostTop = settings.screenHeight - borderWidth
        val cornerRadius =
            if (borderWidth * cornerPercentage > borderWidth - borderSpacing) borderWidth - borderSpacing else borderWidth * cornerPercentage
        val nonRadBorder = borderWidth - cornerRadius

        //sides
        backgroundMesh.addQuad(
            -mostRight - nonRadBorder, mostTop + borderWidth,
            mostRight + nonRadBorder, mostTop + borderSpacing, Colors.GRAY_NORMAL.get, zIndex
        )
        backgroundMesh.addQuad(
            -mostRight - nonRadBorder, -mostTop - borderWidth,
            mostRight + nonRadBorder, -mostTop - borderSpacing, Colors.GRAY_NORMAL.get, zIndex
        )
        backgroundMesh.addQuad(
            -mostRight - borderSpacing, mostTop + nonRadBorder,
            -mostRight - borderWidth, -mostTop - nonRadBorder, Colors.GRAY_NORMAL.get, zIndex
        )
        backgroundMesh.addQuad(
            mostRight + borderSpacing, mostTop + nonRadBorder,
            mostRight + borderWidth, -mostTop - nonRadBorder, Colors.GRAY_NORMAL.get, zIndex
        )

        //outerCorners
        val outerRes = 5
        backgroundMesh.addMesh(
            FlatOuterCurveMesh(
                Vector2f(-mostRight - nonRadBorder, mostTop + nonRadBorder),
                -90f, 0f, cornerRadius, outerRes
            ).setColor(Colors.GRAY_NORMAL.get), zIndex
        )
        backgroundMesh.addMesh(
            FlatOuterCurveMesh(
                Vector2f(mostRight + nonRadBorder, mostTop + nonRadBorder),
                0f, 90f, cornerRadius, outerRes
            ).setColor(Colors.GRAY_NORMAL.get), zIndex
        )
        backgroundMesh.addMesh(
            FlatOuterCurveMesh(
                Vector2f(mostRight + nonRadBorder, -mostTop - nonRadBorder),
                90f, 180f, cornerRadius, outerRes
            ).setColor(Colors.GRAY_NORMAL.get), zIndex
        )
        backgroundMesh.addMesh(
            FlatOuterCurveMesh(
                Vector2f(-mostRight - nonRadBorder, -mostTop - nonRadBorder),
                180f, 270f, cornerRadius, outerRes
            ).setColor(Colors.GRAY_NORMAL.get), zIndex
        )

        //innerCorners
        val innerRes = 3
        backgroundMesh.addMesh(
            FlatCustomCurveMesh(
                Vector2f(-mostRight + cornerRadius - borderSpacing, mostTop - cornerRadius + borderSpacing),
                Vector2f(-mostRight - borderSpacing, mostTop + borderSpacing),
                -90f,
                0f,
                cornerRadius,
                innerRes
            ).setColor(Colors.GRAY_NORMAL.get), zIndex
        )
        backgroundMesh.addMesh(
            FlatCustomCurveMesh(
                Vector2f(mostRight - cornerRadius + borderSpacing, mostTop - cornerRadius + borderSpacing),
                Vector2f(mostRight + borderSpacing, mostTop + borderSpacing),
                0f,
                90f,
                cornerRadius,
                innerRes
            ).setColor(Colors.GRAY_NORMAL.get), zIndex
        )
        backgroundMesh.addMesh(
            FlatCustomCurveMesh(
                Vector2f(mostRight - cornerRadius + borderSpacing, -mostTop + cornerRadius - borderSpacing),
                Vector2f(mostRight + borderSpacing, -mostTop - borderSpacing),
                90f,
                180f,
                cornerRadius,
                innerRes
            ).setColor(Colors.GRAY_NORMAL.get), zIndex
        )
        backgroundMesh.addMesh(
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
                backgroundMesh.addMesh(
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
        backgroundMesh.create()
        backgroundMesh.depth = -1f
    }

    fun delete(){
        backgroundMesh.clear()
    }
}