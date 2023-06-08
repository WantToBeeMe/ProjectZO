package ZO.home

import ZO.game.InGameScene
import base.util.Colors
import base.util.Game
import base.util.IScene
import ecs.components.CameraComponent
import ecs.components.GridComponent
import ecs.components.TransformComponent
import ecs.components.mesh.FlatMeshComponent
import ecs.components.clickBox.CubeClickBox
import ecs.components.clickBox.ClickBoxComponent
import ecs.components.mesh.OpenMeshComponent
import ecs.systems.grid.MeshGridSystem
import ecs.systems.MeshInteractSystem
import ecs.systems.MeshRenderSystem
import org.joml.Vector2f


class HomeScene : IScene() {

    init{
        val cam = CameraComponent()
        controller.setSystems(
            MeshInteractSystem(cam),
            MeshRenderSystem(cam)
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
        val height = 0.2f
        val width = 0.8f

        val buttonID = controller.createEntity()
        val mesh = controller.assign<FlatMeshComponent>(buttonID)
        val b = Pair(Vector2f(-width/2,height/2),Vector2f(width/2,-height/2))
        mesh.addQuad(b.first,b.second)
        mesh.setColor(Colors.GRAY_LIGHT.get)
        mesh.create()
        controller.assign<ClickBoxComponent>(buttonID)
            .addClickBox( CubeClickBox(b.first,b.second) )
            .setOnRelease() {_,_,_ -> Game.changeScene(InGameScene()) }
    }

}

