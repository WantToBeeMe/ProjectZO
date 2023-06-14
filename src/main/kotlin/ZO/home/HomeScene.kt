package ZO.home

import ZO.game.InGameScene
import ZO.interactiveTest.InteractScene
import base.util.Colors
import base.util.Game
import base.util.IScene
import ecs.singletons.Camera
import ecs.components.TransformComponent
import ecs.components.mesh.FlatMeshComponent
import ecs.components.clickBox.RectangleClickBox
import ecs.components.clickBox.ClickBoxComponent
import ecs.components.mesh.OpenMeshComponent
import ecs.components.mesh.customTemplates.FlatCurvedBoxMesh
import ecs.systems.MeshInteractSystem
import ecs.systems.MeshRenderSystem
import org.joml.Vector2f


class HomeScene : IScene() {

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

        genOtherStuff()
    }

    private fun genOtherStuff(){
        val b = Button(controller,0.8f,0.2f ) {_,_ -> Game.changeScene(InteractScene())}
        b.transform.setRotation(20f)
    }

}

