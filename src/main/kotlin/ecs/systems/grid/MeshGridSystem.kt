package ecs.systems.grid

import ZO.game.GLCBlock
import base.input.IMouseClickObserver
import base.input.Mouse
import base.util.Maf
import ecs.ECSController
import ecs.singletons.Camera
import ecs.singletons.GridSettings
import ecs.components.GridLockedComponent
import ecs.components.TransformComponent
import ecs.components.clickBox.ClickBoxComponent
import ecs.components.clickBox.RectangleClickBox
import ecs.components.mesh.FlatMeshComponent
import ecs.systems.IEntityComponentSystem
import org.joml.Vector2f
import org.joml.Vector2i

object MeshGridSystem  : IEntityComponentSystem(), IMouseClickObserver {

    private lateinit var gridMeshGenerator : GridComponentGenerator

    override fun create() {
        super.create()
        val gridSettings = controller.getSingleton<GridSettings>()

        gridMeshGenerator = GridComponentGenerator(controller).setSettings(gridSettings)
        gridMeshGenerator.generateGridBackground()

        GLCBlock(controller,1,1, gridSettings.blockEdgeShorteningPercentage, gridSettings.getCornerRadius() / gridSettings.blockSize )
        GLCBlock(controller,2,2, gridSettings.blockEdgeShorteningPercentage, gridSettings.getCornerRadius() / gridSettings.blockSize )
        GLCBlock(controller,3,2, gridSettings.blockEdgeShorteningPercentage, gridSettings.getCornerRadius() / gridSettings.blockSize )
    }
    override fun stop() {
        super.stop()
        Mouse.unSubscribe(this)
    }
    override fun start(controller: ECSController) {
        super.start(controller)
        Mouse.subscribe(this)
    }

    private var holdingGirdCorrection : Vector2f? = null
    private var oldGridPosition : Vector2f? = null
    private var oldIndex : Vector2i = Vector2i(-1)
    private var holdingGLC: Pair<Int,Triple<TransformComponent, ClickBoxComponent, GridLockedComponent>>? = null
    override fun update(dt: Float) {
        super.update(dt)
        val camera = controller.getSingleton<Camera>()
        val gridSettings = controller.getSingleton<GridSettings>()
        val gLMouse = Maf.pixelToGLCords(Mouse.getX(),Mouse.getY(),camera.aspect)
        val lt = gridSettings.viewBoxLeftTop
        val rb = gridSettings.viewBoxRightBot
        val isInside = (lt.x < gLMouse.x && rb.x > gLMouse.x) && ( lt.y > gLMouse.y && rb.y < gLMouse.y )

        if(holdingGLC != null) {
            holdingGLC!!.second.first.setPosition(gLMouse)
            if (isInside) gridMeshGenerator.showShadow(gLMouse)
            else gridMeshGenerator.hideShadow()
        }
        //todo: moving the grid works, its just broken in some way, just test it yourself and you will see
        else if(holdingGirdCorrection != null) {
            val spaceNeededX = (gridSettings.gridWidth*gridSettings.blockSize + gridSettings.borderWidth*2) * gridSettings.getScale()
            val spaceNeededY = (gridSettings.gridHeight*gridSettings.blockSize + gridSettings.borderWidth*2) * gridSettings.getScale()
            val spaceGivenX = gridSettings.viewBoxRightBot.x - gridSettings.viewBoxLeftTop.x
            val spaceGivenY = gridSettings.viewBoxLeftTop.y - gridSettings.viewBoxRightBot.y
            val freedomX = if(spaceNeededX - spaceGivenX > 0) spaceNeededX - spaceGivenX else 0f
            val freedomY = if(spaceNeededY - spaceGivenY > 0) spaceNeededY - spaceGivenY else 0f
            if(freedomX != 0f || freedomY != 0f){
                if(oldGridPosition == null) oldGridPosition = gridSettings.gridPosition
                val alreadyMoved = gridSettings.getCenterViewBox().add( -oldGridPosition!!.x , -oldGridPosition!!.y )
                val currentlyMoved = Vector2f(gLMouse).add( -holdingGirdCorrection!!.x, -holdingGirdCorrection!!.y )
                val realMoveX = Maf.clamp(alreadyMoved.x + currentlyMoved.x,-freedomX,freedomX )
                val realMoveY = Maf.clamp(alreadyMoved.y + currentlyMoved.y,-freedomY,freedomY )
                val center = Vector2f(oldGridPosition).add(realMoveX, realMoveY)
                gridSettings.setPosition(center)
                gridMeshGenerator.setGridPosition(center)
                redoTransforms()
            }
        }


    }



    override fun onMouseClick(xPos: Double, yPos: Double, button: Int) {
        if(holdingGLC != null) return
        val gridSettings = controller.getSingleton<GridSettings>()
        val gLComponents = controller.getTripleComponents<TransformComponent, ClickBoxComponent, GridLockedComponent>()
        val camera = controller.getSingleton<Camera>()
        val gLMouse = Maf.pixelToGLCords(xPos.toFloat(),yPos.toFloat(),camera.aspect)
        for((gLCKey, gLComponent) in gLComponents){
            if(gLComponent.second.isInside(Maf.revertTransform(gLMouse,gLComponent.first))){
                holdingGLC = Pair(gLCKey,gLComponent)
                oldIndex = gridSettings.removeGLC(gLCKey)
                gLComponent.first.setScale(gridSettings.getScale() * gridSettings.blockSize * 0.9f)
                gridMeshGenerator.createShadow(gLComponent.third)
                break
            }
        }
    }

