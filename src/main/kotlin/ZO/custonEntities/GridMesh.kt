package ZO.custonEntities

import base.util.Colors
import ecs.ECSController
import ecs.components.mesh.customTemplates.FlatCircleMesh
import ecs.components.mesh.OpenMeshComponent
import ecs.components.mesh.customTemplates.FlatCurvedBoxMesh
import ecs.components.mesh.customTemplates.FlatInnerCurveMesh
import ecs.components.mesh.customTemplates.FlatOuterCurveMesh
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
        val mostRight = perBlock*grid[0].size/2
        for(i in grid.indices){
            for(j in 0 until grid[0].size){
                if(i == 0)
                   backgroundMesh.addQuad(-mostRight + perBlock*j ,screenHeight,-mostRight + perBlock*j + perBlock,screenHeight -perBlock/2f , Colors.GRAY_DARK.get, 1f)
                if(i == grid.size-1)
                    backgroundMesh.addQuad(-mostRight + perBlock*j ,-screenHeight,-mostRight + perBlock*j + perBlock,-screenHeight +perBlock/2f , Colors.GRAY_DARK.get, 1f)

                if(j==0)
                    backgroundMesh.addQuad(-mostRight -perBlock/2f ,screenHeight -perBlock*i -perBlock*0.5f,-mostRight,screenHeight -perBlock*i -perBlock*1.5f, Colors.GRAY_DARK.get, 1f)
                if(j==grid[0].size-1)
                    backgroundMesh.addQuad(mostRight,screenHeight -perBlock*i -perBlock*0.5f,mostRight+perBlock/2f ,screenHeight -perBlock*i -perBlock*1.5f, Colors.GRAY_DARK.get, 1f)

                if(grid[i][j]){
                    backgroundMesh.addMesh(
                        FlatCircleMesh(Vector2f(-mostRight + perBlock*j + perBlock/2, screenHeight -perBlock*(i+1)  ), perBlock/10f )
                            .setColor(Colors.GRAY_DARK.get),1f)
                }
                else{
                   backgroundMesh.addMesh(
                       FlatCurvedBoxMesh(Vector2f(-mostRight  + perBlock*j, screenHeight -perBlock*(i+1) + perBlock/2) ,
                       Vector2f(-mostRight + perBlock*j +perBlock ,screenHeight -perBlock*(i+1) - perBlock/2 ),
                           perBlock/8f, 3).setColor(Colors.GRAY_DARK.get),1f)
                }
            }
        }
        backgroundMesh.create()
        backgroundMesh.depth = -1f
    }

}