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

    init {
        controller.addSingleton(Camera())
        controller.addSingleton(GridSettings(17, 10).setScale(0.9f).setBorderWidthPercentage(0.3f).setLocKYaxis(true))

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


    }
}

