package ZO.game

import ZO.custonEntities.GridMesh
import ecs.components.mesh.FlatCircleMesh
import base.util.Colors
import base.util.IScene
import ecs.components.CameraComponent
import ecs.components.TransformComponent
import ecs.components.mesh.FlatMeshComponent
import ecs.components.mesh.OpenMeshComponent
import org.joml.Vector2f


class InGameScene : IScene() {

    override fun start(){
        super.start()
        val camID = controller.createEntity()
        controller.assign<CameraComponent>(camID)

        GridMesh(controller, 11,6,0.8f)
    }

}

