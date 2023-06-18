package ecs.systems.grid

import ZO.game.GLCBlock
import base.input.IMouseClickObserver
import base.input.Mouse
import base.util.Colors
import base.util.Maf
import ecs.ECSController
import ecs.singletons.Camera
import ecs.singletons.GridSettings
import ecs.components.GridLockedComponent
import ecs.components.TransformComponent
import ecs.components.clickBox.ClickBoxComponent
import ecs.components.clickBox.RectangleClickBox
import ecs.components.mesh.FlatMeshComponent
import ecs.components.mesh.customTemplates.FlatCurvedBoxMesh
import ecs.systems.IEntityComponentSystem
import org.joml.Vector2f
import org.joml.Vector2i
import org.joml.Vector4f

object MeshGridSystem  : IEntityComponentSystem(), IMouseClickObserver {

    private lateinit var gridMeshGenerator : GridMeshGenerator

    override fun create() {
        super.create()
        val gridSettings = controller.getSingleton<GridSettings>()

        gridMeshGenerator = GridMeshGenerator(controller).setSettings(gridSettings)
        gridMeshGenerator.generateGridBackground()

        GLCBlock(controller,1,1, gridSettings.blockEdgeShorteningPercentage, gridSettings.getCornerRadius() / gridSettings.blockSize )
        GLCBlock(controller,2,2, gridSettings.blockEdgeShorteningPercentage, gridSettings.getCornerRadius() / gridSettings.blockSize )
       // GLCBlock(controller,3,4, gridSettings.blockEdgeShorteningPercentage, gridSettings.getCornerRadius() / gridSettings.blockSize )
        GLCBlock(controller,3,2, gridSettings.blockEdgeShorteningPercentage, gridSettings.getCornerRadius() / gridSettings.blockSize )
       // GLCBlock(controller,4,3, gridSettings.blockEdgeShorteningPercentage, gridSettings.getCornerRadius() / gridSettings.blockSize )
       // GLCBlock(controller,2,4, gridSettings.blockEdgeShorteningPercentage, gridSettings.getCornerRadius() / gridSettings.blockSize )
    }
    override fun stop() {
        super.stop()
        Mouse.unSubscribe(this)
    }
    override fun start(controller: ECSController) {
        super.start(controller)
        Mouse.subscribe(this)
    }

    private var oldIndex = Vector2i(-1)
    private var holding: Pair<Int,Triple<TransformComponent, ClickBoxComponent, GridLockedComponent>>? = null
    override fun update(dt: Float) {
        super.update(dt)
        val camera = controller.getSingleton<Camera>()
        val gridSettings = controller.getSingleton<GridSettings>()
        val gLMouse = Maf.pixelToGLCords(Mouse.getX(),Mouse.getY(),camera.aspect)
        val lt = gridSettings.viewBoxLeftTop
        val rb = gridSettings.viewBoxRightBot
        val isInside = (lt.x < gLMouse.x && rb.x > gLMouse.x) && ( lt.y > gLMouse.y && rb.y < gLMouse.y )
        if(holding == null) return
        holding!!.second.first.setPosition(gLMouse)
        if(isInside)
            gridMeshGenerator.showShadow(gLMouse)
        else
            gridMeshGenerator.hideShadow()
    }

    override fun onMouseClick(xPos: Double, yPos: Double, button: Int) {
        if(holding != null) return
        val gridSettings = controller.getSingleton<GridSettings>()
        val gLComponents = controller.getTripleComponents<TransformComponent, ClickBoxComponent, GridLockedComponent>()
        val camera = controller.getSingleton<Camera>()
        val gLMouse = Maf.pixelToGLCords(xPos.toFloat(),yPos.toFloat(),camera.aspect)
        for((gLCKey, gLComponent) in gLComponents){
            if(gLComponent.second.isInside(Maf.revertTransform(gLMouse,gLComponent.first))){
                holding = Pair(gLCKey,gLComponent)
                oldIndex = gridSettings.removeGLC(gLCKey)
                gLComponent.first.setScale(gridSettings.getScale() * gridSettings.blockSize * 0.9f)
                gridMeshGenerator.createShadow(gLComponent.third)
                break
            }
        }
    }

    override fun onMouseRelease(xPos: Double, yPos: Double, button: Int) {
        if(holding == null) return
        val gridSettings = controller.getSingleton<GridSettings>()
        val GLC = holding!!.second.third
        val camera = controller.getSingleton<Camera>()
        val gLMouse = Maf.pixelToGLCords(xPos.toFloat(),yPos.toFloat(),camera.aspect)
        val leftTop = GLC.getGLCLeftTopIndex(  gLMouse , gridSettings)
        val lt = gridSettings.viewBoxLeftTop
        val rb = gridSettings.viewBoxRightBot
        val isInside = (lt.x < gLMouse.x && rb.x > gLMouse.x) && ( lt.y > gLMouse.y && rb.y < gLMouse.y )
        if(isInside && gridSettings.canAddGLC(GLC,leftTop.x, leftTop.y)){
            gridSettings.addGLC( holding!!.first, GLC,leftTop.x, leftTop.y  )
            val transform = GLC.getGLCGirdTransform(leftTop , gridSettings)
            holding!!.second.first.setPosition(transform)
           // val gridPos = gridSettings.getGLCGirdTransform(gLMouse, GLC)

        }
        else if(oldIndex.x != -1 && gridSettings.canAddGLC(GLC,oldIndex.x, oldIndex.y)) {
            val transform = GLC.getGLCGirdTransform(oldIndex , gridSettings)
            holding!!.second.first.setPosition(transform)
            gridSettings.addGLC( holding!!.first, GLC,oldIndex.x, oldIndex.y  )
        }
        else{
            return //returning early because you it cant be placed
            //idk what to do here yet
        }
        gridMeshGenerator.clearShadow()
        holding!!.second.first.setScale(gridSettings.getScale() * gridSettings.blockSize)
        holding = null

    }

    //override fun onWindowResize(width: Int, height: Int) {
    //    super.onWindowResize(width, height)
    //}

}
