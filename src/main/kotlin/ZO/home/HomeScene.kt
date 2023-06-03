package ZO.home

import ZO.game.InGameScene
import base.util.Game
import base.util.IScene
import ecs.components.CameraComponent
import ecs.components.mesh.FlatMeshComponent
import ecs.components.TransformComponent
import ecs.components.clickBox.CircleClickBox
import ecs.components.clickBox.CubeClickBox
import ecs.components.clickBox.ClickBoxComponent
import ecs.components.mesh.FlatCircleMesh
import org.joml.Vector2f


class HomeScene : IScene() {

    override fun start(){
        super.start()
        genBackground()
    }

    override fun loop(dt : Float){
        super.loop(dt)
    }


    private fun genBackground(){
        val camID = controller.createEntity()
        controller.assign<CameraComponent>(camID)

        val height = 0.2f
        val width = 0.8f

        val buttonID = controller.createEntity()
        val mesh = controller.assign<FlatMeshComponent>(buttonID)
        val b = Pair(Vector2f(-width/2,height/2),Vector2f(width/2,-height/2))
        mesh.addQuad(b.first,b.second)
        mesh.setColor(43/225f, 45/255f, 49/255f, 1f)
        mesh.create()
        val t = controller.assign<TransformComponent>(buttonID)
        controller.assign<ClickBoxComponent>(buttonID)
            .addClickBox( CubeClickBox(b.first,b.second) )
            //.setOnRelease() {_,_,_ -> Game.changeScene(InGameScene()) }
            .setWhileClick() {_,realPos -> t.setPosition(realPos.x, realPos.y) }
            .showDebugLines = true
    }

}

