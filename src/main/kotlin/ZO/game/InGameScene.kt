package ZO.game

import ZO.custonEntities.GridMesh
import base.util.IScene
import ecs.components.CameraComponent


class InGameScene : IScene() {

    override fun start(){
        super.start()
        val camID = controller.createEntity()
        controller.assign<CameraComponent>(camID)

        GridMesh(controller, arrayOf(
            booleanArrayOf(true,true,true,true,true,true),
            booleanArrayOf(true,true,true,false,true,true),
            booleanArrayOf(true,false,true,false,false,true),
            booleanArrayOf(true,true,true,true,true,true),
        ),0.8f)
    }

}

