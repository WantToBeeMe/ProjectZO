package ecs.systems

import base.input.IMouseClickObserver
import base.input.Mouse
import base.shader.Shader
import base.shader.ShaderObject
import base.util.Window
import ecs.ECSController
import ecs.components.CameraComponent
import ecs.components.TransformComponent
import ecs.components.clickBox.ClickBoxComponent
import ecs.components.clickBox.MouseClickBoxVisualizer
import ecs.components.mesh.FlatMeshComponent
import imgui.ImBool
import imgui.ImGui
import org.joml.Vector2f
import org.joml.Vector4f
import kotlin.math.cos
import kotlin.math.sin

object MeshInteractSystem : IEntityComponentSystem() , IMouseClickObserver{
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
        val realMousePos = getRealMousePosition(mouseX.toDouble(), mouseY.toDouble())

        for ((entityID, clickBoxMesh) in flatClickBoxMeshes) {
            val transformedMousePos = transformPoint(realMousePos, transforms[entityID])
            if (clickBoxMesh.second.isInside(transformedMousePos)) {
                if (clickBoxMesh.first.interact == 1) {
                    clickBoxMesh.second.whileHoverEvent?.let { it(Vector2f(mouseX, mouseY), realMousePos) }
                } else {
                    clickBoxMesh.second.onEnterEvent?.let { it(Vector2f(mouseX, mouseY), realMousePos) }
                    clickBoxMesh.first.interact = 1
                }
                if (clickBoxMesh.second.hold) {
                    clickBoxMesh.second.whileClickEvent?.let { it(Vector2f(mouseX, mouseY), realMousePos) }
                    clickBoxMesh.first.interact = 2
                }
            } else if (clickBoxMesh.first.interact != 0) {
                clickBoxMesh.second.hold = false
                clickBoxMesh.second.onLeaveEvent?.let { it(Vector2f(mouseX, mouseY), realMousePos) }
                clickBoxMesh.first.interact = 0
            }
        }

        if(showClickBox.get()){
            val cameras = controller. getComponents<CameraComponent>()
            val camera=  cameras[cameras.keys.first()]!!

            currentShader.use()
            currentShader.enableBlend()
            currentShader.enableDepthTest()
            currentShader.uploadMat4f("uProjection", camera.projectionMatrix )
            currentShader.uploadMat4f("uView",camera.viewMatrix)
            currentShader.uploadMat3f("uTransform", noTransform)
            currentShader.uploadFloat("uDepth", 1f)
            currentShader.uploadInt("uInteract", 0)

            for((entityID, clickBoxMesh) in flatClickBoxMeshes){
                if(!clickBoxMesh.second.showDebugLines) continue
                currentShader.uploadVec4f("uColor", Vector4f(1f,1f,clickBoxMesh.first.interact.toFloat(),1f))
                clickBoxMesh.second.renderClickBox()
                val transformedMousePos  = transformPoint(realMousePos, transforms[entityID])
                currentShader.uploadVec4f("uColor", Vector4f(1f,0f,1f,1f))
                MouseClickBoxVisualizer.renderMouse(transformedMousePos.x, transformedMousePos.y)
            }

            currentShader.detach()
        }
    }



    private fun transformPoint(point: Vector2f, transform: TransformComponent? ): Vector2f {
        if(transform == null) return point
        //translation
        var newPoint = Vector2f(point).add(Vector2f(transform.getPosition()).mul(-1f))

        //rotate
        val angleRad = transform.getRotation(true)
        val cos = cos(angleRad.toDouble()).toFloat()
        val sin = sin(angleRad.toDouble()).toFloat()
        newPoint = Vector2f(newPoint.x * cos - newPoint.y * sin,  newPoint.x * sin + newPoint.y * cos)
        //scale
        return newPoint.mul(Vector2f(1f).div(transform.getScale()))
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
        val realMousePos = getRealMousePosition(xPos, yPos)

        for ((entityID, clickBoxMesh) in flatClickBoxMeshes) {
            if (clickBoxMesh.second.onClickEvent == null && clickBoxMesh.second.whileClickEvent == null) continue
            val transformedMousePos = transformPoint(realMousePos, transforms[entityID])
            if (clickBoxMesh.second.isInside(transformedMousePos)) {
                clickBoxMesh.second.onClickEvent?.let { it(Vector2f(xPos.toFloat(), yPos.toFloat()), realMousePos, button) }
                clickBoxMesh.second.hold = true
            }
        }
    }

    override fun onMouseRelease(xPos: Double, yPos: Double, button: Int) {
        val transforms = controller.getComponents<TransformComponent>()
        val flatClickBoxMeshes = controller.getDoubleComponents<FlatMeshComponent, ClickBoxComponent>()
        val realMousePos = getRealMousePosition(xPos, yPos)

        for ((entityID, clickBoxMesh) in flatClickBoxMeshes) {
            if (clickBoxMesh.second.onReleaseEvent == null && clickBoxMesh.second.whileClickEvent == null) continue
            val transformedMousePos = transformPoint(realMousePos, transforms[entityID])
            if (clickBoxMesh.second.isInside(transformedMousePos)) {
                clickBoxMesh.second.onReleaseEvent?.let { it(Vector2f(xPos.toFloat(), yPos.toFloat()), realMousePos, button) }
                clickBoxMesh.second.hold = false
            }
        }
    }

    private fun getRealMousePosition(xPos: Double, yPos: Double): Vector2f {
        val mouseX = xPos.toFloat()
        val mouseY = yPos.toFloat()
        val cameras = controller.getComponents<CameraComponent>()
        val cameraID = cameras.keys.first()
        return Vector2f(((mouseX / Window.getWidth()) * 2f - 1f) * cameras[cameraID]!!.aspect, -(mouseY / Window.getHeight()) * 2f + 1f)
    }

}