package ZO.custonEntities

import base.util.Colors
import base.util.IImGuiWindow
import base.util.ImGuiController
import ecs.ECSController
import ecs.components.GridComponent
import ecs.components.mesh.OpenMeshComponent
import ecs.components.mesh.customTemplates.FlatCurvedBoxMesh
import ecs.components.mesh.customTemplates.FlatCustomCurveMesh
import ecs.components.mesh.customTemplates.FlatOuterCurveMesh
import imgui.ImGui
import imgui.enums.ImGuiCond
import org.joml.Vector2f

class GridMesh(controller: ECSController,private val grid: Array<BooleanArray>,private val screenHeight: Float): IImGuiWindow {
    constructor(controller: ECSController, width: Int, height: Int, screenHeight: Float) :
            this(controller, Array(height) { BooleanArray(width) { true } }, screenHeight)

    private val backgroundID = controller.createEntity()
    private val backgroundMesh = controller.assign<OpenMeshComponent>(backgroundID)
    private val gridComponent = controller.assign<GridComponent>(backgroundID).setScreenHeight(screenHeight).setGrid(grid)

    private var edgeSpacing = 0.08f
    private var edgeWidthPercentage = 0.3f
    private var cornerPercentage = 0.48f


    init {
        ImGuiController.addGui(this)
        genStuff()
    }

    private fun genStuff(){
        gridComponent.setEdgeWidthPercentage(edgeWidthPercentage)

        val height = 1f
        val blockEdge = 0.05f

        val tempPerBlock = screenHeight*2/grid.size
        val borderEdgeWidth = tempPerBlock*edgeWidthPercentage
        val borderSpacing = tempPerBlock*edgeSpacing
        val perBlock = tempPerBlock - (borderEdgeWidth*2/grid.size)
        val perBlockEdge = perBlock*blockEdge

        val mostRight = perBlock*grid[0].size/2
        val mostTop = screenHeight-borderEdgeWidth
        val cornerRadius = if(borderEdgeWidth*cornerPercentage > borderEdgeWidth-borderSpacing) borderEdgeWidth-borderSpacing else borderEdgeWidth*cornerPercentage
        val nonRadBorder = borderEdgeWidth-cornerRadius

        //sides
        backgroundMesh.addQuad(-mostRight-nonRadBorder, mostTop+borderEdgeWidth,
            mostRight+nonRadBorder,mostTop+borderSpacing, Colors.GRAY_NORMAL.get,height)
        backgroundMesh.addQuad(-mostRight-nonRadBorder, -mostTop-borderEdgeWidth,
            mostRight+nonRadBorder,-mostTop-borderSpacing, Colors.GRAY_NORMAL.get,height)
        backgroundMesh.addQuad(-mostRight-borderSpacing, mostTop+nonRadBorder,
            -mostRight-borderEdgeWidth, -mostTop-nonRadBorder, Colors.GRAY_NORMAL.get,height)
        backgroundMesh.addQuad(mostRight+borderSpacing, mostTop+nonRadBorder,
            mostRight+borderEdgeWidth, -mostTop-nonRadBorder, Colors.GRAY_NORMAL.get,height)

        //outerCorners
        val outerRes= 5
        backgroundMesh.addMesh( FlatOuterCurveMesh( Vector2f(-mostRight-nonRadBorder,mostTop+nonRadBorder),
            -90f , 0f, cornerRadius,outerRes ).setColor(Colors.GRAY_NORMAL.get),height)
        backgroundMesh.addMesh( FlatOuterCurveMesh( Vector2f(mostRight+nonRadBorder,mostTop+nonRadBorder),
            0f , 90f, cornerRadius,outerRes).setColor(Colors.GRAY_NORMAL.get),height)
        backgroundMesh.addMesh( FlatOuterCurveMesh( Vector2f(mostRight+nonRadBorder,-mostTop-nonRadBorder),
            90f , 180f, cornerRadius,outerRes ).setColor(Colors.GRAY_NORMAL.get),height)
        backgroundMesh.addMesh( FlatOuterCurveMesh( Vector2f(-mostRight-nonRadBorder,-mostTop-nonRadBorder),
            180f , 270f, cornerRadius,outerRes ).setColor(Colors.GRAY_NORMAL.get),height)

        //innerCorners
        val innerRes= 3
        backgroundMesh.addMesh( FlatCustomCurveMesh( Vector2f(-mostRight+cornerRadius -borderSpacing,mostTop-cornerRadius+borderSpacing),Vector2f(-mostRight-borderSpacing,mostTop+borderSpacing),
            -90f , 0f, cornerRadius ,innerRes).setColor(Colors.GRAY_NORMAL.get),height)
        backgroundMesh.addMesh( FlatCustomCurveMesh( Vector2f(mostRight-cornerRadius+borderSpacing,mostTop-cornerRadius+borderSpacing),Vector2f(mostRight+borderSpacing,mostTop+borderSpacing),
            0f , 90f, cornerRadius ,innerRes ).setColor(Colors.GRAY_NORMAL.get),height)
        backgroundMesh.addMesh( FlatCustomCurveMesh( Vector2f(mostRight-cornerRadius +borderSpacing,-mostTop+cornerRadius-borderSpacing),Vector2f(mostRight+borderSpacing,-mostTop-borderSpacing),
            90f , 180f, cornerRadius ,innerRes ).setColor(Colors.GRAY_NORMAL.get),height)
        backgroundMesh.addMesh( FlatCustomCurveMesh( Vector2f(-mostRight+cornerRadius -borderSpacing,-mostTop+cornerRadius-borderSpacing),Vector2f(-mostRight-borderSpacing,-mostTop-borderSpacing),
            180f , 270f, cornerRadius ,innerRes ).setColor(Colors.GRAY_NORMAL.get),height)




        for(i in grid.indices){
            for(j in 0 until grid[0].size){
                if(grid[i][j]){
                    backgroundMesh.addMesh(
                        FlatCurvedBoxMesh(Vector2f(-mostRight+perBlock*j + perBlockEdge, mostTop-perBlock*i - perBlockEdge) ,
                            Vector2f(-mostRight+ perBlock*j+perBlock -perBlockEdge ,mostTop -perBlock*(i+1) + perBlockEdge),
                            cornerRadius, 3).setColor(Colors.GRAY_DARK.get),height)
                }
                else{
                    val top = if(i <= 0) Triple((false),(false),(false)) else Triple(
                        (if (j <= 0) false else grid[i-1][j-1]) ,
                        grid[i-1][j] ,
                        (if (j >= grid[i-1].size-1) false else grid[i-1][j+1])
                    )
                    val side = Pair(
                        (if (j <= 0) false else grid[i][j-1]) ,
                        (if (j >= grid[i].size-1) false else grid[i][j+1])
                    )
                    val bot = if(i >= grid.size-1) Triple((false),(false),(false)) else Triple(
                        (if (j <= 0) false else grid[i+1][j-1]) ,
                        grid[i+1][j] ,
                        (if (j >= grid[i+1].size-1) false else grid[i+1][j+1])
                    )
                    addWall( Vector2f(-mostRight  + perBlock*(j+0.5f) , mostTop -perBlock*(i+0.5f)),perBlock,cornerRadius, borderSpacing, top, side, bot )
                }
            }
        }
        backgroundMesh.create()
        backgroundMesh.depth = -1f
    }

