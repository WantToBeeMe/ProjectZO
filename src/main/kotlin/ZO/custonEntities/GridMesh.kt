package ZO.custonEntities

import base.util.Colors
import ecs.ECSController
import ecs.components.mesh.customTemplates.FlatCircleMesh
import ecs.components.mesh.OpenMeshComponent
import ecs.components.mesh.customTemplates.FlatCurvedBoxMesh
import ecs.components.mesh.customTemplates.FlatOuterCurveMesh
import org.joml.Vector2f

class GridMesh(controller: ECSController, grid: Array<BooleanArray>, screenHeight: Float) {
    constructor(controller: ECSController, width: Int, height: Int, screenHeight: Float) :
            this(controller, Array(height) { BooleanArray(width) { true } }, screenHeight)

    init {
        val cornerPercentage = 8f

        val height = 1f

        val backgroundID = controller.createEntity()
        val backgroundMesh = controller.assign<OpenMeshComponent>(backgroundID)

        val perBlock = screenHeight*2 / (grid.size+1f)
        val mostRight = perBlock*grid[0].size*0.5f
        val mostTop = screenHeight-perBlock*0.5f
        val cornerRadius = perBlock/cornerPercentage
        val borderEdgeWidth = perBlock*0.5f
        val nonRad = borderEdgeWidth-cornerRadius

        //sides
        backgroundMesh.addQuad(-mostRight-nonRad, mostTop+borderEdgeWidth,
            mostRight+nonRad,mostTop, Colors.GRAY_DARK.get,height)
        backgroundMesh.addQuad(-mostRight-nonRad, -mostTop-borderEdgeWidth,
            mostRight+nonRad,-mostTop, Colors.GRAY_DARK.get,height)
        backgroundMesh.addQuad(-mostRight, mostTop+nonRad,
            -mostRight-borderEdgeWidth, -mostTop-nonRad, Colors.GRAY_DARK.get,height)
        backgroundMesh.addQuad(mostRight, mostTop+nonRad,
            mostRight+borderEdgeWidth, -mostTop-nonRad, Colors.GRAY_DARK.get,height)

        //corners
        backgroundMesh.addMesh( FlatOuterCurveMesh( Vector2f(-mostRight-nonRad,mostTop+nonRad),
            -90f , 0f, cornerRadius ).setColor(Colors.GRAY_DARK.get),height)
        backgroundMesh.addMesh( FlatOuterCurveMesh( Vector2f(mostRight+nonRad,mostTop+nonRad),
            0f , 90f, cornerRadius ).setColor(Colors.GRAY_DARK.get),height)
        backgroundMesh.addMesh( FlatOuterCurveMesh( Vector2f(mostRight+nonRad,-mostTop-nonRad),
            90f , 180f, cornerRadius ).setColor(Colors.GRAY_DARK.get),height)
        backgroundMesh.addMesh( FlatOuterCurveMesh( Vector2f(-mostRight-nonRad,-mostTop-nonRad),
            180f , 270f, cornerRadius ).setColor(Colors.GRAY_DARK.get),height)

        for(i in grid.indices){
            for(j in 0 until grid[0].size){
                if(grid[i][j]){
                    backgroundMesh.addMesh(
                        FlatCircleMesh(Vector2f(-mostRight + perBlock*j + perBlock/2, screenHeight -perBlock*(i+1)  ), perBlock/10f )
                            .setColor(Colors.GRAY_DARK.get),height)
                }
                else{
                   backgroundMesh.addMesh(
                       FlatCurvedBoxMesh(Vector2f(-mostRight  + perBlock*j, screenHeight -perBlock*(i+1) + perBlock/2) ,
                       Vector2f(-mostRight + perBlock*j +perBlock ,screenHeight -perBlock*(i+1) - perBlock/2 ),
                           cornerRadius, 3).setColor(Colors.GRAY_DARK.get),height)
                }
            }
        }
        backgroundMesh.create()
        backgroundMesh.depth = -1f
    }

}