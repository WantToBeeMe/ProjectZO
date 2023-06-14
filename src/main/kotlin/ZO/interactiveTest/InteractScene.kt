package ZO.interactiveTest

import base.util.IScene
import ecs.components.TransformComponent
import ecs.components.clickBox.ClickBoxComponent
import ecs.components.mesh.FlatMeshComponent
import ecs.components.mesh.OpenMeshComponent
import ecs.singletons.Camera
import ecs.systems.MeshInteractSystem
import ecs.systems.MeshRenderSystem


class InteractScene : IScene() {

    init{
        controller.addSingleton(Camera())
        controller.setSystems(
            MeshInteractSystem,
            MeshRenderSystem
        )
        controller.setComponentTypes(
            TransformComponent::class,
            FlatMeshComponent::class,
            OpenMeshComponent::class,
            ClickBoxComponent::class,
        )

        genBackground()
    }

    private fun genBackground(){
        Button(controller,0.8f,0.2f )
    }


}
