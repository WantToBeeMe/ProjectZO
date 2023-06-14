package ZO.home

import ZO.game.InGameScene
import base.util.Colors
import base.util.Game
import ecs.ECSController
import ecs.components.TransformComponent
import ecs.components.clickBox.ClickBoxComponent
import ecs.components.clickBox.RectangleClickBox
import ecs.components.mesh.FlatMeshComponent
import ecs.components.mesh.customTemplates.FlatCurvedBoxMesh
import org.joml.Vector2f
import org.joml.Vector4f

class Button(controller : ECSController, width : Float, height: Float, event: ((Vector2f, Int) -> Unit)) {
    private val id: Int

    val transform: TransformComponent
    private val mesh : FlatMeshComponent

    init{
        id = controller.createEntity()

        val size = Pair(Vector2f(-width/2,height/2), Vector2f(width/2,-height/2))
        val defaultColor = Colors.CYAN.get
        val tint = (defaultColor.x + defaultColor.y + defaultColor.z)/5.5f;

        mesh = controller.assign<FlatMeshComponent>(id)
        mesh.addMesh(FlatCurvedBoxMesh(size.first,size.second,0.025f ))
        mesh.setColor(defaultColor)
        mesh.create()
        transform = controller.assign<TransformComponent>(id)

        controller.assign<ClickBoxComponent>(id)
            .addClickBox( RectangleClickBox(size.first,size.second) )
            .setOnEnter {_ -> mesh.setColor(Vector4f(tint,tint,tint,0f).add(defaultColor)) }
            .setOnLeave {_ -> mesh.setColor(defaultColor) }
            .setOnRelease(event)
    }
}