package ZO.interactiveTest

import ZO.game.InGameScene
import base.input.IKeyboardObserver
import base.input.IMouseClickObserver
import base.input.Keyboard
import base.input.Mouse
import base.util.Colors
import base.util.Game
import base.util.IScene
import ecs.components.TransformComponent
import ecs.components.clickBox.ClickBoxComponent
import ecs.components.clickBox.NewClickBoxComponent
import ecs.components.clickBox.RectangleClickBox
import ecs.components.mesh.FlatMeshComponent
import ecs.components.mesh.OpenMeshComponent
import ecs.components.mesh.customTemplates.FlatCurvedBoxMesh
import ecs.singletons.Camera
import ecs.systems.NewMeshInteractSystem
import ecs.systems.MeshRenderSystem
import org.joml.Vector2f


class InteractScene : IScene() {

    init{
        controller.addSingleton(Camera())
        controller.setSystems(
            NewMeshInteractSystem,
            MeshRenderSystem
        )
        controller.setComponentTypes(
            TransformComponent::class,
            FlatMeshComponent::class,
            OpenMeshComponent::class,
            NewClickBoxComponent::class,
        )

        genBackground()
    }

    private fun genBackground(){
        Button(controller,0.8f,0.2f )
    }


}
