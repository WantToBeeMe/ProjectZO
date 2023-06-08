package ecs.systems.grid

import base.input.Mouse
import base.util.Colors
import base.util.Window
import ecs.components.CameraComponent
import ecs.components.GridComponent
import ecs.components.TransformComponent
import ecs.components.mesh.FlatMeshComponent
import ecs.components.mesh.OpenMeshComponent
import ecs.components.mesh.customTemplates.FlatCurvedBoxMesh
import ecs.systems.IEntityComponentSystem
import org.joml.Vector2f

class MeshGridSystem(private val gridSettings : GridComponent, private val defaultCam : CameraComponent)  : IEntityComponentSystem() {

    private lateinit var blockMeshTransform : TransformComponent
    override fun create() {
        super.create()
        GridMesh(controller, gridSettings)

        val tempPerBlock = gridSettings.screenHeight*2/gridSettings.grid.size
        val borderEdgeWidth = tempPerBlock*gridSettings.edgeWidthPercentage
        val perBlock = tempPerBlock-(borderEdgeWidth*2/gridSettings.grid.size)

        val blockID = controller.createEntity()
        val blockMesh = controller.assign<FlatMeshComponent>(blockID)
        blockMesh.addMesh(
            FlatCurvedBoxMesh(
                Vector2f(-perBlock/2, perBlock/2),
                Vector2f(perBlock/2, -perBlock/2),
                borderEdgeWidth* 0.48f, 3)

        )
        blockMesh.setColor(Colors.GRAY_LIGHT.get)
        blockMesh.create()
        blockMesh.depth = 0.1f

        blockMeshTransform = controller.assign<TransformComponent>(blockID)
    }

    override fun update(dt: Float) {
        super.update(dt)
        val mousePos = Vector2f(
            ((Mouse.getX() / Window.getWidth()) * 2f - 1f) * defaultCam.aspect,
            -( Mouse.getY() / Window.getHeight()) * 2f + 1f)

        blockMeshTransform.setPosition(mousePos.x, mousePos.y)

    }


}