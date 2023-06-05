package ZO.custonEntities

import base.util.Colors
import base.util.IImGuiWindow
import base.util.ImGuiController
import ecs.ECSController
import ecs.components.mesh.customTemplates.FlatCircleMesh
import ecs.components.mesh.OpenMeshComponent
import ecs.components.mesh.customTemplates.FlatCurvedBoxMesh
import ecs.components.mesh.customTemplates.FlatCustomCurveMesh
import ecs.components.mesh.customTemplates.FlatOuterCurveMesh
import imgui.ImGui
import imgui.enums.ImGuiCond
import org.joml.Vector2f

class GridMesh(private val controller: ECSController,private val grid: Array<BooleanArray>,private val screenHeight: Float): IImGuiWindow {
    constructor(controller: ECSController, width: Int, height: Int, screenHeight: Float) :
            this(controller, Array(height) { BooleanArray(width) { true } }, screenHeight)

    private val backgroundID = controller.createEntity()
    private val backgroundMesh = controller.assign<OpenMeshComponent>(backgroundID)

    private var edgeSpacing = 0.1f
    private var edgeWidthPercentage = 0.3f
    private var cornerPercentage = 0.4f


    init {
        ImGuiController.addGui(this)
        genStuff()
    }

    private fun genStuff(){
        val height = 1f

        val tempPerBlock = screenHeight*2/grid.size
        val borderEdgeWidth = tempPerBlock*edgeWidthPercentage
        val borderSpacing = tempPerBlock*edgeSpacing
        val perBlock = tempPerBlock - (borderEdgeWidth*2/grid.size)

        val mostRight = perBlock*grid[0].size/2
        val mostTop = screenHeight-borderEdgeWidth
        val cornerRadius = if(borderEdgeWidth*cornerPercentage > borderEdgeWidth-borderSpacing) borderEdgeWidth-borderSpacing else borderEdgeWidth*cornerPercentage
        val nonRadBorder = borderEdgeWidth-cornerRadius

        //sides
        backgroundMesh.addQuad(-mostRight-nonRadBorder, mostTop+borderEdgeWidth,
            mostRight+nonRadBorder,mostTop+borderSpacing, Colors.GRAY_DARK.get,height)
        backgroundMesh.addQuad(-mostRight-nonRadBorder, -mostTop-borderEdgeWidth,
            mostRight+nonRadBorder,-mostTop-borderSpacing, Colors.GRAY_DARK.get,height)
        backgroundMesh.addQuad(-mostRight-borderSpacing, mostTop+nonRadBorder,
            -mostRight-borderEdgeWidth, -mostTop-nonRadBorder, Colors.GRAY_DARK.get,height)
        backgroundMesh.addQuad(mostRight+borderSpacing, mostTop+nonRadBorder,
            mostRight+borderEdgeWidth, -mostTop-nonRadBorder, Colors.GRAY_DARK.get,height)

        //outerCorners
        backgroundMesh.addMesh( FlatOuterCurveMesh( Vector2f(-mostRight-nonRadBorder,mostTop+nonRadBorder),
            -90f , 0f, cornerRadius ).setColor(Colors.GRAY_DARK.get),height)
        backgroundMesh.addMesh( FlatOuterCurveMesh( Vector2f(mostRight+nonRadBorder,mostTop+nonRadBorder),
            0f , 90f, cornerRadius ).setColor(Colors.GRAY_DARK.get),height)
        backgroundMesh.addMesh( FlatOuterCurveMesh( Vector2f(mostRight+nonRadBorder,-mostTop-nonRadBorder),
            90f , 180f, cornerRadius ).setColor(Colors.GRAY_DARK.get),height)
        backgroundMesh.addMesh( FlatOuterCurveMesh( Vector2f(-mostRight-nonRadBorder,-mostTop-nonRadBorder),
            180f , 270f, cornerRadius ).setColor(Colors.GRAY_DARK.get),height)

        //innerCorners
        backgroundMesh.addMesh( FlatCustomCurveMesh( Vector2f(-mostRight+cornerRadius -borderSpacing,mostTop-cornerRadius+borderSpacing),Vector2f(-mostRight-borderSpacing,mostTop+borderSpacing),
            -90f , 0f, cornerRadius ).setColor(Colors.GRAY_DARK.get),height)
        backgroundMesh.addMesh( FlatCustomCurveMesh( Vector2f(mostRight-cornerRadius+borderSpacing,mostTop-cornerRadius+borderSpacing),Vector2f(mostRight+borderSpacing,mostTop+borderSpacing),
            0f , 90f, cornerRadius ).setColor(Colors.GRAY_DARK.get),height)
        backgroundMesh.addMesh( FlatCustomCurveMesh( Vector2f(mostRight-cornerRadius +borderSpacing,-mostTop+cornerRadius-borderSpacing),Vector2f(mostRight+borderSpacing,-mostTop-borderSpacing),
            90f , 180f, cornerRadius ).setColor(Colors.GRAY_DARK.get),height)
        backgroundMesh.addMesh( FlatCustomCurveMesh( Vector2f(-mostRight+cornerRadius -borderSpacing,-mostTop+cornerRadius-borderSpacing),Vector2f(-mostRight-borderSpacing,-mostTop-borderSpacing),
            180f , 270f, cornerRadius ).setColor(Colors.GRAY_DARK.get),height)




        for(i in grid.indices){
            for(j in 0 until grid[0].size){
                if(grid[i][j]){
                    backgroundMesh.addMesh(
                        FlatCircleMesh(Vector2f(-mostRight + perBlock*j + perBlock/2, mostTop -perBlock*i -perBlock/2   ), perBlock/10f )
                            .setColor(Colors.GRAY_DARK.get),height)
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

                    backgroundMesh.addMesh(
                        FlatCurvedBoxMesh(Vector2f(-mostRight  + perBlock*j, mostTop -perBlock*i) ,
                            Vector2f(-mostRight + perBlock*j +perBlock ,mostTop -perBlock*(i+1) ),
                            cornerRadius, 3).setColor(Colors.GRAY_DARK.get),height)
                }
            }
        }
        backgroundMesh.create()
        backgroundMesh.depth = -1f
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