    private fun addWall(center: Vector2f, blockSize: Float, cornerRadius : Float, borderSpacing : Float,
                        top : Triple<Boolean, Boolean,Boolean>, side: Pair<Boolean, Boolean>, bot : Triple<Boolean, Boolean,Boolean>){
        val outerLeftTop = Vector2f(center.x - blockSize/2 + borderSpacing , center.y + blockSize/2 - borderSpacing )
        val outerRightBot = Vector2f(center.x + blockSize/2 - borderSpacing , center.y - blockSize/2 + borderSpacing )
        val innerLeftTop = Vector2f(outerLeftTop.x + cornerRadius, outerLeftTop.y - cornerRadius)
        val innerRightBot = Vector2f(outerRightBot.x - cornerRadius, outerRightBot.y + cornerRadius)

        backgroundMesh.addQuad(innerLeftTop.x, outerLeftTop.y, innerRightBot.x, outerRightBot.y, Colors.GRAY_NORMAL.get)
        backgroundMesh.addQuad(outerLeftTop.x, innerLeftTop.y, outerRightBot.x, innerRightBot.y, Colors.GRAY_NORMAL.get)

        if(!top.second)
            backgroundMesh.addQuad(
                outerLeftTop.x, center.y + blockSize/2 + borderSpacing,
                outerRightBot.x,  innerLeftTop.y, Colors.GRAY_NORMAL.get)
        if(!bot.second)
            backgroundMesh.addQuad(
                outerLeftTop.x, innerRightBot.y,
                outerRightBot.x, center.y - blockSize/2 - borderSpacing, Colors.GRAY_NORMAL.get)
        if(!side.first)
            backgroundMesh.addQuad(
                center.x - blockSize/2 - borderSpacing , outerLeftTop.y,
                innerLeftTop.x,  outerRightBot.y, Colors.GRAY_NORMAL.get)
        if(!side.second)
            backgroundMesh.addQuad(
                innerRightBot.x , outerLeftTop.y,
                center.x + blockSize/2 + borderSpacing,  outerRightBot.y, Colors.GRAY_NORMAL.get)

        val outerRes = 3
        if(side.first && top.second)
            backgroundMesh.addMesh( FlatOuterCurveMesh( innerLeftTop,
                -90f , 0f, cornerRadius ,outerRes ).setColor(Colors.GRAY_NORMAL.get))
        if(top.second && side.second)
            backgroundMesh.addMesh( FlatOuterCurveMesh( Vector2f(innerRightBot.x, innerLeftTop.y),
                0f , 90f, cornerRadius ,outerRes ).setColor(Colors.GRAY_NORMAL.get))
        if(side.second && bot.second)
            backgroundMesh.addMesh( FlatOuterCurveMesh( innerRightBot,
                90f , 180f, cornerRadius ,outerRes ).setColor(Colors.GRAY_NORMAL.get))
        if(bot.second && side.first)
            backgroundMesh.addMesh( FlatOuterCurveMesh( Vector2f(innerLeftTop.x, innerRightBot.y),
                180f , 270f, cornerRadius ,outerRes ).setColor(Colors.GRAY_NORMAL.get))

        val innerRes = 3
        if(!side.first && !top.second){
            if(!top.first)
                backgroundMesh.addQuad(
                        outerLeftTop.x, outerLeftTop.y,
                        center.x - blockSize/2 - borderSpacing, center.y + blockSize/2 + borderSpacing , Colors.GRAY_NORMAL.get)
            else
                backgroundMesh.addMesh(
                        FlatCustomCurveMesh(Vector2f(outerLeftTop).add(-cornerRadius, cornerRadius), outerLeftTop,
                                90f, 180f ,cornerRadius, innerRes ).setColor(Colors.GRAY_NORMAL.get))
        }

        if(!top.second && !side.second){
            if(!top.third)
                backgroundMesh.addQuad(
                        outerRightBot.x, outerLeftTop.y,
                        center.x + blockSize/2 + borderSpacing, center.y + blockSize/2 + borderSpacing , Colors.GRAY_NORMAL.get)
            else
                backgroundMesh.addMesh(
                        FlatCustomCurveMesh(Vector2f(outerRightBot.x+cornerRadius,outerLeftTop.y+cornerRadius), Vector2f(outerRightBot.x,outerLeftTop.y),
                                180f, 270f ,cornerRadius, innerRes ).setColor(Colors.GRAY_NORMAL.get))
        }

        if(!side.second && !bot.second) {
            if(!bot.third)
                backgroundMesh.addQuad(
                        outerRightBot.x, outerRightBot.y,
                        center.x + blockSize/2 + borderSpacing, center.y - blockSize/2 - borderSpacing , Colors.GRAY_NORMAL.get)
            else
                backgroundMesh.addMesh(
                        FlatCustomCurveMesh(Vector2f(outerRightBot).add(cornerRadius, -cornerRadius), outerRightBot,
                                270f, 0f ,cornerRadius, innerRes ).setColor(Colors.GRAY_NORMAL.get))
        }

        if(!bot.second && !side.first){
            if(!bot.first)
                backgroundMesh.addQuad(
                        outerLeftTop.x, outerRightBot.y,
                        center.x - blockSize/2 - borderSpacing, center.y - blockSize/2 - borderSpacing , Colors.GRAY_NORMAL.get)
            else
                backgroundMesh.addMesh(
                        FlatCustomCurveMesh(Vector2f(outerLeftTop.x-cornerRadius,outerRightBot.y-cornerRadius), Vector2f(outerLeftTop.x,outerRightBot.y),
                                0f, 90f ,cornerRadius, innerRes ).setColor(Colors.GRAY_NORMAL.get))
        }



    }





