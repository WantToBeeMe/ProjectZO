package ZO.custonEntities

import base.util.Colors
import ecs.ECSController
import ecs.components.mesh.FlatCircleMesh
import ecs.components.mesh.OpenMeshComponent
import org.joml.Vector2f

class GridMesh(controller: ECSController, grid: Array<BooleanArray>, screenHeight: Float) {
    constructor(controller: ECSController, width: Int, height: Int, screenHeight: Float) :
            this(controller, Array(height) { BooleanArray(width) { true } }, screenHeight)

    init {
        //the real screen height is from 1 to -1 while the input screen height is from 1 to 0, so we multiply it with 2
        //the +1 is for the outside borders, they combined are the size of 1 grid block (so half as thin)
        val perBlock = screenHeight*2 / (grid.size+1)

        val backgroundID = controller.createEntity()
        val backgroundMesh = controller.assign<OpenMeshComponent>(backgroundID)
        //backgroundMesh.addQuad(-0.1f,screenHeight,0.1f,-screenHeight , Colors.GRAY_SUPER_DARK.get)

        backgroundMesh.addQuad(-0.1f,-screenHeight,0.1f,-screenHeight +perBlock/2f , Colors.GRAY_DARK.get, 1f)
        val mostLeft = -perBlock*grid[0].size/2
        for(i in grid.indices){
            for(j in 0 until grid[0].size){
                if(i == 0){
                    backgroundMesh.addQuad(0.1f ,screenHeight,0.1f,screenHeight -perBlock/2f , Colors.GRAY_DARK.get, 0.5f)
                   //backgroundMesh.addQuad(-mostLeft + perBlock*j ,screenHeight,-mostLeft + perBlock*j + perBlock,screenHeight -perBlock/2f , Colors.GRAY_DARK.get, 1f)
                }
                if(grid[i][j]){
                    backgroundMesh.addMesh(
                        FlatCircleMesh(Vector2f(mostLeft + perBlock*j + perBlock/2, screenHeight -perBlock*(i+1)  ), perBlock/10f ).setColor(Colors.GRAY_DARK.get)
                        ,1f)
                }
            }
        }
        backgroundMesh.create()
        backgroundMesh.depth = -1f
    }

}