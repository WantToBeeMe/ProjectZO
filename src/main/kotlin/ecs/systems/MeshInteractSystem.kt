package ecs.systems

import base.input.IMouseClickObserver
import base.input.Mouse
import base.shader.Shader
import base.shader.ShaderObject
import base.util.Maf
import ecs.ECSController
import ecs.components.CameraComponent
import ecs.components.TransformComponent
import ecs.components.clickBox.ClickBoxComponent
import ecs.components.mesh.FlatMeshComponent
import imgui.ImBool
import imgui.ImGui
import org.joml.Vector2f
import org.joml.Vector4f

class MeshInteractSystem(private val defaultCam : CameraComponent)  : IEntityComponentSystem() , IMouseClickObserver{
    private val currentShader : ShaderObject = Shader.FLAT_OBJECT.get()
    private val noTransform =  TransformComponent().transform
    private val showClickBox = ImBool(false)

    override fun stop() {
        super.stop()
        Mouse.unSubscribe(this)
    }
    override fun start(controller: ECSController) {
        super.start(controller)
        Mouse.subscribe(this)
    }

    override fun update(dt: Float) {
        super.update(dt)
        val transforms = controller.getComponents<TransformComponent>()
        val flatClickBoxMeshes = controller.getDoubleComponents<FlatMeshComponent, ClickBoxComponent>()

        val mouseX = Mouse.getX()
        val mouseY = Mouse.getY()
        val realMousePos = Maf.pixelToGLCords(mouseX,mouseY,defaultCam.aspect)//getRealMousePosition(mouseX.toDouble(), mouseY.toDouble())

        for ((entityID, clickBoxMesh) in flatClickBoxMeshes) {
            //val transformedMousePos = transformPoint()
            if (clickBoxMesh.second.isInside(realMousePos, transforms[entityID])) {
                if (clickBoxMesh.first.clickBoxInteract == 1) {
                    clickBoxMesh.second.whileHoverEvent?.let { it(Vector2f(mouseX, mouseY), realMousePos) }
                } else {
                    clickBoxMesh.second.onEnterEvent?.let { it(Vector2f(mouseX, mouseY), realMousePos) }
                    clickBoxMesh.first.clickBoxInteract = 1
                }
                if (clickBoxMesh.second.hold) {
                    clickBoxMesh.second.whileClickEvent?.let { it(Vector2f(mouseX, mouseY), realMousePos) }
                    clickBoxMesh.first.clickBoxInteract = 2
                }
            } else if (clickBoxMesh.first.clickBoxInteract != 0) {
                clickBoxMesh.second.hold = false
                clickBoxMesh.second.onLeaveEvent?.let { it(Vector2f(mouseX, mouseY), realMousePos) }
                clickBoxMesh.first.clickBoxInteract = 0
            }
        }

        if(showClickBox.get()){

            currentShader.use()
            currentShader.enableBlend()
            currentShader.enableDepthTest()
            currentShader.uploadMat4f("uProjection", defaultCam.projectionMatrix )
            currentShader.uploadMat4f("uView",defaultCam.viewMatrix)
            currentShader.uploadMat3f("uTransform", noTransform)
            currentShader.uploadFloat("uDepth", 1f)
            currentShader.uploadInt("uInteract", 0)

            for((entityID, clickBoxMesh) in flatClickBoxMeshes){
                if(!clickBoxMesh.second.showDebugLines) continue
                currentShader.uploadVec4f("uColor", Vector4f(1f,1f,clickBoxMesh.first.clickBoxInteract.toFloat(),1f))
                clickBoxMesh.second.renderClickBox()
                //tranform hasbeen moved to the ClickBoxComp. so that doesnt work here anymore
               //val transformedMousePos  = ClickBoxComponent.transformPoint(realMousePos, transforms[entityID])
               //currentShader.uploadVec4f("uColor", Vector4f(1f,0f,1f,1f))
               //MouseClickBoxVisualizer.renderMouse(transformedMousePos.x, transformedMousePos.y)
            }

            currentShader.detach()
        }
    }






    override fun guiOptions() {
        if (ImGui.beginTabItem("ClickBox" )) {
            ImGui.checkbox("Show ClickBoxes",showClickBox)
            ImGui.endTabItem();
        }
    }

    override fun onMouseClick(xPos: Double, yPos: Double, button: Int) {
        val transforms = controller.getComponents<TransformComponent>()
        val flatClickBoxMeshes = controller.getDoubleComponents<FlatMeshComponent, ClickBoxComponent>()
        val realMousePos = Maf.pixelToGLCords(xPos.toFloat(),yPos.toFloat(),defaultCam.aspect)

        for ((entityID, clickBoxMesh) in flatClickBoxMeshes) {
            if (clickBoxMesh.second.onClickEvent == null && clickBoxMesh.second.whileClickEvent == null) continue
            //val transformedMousePos = transformPoint(realMousePos, transforms[entityID])
            if (clickBoxMesh.second.isInside(realMousePos, transforms[entityID])) {
                clickBoxMesh.second.onClickEvent?.let { it(Vector2f(xPos.toFloat(), yPos.toFloat()), realMousePos, button) }
                clickBoxMesh.second.hold = true
            }
        }
    }

    override fun onMouseRelease(xPos: Double, yPos: Double, button: Int) {
        val transforms = controller.getComponents<TransformComponent>()
        val flatClickBoxMeshes = controller.getDoubleComponents<FlatMeshComponent, ClickBoxComponent>()
        val realMousePos = Maf.pixelToGLCords(xPos.toFloat(),yPos.toFloat(),defaultCam.aspect)

        for ((entityID, clickBoxMesh) in flatClickBoxMeshes) {
            if (clickBoxMesh.second.onReleaseEvent == null && clickBoxMesh.second.whileClickEvent == null) continue
            //val transformedMousePos = transformPoint(realMousePos, transforms[entityID])
            if (clickBoxMesh.second.isInside(realMousePos, transforms[entityID])) {
                clickBoxMesh.second.onReleaseEvent?.let { it(Vector2f(xPos.toFloat(), yPos.toFloat()), realMousePos, button) }
                clickBoxMesh.second.hold = false
            }
        }
    }

    override fun onWindowResize(width: Int, height: Int) {
        defaultCam.resizeViewPort(width.toFloat(), height.toFloat())
    }

}