package ZO.home

import ZO.game.InGameScene
import base.util.Colors
import base.util.Game
import base.util.IScene
import ecs.singletons.Camera
import ecs.components.TransformComponent
import ecs.components.mesh.FlatMeshComponent
import ecs.components.clickBox.CubeClickBox
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

        genBackground()
    }

    private fun genBackground(){
        val height = 0.2f
        val width = 0.8f
        val size = Pair(Vector2f(-width/2,height/2),Vector2f(width/2,-height/2))

        val buttonID = controller.createEntity()
        val mesh = controller.assign<FlatMeshComponent>(buttonID)
        mesh.addMesh(FlatCurvedBoxMesh(size.first,size.second,0.025f ))
        mesh.setColor(Colors.GRAY_LIGHT.get)
        mesh.create()
        controller.assign<ClickBoxComponent>(buttonID)
            .addClickBox( CubeClickBox(size.first,size.second) )
            .setOnRelease() {_,_,_ -> Game.changeScene(InGameScene()) }
    }

}

