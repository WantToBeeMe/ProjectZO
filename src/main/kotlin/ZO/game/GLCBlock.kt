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

class GLCBlock(controller :ECSController, width:Int, height: Int, gridSettings : GridSettings, blockSpacingFactor : Float) {
    val GLC : GridLockedComponent
    val transform : TransformComponent
    val id : Int

    init{
        val borderWidth = gridSettings.borderWidth
        val blockSize = gridSettings.blockSize
        val perBlockSpacing = blockSize * blockSpacingFactor

        id = controller.createEntity()
        val blockMesh = controller.assign<FlatMeshComponent>(id)
        blockMesh.addMesh(
                FlatCurvedBoxMesh(
                        Vector2f( -(blockSize/2)*width + perBlockSpacing, (blockSize/2)*height  - perBlockSpacing),
                        Vector2f((blockSize/2)*width - perBlockSpacing, -(blockSize/2)*height  + perBlockSpacing),
                        borderWidth* 0.48f, 3)
        )
        blockMesh.setColor(Colors.BLUE.get).setVisualClickBox(false)
        blockMesh.create()
        blockMesh.depth = 0.1f
        transform = controller.assign<TransformComponent>(id)
        GLC = controller.assign<GridLockedComponent>(id).setWidth(width).setHeight(height)
        controller.assign<ClickBoxComponent>(id).addClickBox( RectangleClickBox( Vector2f( -(blockSize/2)*width + perBlockSpacing, (blockSize/2)*height  - perBlockSpacing),
                                                                            Vector2f((blockSize/2)*width - perBlockSpacing, -(blockSize/2)*height  + perBlockSpacing)) )
    }

    fun hold(){
        transform.setScale(1.2f)
    }

    fun delete(){

    }

    fun place(){
        transform.setScale(1f)
    }
}