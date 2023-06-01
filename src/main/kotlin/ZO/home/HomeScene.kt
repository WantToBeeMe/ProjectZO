package ZO.home

import base.util.IScene
import ecs.components.CameraComponent
import ecs.components.FlatMeshComponent
import ecs.components.TransformComponent
import ecs.components.hitbox.BoxHitBox
import ecs.components.hitbox.HitBoxComponent
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
        mesh.setColor(1f,1f,0f, 1f)
        mesh.create()
        controller.assign<HitBoxComponent>(buttonID)
                .addHitBox( BoxHitBox(Vector2f(-width/2,height/2),Vector2f(width/2,-height/2))  )
        val transform = controller.assign<TransformComponent>(buttonID)
        transform.setPosition(0.5f,0.1f)
    }


}