    override fun showUi() {
        ImGui.setNextWindowSize(250f, 140f, ImGuiCond.Once)
        ImGui.setNextWindowPos(0f, 0f, ImGuiCond.Once)
        ImGui.begin( "grid mesh thing" )
        var reGenMesh = false

        val edgeValue = floatArrayOf(edgeWidthPercentage)
        ImGui.sliderFloat("edge width", edgeValue, 0.05f, 0.5f)
        if(edgeWidthPercentage != edgeValue[0]){
            edgeWidthPercentage = edgeValue[0]
            if(edgeSpacing > edgeWidthPercentage -0.05f ){
                edgeSpacing = edgeWidthPercentage  -0.05f
            }
            reGenMesh = true
        }

        val edgeSpacingValue = floatArrayOf(edgeSpacing)
        ImGui.sliderFloat("edge spacing", edgeSpacingValue, 0f, edgeWidthPercentage - 0.05f)
        if(edgeSpacing != edgeSpacingValue[0]){
            edgeSpacing = edgeSpacingValue[0]
            reGenMesh = true
        }


        val cornerValue = floatArrayOf(cornerPercentage)
        ImGui.sliderFloat("corner", cornerValue, 0f, 1f)
        if(cornerPercentage != cornerValue[0]){
            cornerPercentage = cornerValue[0]
            reGenMesh = true
        }


        if(reGenMesh){
            backgroundMesh.clear()
            genStuff()
        }

        ImGui.end()
    }
}