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
import ecs.components.mesh.FlatMeshComponent
import ecs.components.mesh.customTemplates.FlatCurvedBoxMesh
import ecs.systems.IEntityComponentSystem
import org.joml.Vector2f
import org.joml.Vector2i

object MeshGridSystem  : IEntityComponentSystem(), IMouseClickObserver {
    private const val edgeSpacingFactor: Float = 0.08f
    private const val cornerPercentage : Float = 0.48f
    private const val blockSpacingFactor : Float = 0.05f

    private var shadowGLC : GridLockedComponent? = null
    private var shadowTransform : TransformComponent? = null
    private var shadowMesh : FlatMeshComponent? = null
    private fun createShadow(width: Int, height:Int){
        val gridSettings = controller.getSingleton<GridSettings>()
        val borderWidth = gridSettings.borderWidth
        val blockSize = gridSettings.blockSize
        val perBlockSpacing = blockSize * blockSpacingFactor
        val camera = controller.getSingleton<Camera>()

        val shadowID = controller.createEntity()
        shadowMesh = controller.assign<FlatMeshComponent>(shadowID)
        shadowMesh!!.addMesh(
                FlatCurvedBoxMesh(
                        Vector2f( -(blockSize/2)*width + perBlockSpacing, (blockSize/2)*height  - perBlockSpacing),
                        Vector2f((blockSize/2)*width - perBlockSpacing, -(blockSize/2)*height  + perBlockSpacing),
                        borderWidth* 0.48f, 3)
        )
        shadowMesh!!.setColor(1f,1f,1f,0.1f).setVisualClickBox(false)
        shadowMesh!!.create()
        shadowMesh!!.depth = 0.05f
        shadowGLC = controller.assign<GridLockedComponent>(shadowID).setWidth(width).setHeight(height)
        shadowTransform = controller.assign<TransformComponent>(shadowID)
        val gLMouse = Maf.pixelToGLCords(Mouse.getX(),Mouse.getY(),camera.aspect)
        val leftTop = gridSettings.getGLCLeftTopIndex(  gLMouse , shadowGLC!!)
        val transform = gridSettings.getGLCGirdTransform(leftTop , shadowGLC!!)
        shadowTransform!!.setPosition(transform.x, transform.y)

    }
    fun deleteShadow(){
        if(shadowTransform == null) return
        shadowTransform!!.setScale(0f)
        shadowTransform = null
        shadowGLC = null
        shadowMesh!!.clear()
        shadowMesh = null
    }

    override fun create() {
        super.create()
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
        holding!!.second.first.setPosition(gLMouse.x,gLMouse.y)
        val leftTop = gridSettings.getGLCLeftTopIndex(  gLMouse , shadowGLC!!)
        val transform = gridSettings.getGLCGirdTransform(leftTop , shadowGLC!!)
        shadowTransform!!.setPosition(transform.x, transform.y)
        if(gridSettings.canAddGLC(shadowGLC!!,leftTop.x, leftTop.y))
            shadowMesh!!.setColor(1f,1f,1f,0.1f)
        else shadowMesh!!.setColor(1f,0f,0f,0.1f)

    }

    override fun onMouseClick(xPos: Double, yPos: Double, button: Int) {
        if(holding != null) return
        val gridSettings = controller.getSingleton<GridSettings>()
        val gLComponents = controller.getTripleComponents<TransformComponent, ClickBoxComponent, GridLockedComponent>()
        val camera = controller.getSingleton<Camera>()
        val gLMouse = Maf.pixelToGLCords(xPos.toFloat(),yPos.toFloat(),camera.aspect)
        for((gLCKey, gLComponent) in gLComponents){
            if(gLComponent.second.isInside(gLMouse,gLComponent.first)){
                holding = Pair(gLCKey,gLComponent)
                oldIndex = gridSettings.removeGLC(gLCKey)
                gLComponent.first.setScale(0.9f)
                createShadow(gLComponent.third.width, gLComponent.third.height)
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
            holding!!.second.first.setPosition(transform.x, transform.y)
           // val gridPos = gridSettings.getGLCGirdTransform(gLMouse, GLC)

        }
        else if(oldIndex.x != -1 && gridSettings.canAddGLC(GLC,oldIndex.x, oldIndex.y)) {
            val transform = gridSettings.getGLCGirdTransform(oldIndex , GLC)
            holding!!.second.first.setPosition(transform.x, transform.y)
            gridSettings.addGLC( holding!!.first, GLC,oldIndex.x, oldIndex.y  )
        }
        else{
            return //returning early because you it cant be placed
            //idk what to do here yet
        }
        deleteShadow()
        holding!!.second.first.setScale(1f)
        holding = null
    }



    //gLCBlock = GLCBlock(controller,2,4, gridSettings, blockSpacingFactor)
}