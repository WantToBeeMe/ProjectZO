package ZO.game

import ZO.custonEntities.GridMesh
import base.util.IScene
import ecs.components.CameraComponent


class InGameScene : IScene() {

    override fun start(){
        super.start()
        val camID = controller.createEntity()
        controller.assign<CameraComponent>(camID)


       GridMesh(controller,
              //arrayOf(
              //booleanArrayOf(true,true,false,true,true,true,true,true,false),
              //booleanArrayOf(true,true,true,true,false,true,false,true,false),
              //booleanArrayOf(true,true,false,false,false,true,false,false,true),
              //booleanArrayOf(true,true,true,true,true,true,true,false,true),
              //booleanArrayOf(true,true,true,true,true,true,true,false,true),
              //booleanArrayOf(false,true,false,false,true,true,true,false,false),)
               10,6
       ,0.9f)
    }

}

