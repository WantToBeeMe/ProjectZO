package ZO.home

import base.util.IScene
import ecs.components.CameraComponent
import ecs.components.FlatMeshComponent
import ecs.components.TransformComponent
import ecs.components.clickBox.CubeClickBox
import ecs.components.clickBox.ClickBoxComponent
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
        mesh.addQuad(-width/2,height/2,width/2,-height/2)
        mesh.setColor(43/225f, 45/255f, 49/255f, 1f)
        mesh.create()
        controller.assign<ClickBoxComponent>(buttonID)
            .addClickBox( CubeClickBox(Vector2f(-width/2,height/2),Vector2f(width/2,-height/2))  )

    }

}

