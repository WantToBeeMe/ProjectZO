package ZO.game

import base.util.Colors
import ecs.ECSController
import ecs.singletons.GridSettings
import ecs.components.GridLockedComponent
import ecs.components.TransformComponent
import ecs.components.clickBox.ClickBoxComponent
import ecs.components.clickBox.RectangleClickBox
import ecs.components.mesh.FlatMeshComponent
import ecs.components.mesh.customTemplates.FlatCurvedBoxMesh
import org.joml.Vector2f

class GLCBlock(controller :ECSController, width:Int, height: Int, blockEdgeShorteningPercentage : Float, cornerRadius : Float) {
    val GLC : GridLockedComponent
    val transform : TransformComponent
    val id : Int

    init{
        id = controller.createEntity()
        val blockMesh = controller.assign<FlatMeshComponent>(id)
        blockMesh.addMesh(
                FlatCurvedBoxMesh(
                        Vector2f(-0.5f * width + blockEdgeShorteningPercentage, 0.5f * height - blockEdgeShorteningPercentage),
                        Vector2f(0.5f * width - blockEdgeShorteningPercentage, -0.5f * height + blockEdgeShorteningPercentage),
                    cornerRadius, 3)
        )
        blockMesh.setColor(Colors.BLUE.get)
        blockMesh.create()
        blockMesh.depth = 0.1f

        transform = controller.assign<TransformComponent>(id).setScale(0.2f)
        GLC = controller.assign<GridLockedComponent>(id).setWidth(width).setHeight(height)
        controller.assign<ClickBoxComponent>(id).addClickBox( RectangleClickBox(
            Vector2f(-0.5f * width + blockEdgeShorteningPercentage, 0.5f * height - blockEdgeShorteningPercentage),
            Vector2f(0.5f * width - blockEdgeShorteningPercentage, -0.5f * height + blockEdgeShorteningPercentage)) )

    }

}