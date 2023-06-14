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
import kotlin.math.cos
import kotlin.math.sin

object MeshGridSystem  : IEntityComponentSystem(), IMouseClickObserver {
    private const val edgeSpacingFactor: Float = 0.08f
    private const val cornerPercentage : Float = 0.48f
    private const val blockSpacingFactor : Float = 0.05f

    private lateinit var shadowGLC : GridLockedComponent
    private lateinit var shadowTransform : TransformComponent
    private lateinit var shadowMesh : FlatMeshComponent

    private lateinit var horizontalBarMesh : FlatMeshComponent
    private lateinit var horizontalBarTransform : TransformComponent
    private lateinit var horizontalBarClickBox : ClickBoxComponent
    private var clickBox : RectangleClickBox? = null
    private fun showShadow(width: Int, height:Int, aspect : Float){

        val gridSettings = controller.getSingleton<GridSettings>()
        val blockSize = gridSettings.blockSize
        val perBlockSpacing = blockSize * blockSpacingFactor
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
        val gLMouse = Maf.pixelToGLCords(Mouse.getX(),Mouse.getY(),aspect)
        val leftTop = gridSettings.getGLCLeftTopIndex(  gLMouse , shadowGLC)
        val transform = gridSettings.getGLCGirdTransform(leftTop , shadowGLC)
        shadowTransform.setPosition(transform)
    }
    private fun hideShadow(){
        shadowMesh.clear()
    }

    private fun horizontalBar(){ //the viewPercentage is the percentage of the screen you can see without moving
        val barSpacing = 0.02f
        val barHeight = 0.1f

        val camera = controller.getSingleton<Camera>()
        val settings = controller.getSingleton<GridSettings>()
        //calculating the stuff
        val mostRightPlusBorder = (settings.blockSize * settings.width / 2) + settings.borderWidth + (1-settings.screenHeight)
        val aspect = camera.aspect
        horizontalBarMesh.clear()
        val viewPercentage = aspect/mostRightPlusBorder
        if(viewPercentage >= 1f) return
        val bar = Pair(Vector2f( -aspect*viewPercentage + barSpacing, -1f+barHeight - barSpacing ),  Vector2f(aspect*viewPercentage - barSpacing , -1f + barSpacing))
        //remaking the mesh
        horizontalBarMesh.addMesh(FlatCurvedBoxMesh(bar.first, bar.second,barHeight*0.2f, 3))
        horizontalBarMesh.setColor(Colors.GRAY_LIGHT.get)
        horizontalBarMesh.create()
        horizontalBarMesh.depth = 1f
        //remaking the clickBox
        if(clickBox != null) horizontalBarClickBox.removeClickBox(clickBox!!)
        clickBox = RectangleClickBox(bar.first, bar.second)
        horizontalBarClickBox.addClickBox( clickBox!! )

    }

    var horizontalBarStartingX : Float? = null
    override fun create() {
        super.create()

        val shadowID = controller.createEntity()
        shadowMesh = controller.assign<FlatMeshComponent>(shadowID)
        shadowGLC = controller.assign<GridLockedComponent>(shadowID).setWidth(1).setHeight(1)
        shadowTransform = controller.assign<TransformComponent>(shadowID)
        //this doesn't look to good, I should rework the clickBox + make a viewBox, but still need to think about that
        // at least the reworking of the clickBox
        val horizontalBarID = controller.createEntity()
        horizontalBarMesh = controller.assign<FlatMeshComponent>(horizontalBarID)
        horizontalBarTransform = controller.assign<TransformComponent>(horizontalBarID)
        horizontalBarClickBox = controller.assign<ClickBoxComponent>(horizontalBarID)
        //horizontalBarClickBox.setWhileClick { _,realMousePos ->
        //    if(horizontalBarStartingX == null) horizontalBarStartingX = -realMousePos.x + horizontalBarTransform.getPosition().x
        //    horizontalBarTransform.setX(realMousePos.x + horizontalBarStartingX!! )
        //}
        //horizontalBarClickBox.setOnRelease {_,_,_ -> horizontalBarStartingX = null }
        //horizontalBarClickBox.setOnLeave() {_,_ -> horizontalBarStartingX = null }
        horizontalBar()

        val gridSettings = controller.getSingleton<GridSettings>()
        GridMeshGenerator(controller, gridSettings, edgeSpacingFactor,cornerPercentage,blockSpacingFactor)
        GLCBlock(controller,2,2, gridSettings, blockSpacingFactor)
        GLCBlock(controller,3,4, gridSettings, blockSpacingFactor)
        GLCBlock(controller,3,2, gridSettings, blockSpacingFactor)
        GLCBlock(controller,4,3, gridSettings, blockSpacingFactor)
        GLCBlock(controller,2,4, gridSettings, blockSpacingFactor)
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
        if(holding == null) return
        val gridSettings = controller.getSingleton<GridSettings>()
        val camera = controller.getSingleton<Camera>()
        val gLMouse = Maf.pixelToGLCords(Mouse.getX(),Mouse.getY(),camera.aspect)
        holding!!.second.first.setPosition(gLMouse)
        val leftTop = gridSettings.getGLCLeftTopIndex(  gLMouse , shadowGLC)
        val transform = gridSettings.getGLCGirdTransform(leftTop , shadowGLC)
        shadowTransform.setPosition(transform)
        if(gridSettings.canAddGLC(shadowGLC,leftTop.x, leftTop.y))
            shadowMesh.setColor(1f,1f,1f,0.1f)
        else shadowMesh.setColor(1f,0f,0f,0.1f)

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
                gLComponent.first.setScale(0.9f)
                showShadow(gLComponent.third.width, gLComponent.third.height, camera.aspect)
                break
            }
        }
    }

    override fun onMouseRelease(xPos: Double, yPos: Double, button: Int) {
        if(holding == null) return
        val gridSettings = controller.getSingleton<GridSettings>()
        val GLC = holding!!.second.third
        val camera = controller.getSingleton<Camera>()
        val leftTop = gridSettings.getGLCLeftTopIndex(  Maf.pixelToGLCords(xPos.toFloat(),yPos.toFloat(),camera.aspect) , GLC)
        if(gridSettings.canAddGLC(GLC,leftTop.x, leftTop.y)){
            gridSettings.addGLC( holding!!.first, GLC,leftTop.x, leftTop.y  )
            val transform = gridSettings.getGLCGirdTransform(leftTop , GLC)
            holding!!.second.first.setPosition(transform)
           // val gridPos = gridSettings.getGLCGirdTransform(gLMouse, GLC)

        }
        else if(oldIndex.x != -1 && gridSettings.canAddGLC(GLC,oldIndex.x, oldIndex.y)) {
            val transform = gridSettings.getGLCGirdTransform(oldIndex , GLC)
            holding!!.second.first.setPosition(transform)
            gridSettings.addGLC( holding!!.first, GLC,oldIndex.x, oldIndex.y  )
        }
        else{
            return //returning early because you it cant be placed
            //idk what to do here yet
        }
        hideShadow()
        holding!!.second.first.setScale(1f)
        holding = null
    }

    override fun onWindowResize(width: Int, height: Int) {
        super.onWindowResize(width, height)
        horizontalBar()
    }

}
