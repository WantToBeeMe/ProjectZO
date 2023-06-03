package ZO.game

import base.util.IScene
import ecs.components.CameraComponent
import ecs.components.OpenMeshComponent
import org.joml.Vector4f


class InGameScene : IScene() {

    private val dotSystem = DotSystem(controller)
    override fun start(){
        super.start()
        genBackground()
        dotSystem.start()
    }

    override fun loop(dt : Float){
        super.loop(dt)
    }

    override fun stop() {
        super.stop()
        dotSystem.stop()
    }

    private fun genBackground(){
        val camID = controller.createEntity()
        controller.assign<CameraComponent>(camID)

        val dist1 = 0.10f //distance to the window borders
        val dist2 = 0.03f + dist1// the size of the border
        val colorBorder = Vector4f(49/225f, 51/255f, 56/255f, 1f)
        val colorInner = Vector4f(43/225f, 45/255f, 49/255f, 1f)

        val backgroundID = controller.createEntity()
        val backgroundMesh = controller.assign<OpenMeshComponent>(backgroundID)
        backgroundMesh.addQuad(-1f * (16f/9f) + dist1,1f -dist1,1f * (16f/9f) -dist1,-1f + dist1 , colorBorder)
        backgroundMesh.addQuad(-1f * (16f/9f) +dist2,1f -dist2,1f * (16f/9f) - dist2,-1f + dist2 , colorInner, 1f)
        backgroundMesh.create()
        backgroundMesh.depth = -1f
    }


}

