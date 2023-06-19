package ZO.home

import ZO.game.InGameScene
import ZO.interactiveTest.InteractScene
import base.input.Mouse
import base.util.Colors
import base.util.Game
import base.util.IScene
import base.util.Maf
import ecs.singletons.Camera
import ecs.components.TransformComponent
import ecs.components.mesh.FlatMeshComponent
import ecs.components.clickBox.RectangleClickBox
import ecs.components.clickBox.ClickBoxComponent
import ecs.components.mesh.OpenMeshComponent
import ecs.components.mesh.customTemplates.FlatCurvedBoxMesh
import ecs.systems.MeshInteractSystem
import ecs.systems.MeshRenderSystem
import org.joml.Vector2f


class HomeScene : IScene() {

    init{
        controller.addSingleton(Camera())
        controller.setSystems(
            MeshInteractSystem,
            MeshRenderSystem
        )
        controller.setComponentTypes(
            TransformComponent::class,
            FlatMeshComponent::class,
            OpenMeshComponent::class,
            ClickBoxComponent::class,
            )

        genOtherStuff()
    }

    private var holding1 : Vector2f? = null
    private var holding2 : Vector2f? = null
    private lateinit var block1Transform : TransformComponent
    private lateinit var block2Transform : TransformComponent

    private fun genOtherStuff(){
        val b = Button(controller,0.8f,0.2f ) {_,_ -> Game.changeScene(InteractScene())}
        b.transform.setRotation(20f)

        val c = Button(controller,0.6f,0.3f ) {_,_ -> Game.changeScene(InteractScene())}
        c.transform.setRotation(-20f)

        val block = Pair(Vector2f(-0.2f, 0.2f), Vector2f(0.2f, -0.2f))
        val block1ID = controller.createEntity()
        val block1Mesh = controller.assign<FlatMeshComponent>(block1ID)
        block1Mesh.setColor(Colors.CYAN.get)
        block1Mesh.addQuad(block.first, block.second)
        block1Mesh.create()
        block1Mesh.depth = 1f
        block1Transform = controller.assign<TransformComponent>(block1ID).setX(1f).setY(0.6f).setRotation(10f)
        val block1ClickBox = controller.assign<ClickBoxComponent>(block1ID).addClickBox( RectangleClickBox(block.first, block.second ) )
        block1ClickBox.setOnClick {s,_ -> holding1 = block1Transform.getPosition().add(-s.x, -s.y) }
        block1ClickBox.setOnRelease{ _,_ -> holding1 = null }


        val block2ID = controller.createEntity()
        val block2Mesh = controller.assign<FlatMeshComponent>(block2ID)
        block2Mesh.setColor(Colors.YELLOW.get)
        block2Mesh.addQuad(block.first, block.second)
        block2Mesh.create()
        block2Transform = controller.assign<TransformComponent>(block2ID).setX(1f).setY(0.6f).setRotation(-10f)
        val block2ClickBox = controller.assign<ClickBoxComponent>(block2ID).addClickBox( RectangleClickBox(block.first, block.second ) )
        block2ClickBox.setOnClick {s,_ -> holding2 = block2Transform.getPosition().add(-s.x, -s.y) }
        block2ClickBox.setOnRelease{ _,_ -> holding2 = null }
        block2ClickBox.priority = 1
    }

    override fun loop(dt: Float) {
        super.loop(dt)
        val cam = controller.getSingleton<Camera>()
        val gLMouse = Maf.pixelToGLCords(Mouse.getX(),Mouse.getY(),cam.aspect )
        if(holding1 != null) block1Transform.setPosition(Vector2f(gLMouse).add(holding1))
        if(holding2 != null) block2Transform.setPosition(Vector2f(gLMouse).add(holding2))
    }

}

