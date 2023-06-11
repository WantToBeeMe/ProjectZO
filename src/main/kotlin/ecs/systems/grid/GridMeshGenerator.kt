package ecs.systems.grid

import base.util.Colors
import ecs.ECSController
import ecs.components.GridComponent
import ecs.components.mesh.OpenMeshComponent
import ecs.components.mesh.customTemplates.FlatCurvedBoxMesh
import ecs.components.mesh.customTemplates.FlatCustomCurveMesh
import ecs.components.mesh.customTemplates.FlatOuterCurveMesh
import org.joml.Vector2f

class GridMeshGenerator(controller: ECSController, private val component : GridComponent, private val edgeSpacingFactor : Float = 0.08f, private val  cornerPercentage : Float = 0.48f, private val blockSpacingFactor : Float = 0.05f ) {
    private val backgroundID = controller.createEntity()
    private val backgroundMesh = controller.assign<OpenMeshComponent>(backgroundID)

    init {
        genTheMesh()
    }

    private fun genTheMesh() {
        val height = 1f //the lvl height index of the tiles or something (at least it's not that important because it's the height of only this openMesh, so it's not that important)
        val grid = component.grid

        val tempPerBlock = component.screenHeight * 2 / grid.size //temporaryValue
        val borderSpacing = tempPerBlock * edgeSpacingFactor //pure visual, te amount of space between the walls and the tiles
        val borderWidth = component.borderWidth  //the total width of the wall (no the true visual with, that's borderEdgeWidth - borderSpacing)
        val blockSize = component.blockSize      //the size of a cube in the grid
        //val borderWidth = tempPerBlock * component.edgeWidthPercentage
        //val perBlock = tempPerBlock - (borderWidth * 2 / grid.size)
        val perBlockSpacing = blockSize * blockSpacingFactor  //pure visual, the amount that the background will show through on the edge of every tile

        val mostRight = blockSize * grid[0].size / 2
        val mostTop = component.screenHeight - borderWidth
        val cornerRadius =
            if (borderWidth * cornerPercentage > borderWidth - borderSpacing) borderWidth - borderSpacing else borderWidth * cornerPercentage
        val nonRadBorder = borderWidth - cornerRadius

        //sides
        backgroundMesh.addQuad(
            -mostRight - nonRadBorder, mostTop + borderWidth,
            mostRight + nonRadBorder, mostTop + borderSpacing, Colors.GRAY_NORMAL.get, height
        )
        backgroundMesh.addQuad(
            -mostRight - nonRadBorder, -mostTop - borderWidth,
            mostRight + nonRadBorder, -mostTop - borderSpacing, Colors.GRAY_NORMAL.get, height
        )
        backgroundMesh.addQuad(
            -mostRight - borderSpacing, mostTop + nonRadBorder,
            -mostRight - borderWidth, -mostTop - nonRadBorder, Colors.GRAY_NORMAL.get, height
        )
        backgroundMesh.addQuad(
            mostRight + borderSpacing, mostTop + nonRadBorder,
            mostRight + borderWidth, -mostTop - nonRadBorder, Colors.GRAY_NORMAL.get, height
        )

        //outerCorners
        val outerRes = 5
        backgroundMesh.addMesh(
            FlatOuterCurveMesh(
                Vector2f(-mostRight - nonRadBorder, mostTop + nonRadBorder),
                -90f, 0f, cornerRadius, outerRes
            ).setColor(Colors.GRAY_NORMAL.get), height
        )
        backgroundMesh.addMesh(
            FlatOuterCurveMesh(
                Vector2f(mostRight + nonRadBorder, mostTop + nonRadBorder),
                0f, 90f, cornerRadius, outerRes
            ).setColor(Colors.GRAY_NORMAL.get), height
        )
        backgroundMesh.addMesh(
            FlatOuterCurveMesh(
                Vector2f(mostRight + nonRadBorder, -mostTop - nonRadBorder),
                90f, 180f, cornerRadius, outerRes
            ).setColor(Colors.GRAY_NORMAL.get), height
        )
        backgroundMesh.addMesh(
            FlatOuterCurveMesh(
                Vector2f(-mostRight - nonRadBorder, -mostTop - nonRadBorder),
                180f, 270f, cornerRadius, outerRes
            ).setColor(Colors.GRAY_NORMAL.get), height
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
            ).setColor(Colors.GRAY_NORMAL.get), height
        )
        backgroundMesh.addMesh(
            FlatCustomCurveMesh(
                Vector2f(mostRight - cornerRadius + borderSpacing, mostTop - cornerRadius + borderSpacing),
                Vector2f(mostRight + borderSpacing, mostTop + borderSpacing),
                0f,
                90f,
                cornerRadius,
                innerRes
            ).setColor(Colors.GRAY_NORMAL.get), height
        )
        backgroundMesh.addMesh(
            FlatCustomCurveMesh(
                Vector2f(mostRight - cornerRadius + borderSpacing, -mostTop + cornerRadius - borderSpacing),
                Vector2f(mostRight + borderSpacing, -mostTop - borderSpacing),
                90f,
                180f,
                cornerRadius,
                innerRes
            ).setColor(Colors.GRAY_NORMAL.get), height
        )
        backgroundMesh.addMesh(
            FlatCustomCurveMesh(
                Vector2f(-mostRight + cornerRadius - borderSpacing, -mostTop + cornerRadius - borderSpacing),
                Vector2f(-mostRight - borderSpacing, -mostTop - borderSpacing),
                180f,
                270f,
                cornerRadius,
                innerRes
            ).setColor(Colors.GRAY_NORMAL.get), height
        )




