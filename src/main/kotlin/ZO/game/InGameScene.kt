package ZO.game

import base.util.IScene
import ecs.components.CameraComponent
import ecs.components.GridComponent
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
        val cam = CameraComponent()
        val grid = GridComponent().setGrid(16, 10).setScreenHeight(0.9f).setEdgeWidthPercentage(0.3f)

        controller.setSystems(
                MeshInteractSystem(cam),
                MeshRenderSystem(cam),
                MeshGridSystem(grid, cam),
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

