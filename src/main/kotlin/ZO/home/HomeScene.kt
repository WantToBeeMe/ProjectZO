package ZO.home

import ZO.game.InGameScene
import base.util.Colors
import base.util.Game
import base.util.IScene
import ecs.components.CameraComponent
import ecs.components.mesh.FlatMeshComponent
import ecs.components.clickBox.CubeClickBox
import ecs.components.clickBox.ClickBoxComponent
import org.joml.Vector2f


class HomeScene : IScene() {

    override fun start(){
        super.start()
        val camID = controller.createEntity()
        controller.assign<CameraComponent>(camID)
        genBackground()
    }

    private fun genBackground(){
        val height = 0.2f
        val width = 0.8f

        val buttonID = controller.createEntity()
        val mesh = controller.assign<FlatMeshComponent>(buttonID)
        val b = Pair(Vector2f(-width/2,height/2),Vector2f(width/2,-height/2))
        mesh.addQuad(b.first,b.second)
        mesh.setColor(Colors.GRAY_NORMAL.get)
        mesh.setColor(Colors.GRAY_LIGHT.get)
        mesh.create()
        controller.assign<ClickBoxComponent>(buttonID)
            .addClickBox( CubeClickBox(b.first,b.second) )
            .setOnRelease() {_,_,_ -> Game.changeScene(InGameScene()) }
    }

}

