package ZO.game

import base.input.IMouseClickObserver
import base.input.Mouse
import base.util.Window
import ecs.ECSController
import ecs.components.CameraComponent
import ecs.components.FlatMeshComponent
import ecs.components.TransformComponent
import org.joml.Vector4f
import kotlin.math.cos
import kotlin.math.sin

class DotSystem(private val controller: ECSController) : IMouseClickObserver {

    fun start(){
        Mouse.subscribe(this)
    }
    fun stop(){
        Mouse.unSubscribe(this)
    }

    override fun onMouseClick(xPos: Double, yPos: Double, button: Int) {
    }

    private val colors = arrayOf( Vector4f(1f,1f,0f,1f), Vector4f(1f,0f,1f,1f), Vector4f(0f,1f,1f,1f) )
    private var index = 0
    override fun onMouseRelease(xPos: Double, yPos: Double, button: Int) {
        val dotID = controller.createEntity()
        val mesh = controller.assign<FlatMeshComponent>(dotID)
        mesh.setColor(colors[index++ % colors.size])

        val cameras = controller. getComponents<CameraComponent>()
        val cameraID = cameras.keys.first()
        val firstCamera = cameras[cameraID]!!

        val resolution = 8
        if(resolution > 2){
            mesh.addVertex(0f,0f)
            val angleStep = (2 * Math.PI / resolution).toFloat()

            for (i in 1 .. resolution) {
                val angle = (i * angleStep).toDouble()
                val newX = 0.5f * cos(angle).toFloat()
                val newY = 0.5f * sin(angle).toFloat()
                mesh.addVertex(newX,newY)
                mesh.addTriangle(0, i, (i) % resolution + 1)
            }
        }
        mesh.create()

        val transform = controller.assign<TransformComponent>(dotID)
        transform.setPosition(((xPos/Window.getWidth()).toFloat()*2f -1f)*firstCamera.aspect ,-(yPos/Window.getHeight() ).toFloat() *2f +1f)
        transform.setScale(0.025f)
    }


}