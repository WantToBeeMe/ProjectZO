package ZO.interactiveTest

import ZO.game.InGameScene
import ZO.home.Button
import base.util.Game
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
        genOtherStuff()
    }

    private fun genOtherStuff(){
        val bl = Button(controller,1.2f,1.6f ) {_,_ -> Game.changeScene(InGameScene())}
        val bm = Button(controller,1.2f,1.6f ) {_,_ -> Game.changeScene(InGameScene())}
        val br = Button(controller,1.2f,1.6f ) {_,_ -> Game.changeScene(InGameScene())}
        bl.transform.setX(-1.4f)
        br.transform.setX(1.4f)
    }


}
