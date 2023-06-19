package ecs.systems

import base.input.IMouseClickObserver
import base.input.Mouse
import base.util.Maf
import ecs.ECSController
import ecs.singletons.Camera
import ecs.components.TransformComponent
import ecs.components.clickBox.ClickBoxComponent
import ecs.components.mesh.FlatMeshComponent
import org.joml.Vector2f
import kotlin.math.cos
import kotlin.math.sin

object MeshInteractSystem : IEntityComponentSystem(), IMouseClickObserver {

    override fun start(controller: ECSController) {
        super.start(controller)
        Mouse.subscribe(this)
    }
    override fun stop() {
        super.stop()
        Mouse.unSubscribe(this)
    }

    override fun update(dt: Float) {
        super.update(dt)
        var hovering = false //this makes sure there can only be 1 hovering at a time
        val transforms = controller.getComponents<TransformComponent>()
        val flatClickBoxMeshes = controller.getDoubleComponents<FlatMeshComponent, ClickBoxComponent>()
        val sortedFlatClickBoxMeshes = flatClickBoxMeshes.entries.sortedByDescending { it.value.second.priority }

        val camera = controller.getSingleton<Camera>()
        val realMousePos = Maf.pixelToGLCords(Mouse.getX(),Mouse.getY(),camera.aspect)

        for ((entityID, clickBoxMesh) in sortedFlatClickBoxMeshes) {
            val transformedMousePos = Maf.revertTransform(realMousePos, transforms[entityID])
            if (clickBoxMesh.second.isInside( transformedMousePos ) && !hovering) {
                hovering = true
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
        val flatClickBoxMeshes = controller.getDoubleComponents<FlatMeshComponent, ClickBoxComponent>()
        val sortedFlatClickBoxMeshes = flatClickBoxMeshes.entries.sortedByDescending { it.value.second.priority }

        val camera = controller.getSingleton<Camera>()
        val realMousePos = Maf.pixelToGLCords(xPos.toFloat(),yPos.toFloat(),camera.aspect)

        for ((entityID, clickBoxMesh) in sortedFlatClickBoxMeshes) {
            val transformedMousePos = Maf.revertTransform(realMousePos, transforms[entityID])
            if (clickBoxMesh.second.isInside(transformedMousePos)){
                clickBoxMesh.second.onClickEvent?.let { it(realMousePos, button) }
                return
            }
        }
    }

    override fun onMouseRelease(xPos: Double, yPos: Double, button: Int) {
        val transforms = controller.getComponents<TransformComponent>()
        val flatClickBoxMeshes = controller.getDoubleComponents<FlatMeshComponent, ClickBoxComponent>()
        val sortedFlatClickBoxMeshes = flatClickBoxMeshes.entries.sortedByDescending { it.value.second.priority }

        val camera = controller.getSingleton<Camera>()
        val realMousePos = Maf.pixelToGLCords(xPos.toFloat(),yPos.toFloat(),camera.aspect)

        for ((entityID, clickBoxMesh) in sortedFlatClickBoxMeshes) {
            val transformedMousePos = Maf.revertTransform(realMousePos, transforms[entityID])
            if (clickBoxMesh.second.isInside(transformedMousePos)) {
                clickBoxMesh.second.onReleaseEvent?.let { it(realMousePos, button) }
                //return
            }
        }
    }
}