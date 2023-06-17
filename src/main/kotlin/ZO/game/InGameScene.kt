package ZO.game

import base.util.IScene
import ecs.singletons.Camera
import ecs.singletons.GridSettings
import ecs.components.GridLockedComponent
import ecs.components.TransformComponent
import ecs.components.clickBox.ClickBoxComponent
import ecs.components.mesh.FlatMeshComponent
import ecs.components.mesh.OpenMeshComponent
import ecs.components.mesh.customTemplates.FlatCurvedBoxMesh
import ecs.systems.grid.MeshGridSystem
import ecs.systems.MeshInteractSystem
import ecs.systems.MeshRenderSystem
import org.joml.Vector2f


class InGameScene : IScene() {
    private val viewBox = Pair(Vector2f(-0.8f,0.9f),  Vector2f(1.4f,-0.8f))

    init {
        controller.addSingleton(Camera())
        controller.addSingleton(GridSettings().setGrid(17, 10).setScale(0.9f).setBorderWidthPercentage(0.3f).setViewBox(viewBox.first, viewBox.second).setLocKYaxis(true))

        controller.setSystems(
                MeshInteractSystem,
                MeshRenderSystem,
                MeshGridSystem,
        )

        controller.setComponentTypes(
                TransformComponent::class,
                FlatMeshComponent::class,
                OpenMeshComponent::class,
                ClickBoxComponent::class,
                GridLockedComponent::class,
        )
    }

    override fun start() {
        super.start()

        val viewBoxID = controller.createEntity()
       val  viewBoxMesh = controller.assign<FlatMeshComponent>(viewBoxID)
        viewBoxMesh.addQuad(viewBox.first,viewBox.second)
        viewBoxMesh.setColor(0f,1f,1f,0.05f)
        viewBoxMesh.create()
        viewBoxMesh.depth = 0.1f



    }
}

