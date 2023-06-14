package ecs.systems

import base.input.IMouseClickObserver
import base.input.Mouse
import base.shader.Shader
import base.shader.ShaderObject
import base.util.Maf
import ecs.ECSController
import ecs.singletons.Camera
import ecs.components.TransformComponent
import ecs.components.clickBox.NewClickBoxComponent
import ecs.components.mesh.FlatMeshComponent
import imgui.ImBool
import imgui.ImGui
import org.joml.Vector2f
import org.joml.Vector4f
import kotlin.math.cos
import kotlin.math.sin

object NewMeshInteractSystem : IEntityComponentSystem(), IMouseClickObserver {

    override fun stop() {
        super.stop()
        Mouse.subscribe(this)
    }
    override fun start(controller: ECSController) {
        super.start(controller)
        Mouse.unSubscribe(this)
    }

    override fun update(dt: Float) {
        super.update(dt)
        val transforms = controller.getComponents<TransformComponent>()
        val flatClickBoxMeshes = controller.getDoubleComponents<FlatMeshComponent, NewClickBoxComponent>()
        val camera = controller.getSingleton<Camera>()
        val realMousePos = Maf.pixelToGLCords(Mouse.getX(),Mouse.getY(),camera.aspect)

        for ((entityID, clickBoxMesh) in flatClickBoxMeshes) {
            val transformedMousePos = transformPoint(realMousePos, transforms[entityID])
            if (clickBoxMesh.second.isInside( transformedMousePos )) {
                if (!clickBoxMesh.second.hovering) {
                    clickBoxMesh.second.hovering = true
                    clickBoxMesh.second.onEnterEvent?.let { it(realMousePos) }
                }
            } else if (clickBoxMesh.second.hovering) {
                clickBoxMesh.second.hovering = false
                clickBoxMesh.second.onLeaveEvent?.let { it(realMousePos) }
            }
        }
    }

    override fun onMouseClick(xPos: Double, yPos: Double, button: Int) {
        val transforms = controller.getComponents<TransformComponent>()
        val flatClickBoxMeshes = controller.getDoubleComponents<FlatMeshComponent, NewClickBoxComponent>()
        val camera = controller.getSingleton<Camera>()
        val realMousePos = Maf.pixelToGLCords(xPos.toFloat(),yPos.toFloat(),camera.aspect)

        for ((entityID, clickBoxMesh) in flatClickBoxMeshes) {
            val transformedMousePos = transformPoint(realMousePos, transforms[entityID])
            if (clickBoxMesh.second.isInside(transformedMousePos))
                clickBoxMesh.second.onClickEvent?.let { it(realMousePos, button) }
        }
    }

    override fun onMouseRelease(xPos: Double, yPos: Double, button: Int) {
        val transforms = controller.getComponents<TransformComponent>()
        val flatClickBoxMeshes = controller.getDoubleComponents<FlatMeshComponent, NewClickBoxComponent>()
        val camera = controller.getSingleton<Camera>()
        val realMousePos = Maf.pixelToGLCords(xPos.toFloat(),yPos.toFloat(),camera.aspect)

        for ((entityID, clickBoxMesh) in flatClickBoxMeshes) {
            val transformedMousePos = transformPoint(realMousePos, transforms[entityID])
            if (clickBoxMesh.second.isInside(transformedMousePos))
                clickBoxMesh.second.onReleaseEvent?.let { it(realMousePos, button) }
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

}