        for (i in grid.indices) {
            for (j in 0 until grid[0].size) {
                if (grid[i][j]) {
                    backgroundMesh.addMesh(
                        FlatCurvedBoxMesh(
                            Vector2f(-mostRight + blockSize * j + perBlockSpacing, mostTop - blockSize * i - perBlockSpacing),
                            Vector2f(
                                -mostRight + blockSize * j + blockSize - perBlockSpacing,
                                mostTop - blockSize * (i + 1) + perBlockSpacing
                            ),
                            cornerRadius, 3
                        ).setColor(Colors.GRAY_DARK.get), height
                    )
                } else {
                    val top = if (i <= 0) Triple((false), (false), (false)) else Triple(
                        (if (j <= 0) false else grid[i - 1][j - 1]),
                        grid[i - 1][j],
                        (if (j >= grid[i - 1].size - 1) false else grid[i - 1][j + 1])
                    )
                    val side = Pair(
                        (if (j <= 0) false else grid[i][j - 1]),
                        (if (j >= grid[i].size - 1) false else grid[i][j + 1])
                    )
                    val bot = if (i >= grid.size - 1) Triple((false), (false), (false)) else Triple(
                        (if (j <= 0) false else grid[i + 1][j - 1]),
                        grid[i + 1][j],
                        (if (j >= grid[i + 1].size - 1) false else grid[i + 1][j + 1])
                    )
                    addWall(
                        Vector2f(-mostRight + blockSize * (j + 0.5f), mostTop - blockSize * (i + 0.5f)),
                        blockSize,
                        cornerRadius,
                        borderSpacing,
                        top,
                        side,
                        bot
                    )
                }
            }
        }
        backgroundMesh.create()
        backgroundMesh.depth = -1f
    }

    private fun addWall(
        center: Vector2f, blockSize: Float, cornerRadius: Float, borderSpacing: Float,
        top: Triple<Boolean, Boolean, Boolean>, side: Pair<Boolean, Boolean>, bot: Triple<Boolean, Boolean, Boolean>
    ) {
        val outerLeftTop = Vector2f(center.x - blockSize / 2 + borderSpacing, center.y + blockSize / 2 - borderSpacing)
        val outerRightBot = Vector2f(center.x + blockSize / 2 - borderSpacing, center.y - blockSize / 2 + borderSpacing)
        val innerLeftTop = Vector2f(outerLeftTop.x + cornerRadius, outerLeftTop.y - cornerRadius)
        val innerRightBot = Vector2f(outerRightBot.x - cornerRadius, outerRightBot.y + cornerRadius)

        backgroundMesh.addQuad(innerLeftTop.x, outerLeftTop.y, innerRightBot.x, outerRightBot.y, Colors.GRAY_NORMAL.get)
        backgroundMesh.addQuad(outerLeftTop.x, innerLeftTop.y, outerRightBot.x, innerRightBot.y, Colors.GRAY_NORMAL.get)

        if (!top.second)
            backgroundMesh.addQuad(
                outerLeftTop.x, center.y + blockSize / 2 + borderSpacing,
                outerRightBot.x, innerLeftTop.y, Colors.GRAY_NORMAL.get
            )
        if (!bot.second)
            backgroundMesh.addQuad(
                outerLeftTop.x, innerRightBot.y,
                outerRightBot.x, center.y - blockSize / 2 - borderSpacing, Colors.GRAY_NORMAL.get
            )
        if (!side.first)
            backgroundMesh.addQuad(
                center.x - blockSize / 2 - borderSpacing, outerLeftTop.y,
                innerLeftTop.x, outerRightBot.y, Colors.GRAY_NORMAL.get
            )
        if (!side.second)
            backgroundMesh.addQuad(
                innerRightBot.x, outerLeftTop.y,
                center.x + blockSize / 2 + borderSpacing, outerRightBot.y, Colors.GRAY_NORMAL.get
            )

        val outerRes = 3
        if (side.first && top.second)
            backgroundMesh.addMesh(
                FlatOuterCurveMesh(
                    innerLeftTop,
                    -90f, 0f, cornerRadius, outerRes
                ).setColor(Colors.GRAY_NORMAL.get)
            )
        if (top.second && side.second)
            backgroundMesh.addMesh(
                FlatOuterCurveMesh(
                    Vector2f(innerRightBot.x, innerLeftTop.y),
                    0f, 90f, cornerRadius, outerRes
                ).setColor(Colors.GRAY_NORMAL.get)
            )
        if (side.second && bot.second)
            backgroundMesh.addMesh(
                FlatOuterCurveMesh(
                    innerRightBot,
                    90f, 180f, cornerRadius, outerRes
                ).setColor(Colors.GRAY_NORMAL.get)
            )
        if (bot.second && side.first)
            backgroundMesh.addMesh(
                FlatOuterCurveMesh(
                    Vector2f(innerLeftTop.x, innerRightBot.y),
                    180f, 270f, cornerRadius, outerRes
                ).setColor(Colors.GRAY_NORMAL.get)
            )

        val innerRes = 3
        if (!side.first && !top.second) {
            if (!top.first)
                backgroundMesh.addQuad(
                    outerLeftTop.x,
                    outerLeftTop.y,
                    center.x - blockSize / 2 - borderSpacing,
                    center.y + blockSize / 2 + borderSpacing,
                    Colors.GRAY_NORMAL.get
                )
            else
                backgroundMesh.addMesh(
                    FlatCustomCurveMesh(
                        Vector2f(outerLeftTop).add(-cornerRadius, cornerRadius), outerLeftTop,
                        90f, 180f, cornerRadius, innerRes
                    ).setColor(Colors.GRAY_NORMAL.get)
                )
        }

        if (!top.second && !side.second) {
            if (!top.third)
                backgroundMesh.addQuad(
                    outerRightBot.x,
                    outerLeftTop.y,
                    center.x + blockSize / 2 + borderSpacing,
                    center.y + blockSize / 2 + borderSpacing,
                    Colors.GRAY_NORMAL.get
                )
            else
                backgroundMesh.addMesh(
                    FlatCustomCurveMesh(
                        Vector2f(outerRightBot.x + cornerRadius, outerLeftTop.y + cornerRadius),
                        Vector2f(outerRightBot.x, outerLeftTop.y),
                        180f,
                        270f,
                        cornerRadius,
                        innerRes
                    ).setColor(Colors.GRAY_NORMAL.get)
                )
        }

        if (!side.second && !bot.second) {
            if (!bot.third)
                backgroundMesh.addQuad(
                    outerRightBot.x,
                    outerRightBot.y,
                    center.x + blockSize / 2 + borderSpacing,
                    center.y - blockSize / 2 - borderSpacing,
                    Colors.GRAY_NORMAL.get
                )
            else
                backgroundMesh.addMesh(
                    FlatCustomCurveMesh(
                        Vector2f(outerRightBot).add(cornerRadius, -cornerRadius), outerRightBot,
                        270f, 0f, cornerRadius, innerRes
                    ).setColor(Colors.GRAY_NORMAL.get)
                )
        }

        if (!bot.second && !side.first) {
            if (!bot.first)
                backgroundMesh.addQuad(
                    outerLeftTop.x,
                    outerRightBot.y,
                    center.x - blockSize / 2 - borderSpacing,
                    center.y - blockSize / 2 - borderSpacing,
                    Colors.GRAY_NORMAL.get
                )
            else
                backgroundMesh.addMesh(
                    FlatCustomCurveMesh(
                        Vector2f(outerLeftTop.x - cornerRadius, outerRightBot.y - cornerRadius),
                        Vector2f(outerLeftTop.x, outerRightBot.y),
                        0f,
                        90f,
                        cornerRadius,
                        innerRes
                    ).setColor(Colors.GRAY_NORMAL.get)
                )
        }
    }

    fun delete(){
        backgroundMesh.clear()
    }
}