    override fun onMouseRelease(xPos: Double, yPos: Double, button: Int) {
        if(holdingGLC == null) return
        val gridSettings = controller.getSingleton<GridSettings>()
        val GLC = holdingGLC!!.second.third
        val camera = controller.getSingleton<Camera>()
        val gLMouse = Maf.pixelToGLCords(xPos.toFloat(),yPos.toFloat(),camera.aspect)
        val leftTop = GLC.getGLCLeftTopIndex(  gLMouse , gridSettings)
        val lt = gridSettings.viewBoxLeftTop
        val rb = gridSettings.viewBoxRightBot
        val isInsideViewBox = (lt.x < gLMouse.x && rb.x > gLMouse.x) && ( lt.y > gLMouse.y && rb.y < gLMouse.y )
        if(isInsideViewBox && gridSettings.canAddGLC(GLC,leftTop.x, leftTop.y)){
            gridSettings.addGLC( holdingGLC!!.first, GLC,leftTop.x, leftTop.y  )
            val transform = GLC.getGLCGirdTransform(leftTop , gridSettings)
            holdingGLC!!.second.first.setPosition(transform)
           // val gridPos = gridSettings.getGLCGirdTransform(gLMouse, GLC)

        }
        else if(oldIndex.x != -1 && gridSettings.canAddGLC(GLC,oldIndex.x, oldIndex.y)) {
            val transform = GLC.getGLCGirdTransform(oldIndex , gridSettings)
            holdingGLC!!.second.first.setPosition(transform)
            gridSettings.addGLC( holdingGLC!!.first, GLC,oldIndex.x, oldIndex.y  )
        }
        else{
            return //returning early because you it cant be placed
            //idk what to do here yet
        }
        gridMeshGenerator.clearShadow()
        holdingGLC!!.second.first.setScale(gridSettings.getScale() * gridSettings.blockSize)
        holdingGLC = null

    }



    private fun redoTransforms(){
        val gridSettings = controller.getSingleton<GridSettings>()
        val alreadyDone = mutableListOf<Int>()
        val occupationGrid = gridSettings.getOccupationGird()
        val gLCTransformComponents = controller.getDoubleComponents<GridLockedComponent, TransformComponent>()
        for(y in occupationGrid.indices){
            for(x in 0 until occupationGrid[0].size){
                val value = occupationGrid[y][x]
                if(value != -1 && !alreadyDone.contains(value)){
                    alreadyDone.add(value)
                    //println("id:$value  - left:$x top:$y ")
                    val pair = gLCTransformComponents[value] ?: continue
                    val transform = pair.first.getGLCGirdTransform(Vector2i(x,y) , gridSettings)
                    pair.second.setPosition(transform)
                    pair.second.setScale(gridSettings.getScale() * gridSettings.blockSize)

                }
            }
        }
    }

    private lateinit var viewBoxMesh : FlatMeshComponent
    private lateinit var viewClickBox : ClickBoxComponent
    private var oldViewBox : RectangleClickBox? = null
    private fun resizeViewBox(leftTop: Vector2f, rightBot : Vector2f){
        val gridSettings = controller.getSingleton<GridSettings>()
        gridSettings.setViewBox(leftTop, rightBot)
        gridMeshGenerator.generateGridBackground()

        redoTransforms()

        //this is pure for testing
        viewBoxMesh.clear()
        viewBoxMesh.addQuad(leftTop,rightBot)
        viewBoxMesh.setColor(0f,1f,1f,0.05f)
        viewBoxMesh.create()
        viewBoxMesh.depth = 0.1f

        if(oldViewBox != null) viewClickBox.removeClickBox( oldViewBox!! )
        oldViewBox = RectangleClickBox(gridSettings.viewBoxLeftTop, gridSettings.viewBoxRightBot)
        viewClickBox.addClickBox( oldViewBox!! )

    }


    override fun onWindowResize(width: Int, height: Int) {
        super.onWindowResize(width, height)
        if(! this::viewBoxMesh.isInitialized) {
            val id = controller.createEntity()
            viewBoxMesh = controller.assign<FlatMeshComponent>(id)
            viewClickBox = controller.assign<ClickBoxComponent>(id)
            viewClickBox.setOnClick {gLMouse,_ -> holdingGirdCorrection = gLMouse }
            viewClickBox.setOnLeave { _ -> holdingGirdCorrection = null ;oldGridPosition=null }
            viewClickBox.setOnRelease { _,_ -> holdingGirdCorrection = null;oldGridPosition=null }
        }
        val aspect = width.toFloat() / height.toFloat() //you can also take the aspect from the camera, but then you have to search the camera via the controller, its possible, but this is just the exact same thing but shorted
        resizeViewBox(
            Vector2f(-aspect + 0.6f,0.9f),
            Vector2f(aspect - 0.1f,-0.9f)
        )
    }

}
