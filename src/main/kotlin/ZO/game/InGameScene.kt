package ZO.game

import base.util.IScene
import ecs.singletons.Camera
import ecs.singletons.GridSettings
import ecs.components.GridLockedComponent
import ecs.components.TransformComponent
import ecs.components.clickBox.ClickBoxComponent
import ecs.components.mesh.FlatMeshComponent
import ecs.components.mesh.OpenMeshComponent
import ecs.systems.grid.MeshGridSystem
import ecs.systems.MeshInteractSystem
import ecs.systems.MeshRenderSystem


class InGameScene : IScene() {

    init {
        controller.addSingleton(Camera())
        controller.addSingleton(GridSettings().setGrid(17, 10).setScale(0.9f).setBorderWidthPercentage(0.3f))

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
}

