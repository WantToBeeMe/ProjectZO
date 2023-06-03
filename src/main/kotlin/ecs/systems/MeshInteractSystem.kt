package ecs.systems

import base.input.Mouse
import base.shader.Shader
import base.shader.ShaderObject
import base.util.Window
import ecs.components.CameraComponent
import ecs.components.FlatMeshComponent
import ecs.components.TransformComponent
import ecs.components.clickBox.ClickBoxComponent
import ecs.components.clickBox.MouseClickBoxVisualizer
import imgui.ImBool
import imgui.ImGui
import org.joml.Vector2f
import org.joml.Vector4f
import kotlin.math.cos
import kotlin.math.sin

object MeshInteractSystem : IEntityComponentSystem(){
    private val currentShader : ShaderObject = Shader.FLAT_OBJECT.get()
    private val noTransform =  TransformComponent().transform
    private val showClickBox = ImBool(false)

    override fun update(dt: Float) {
        super.update(dt)
        val transforms = controller.getComponents<TransformComponent>()
        val flatClickBoxMeshes = controller.getDoubleComponents<FlatMeshComponent, ClickBoxComponent>()

        val cameras = controller. getComponents<CameraComponent>()
        val cameraID = cameras.keys.first()
        val realMousePos = Vector2f( ((Mouse.getX()/Window.getWidth())*2f -1f)* cameras[cameraID]!!.aspect ,-(Mouse.getY()/Window.getHeight() ) *2f +1f)
        for(clickBoxMesh in flatClickBoxMeshes){
            val transformedMousePos  = transformPoint(realMousePos, transforms[clickBoxMesh.key])
            if( clickBoxMesh.value.second.isInside(transformedMousePos) ){
                clickBoxMesh.value.first.interact = 1
            }
            else clickBoxMesh.value.first.interact = 0
        }

        if(showClickBox.get()){
            currentShader.use()
            currentShader.enableBlend()
            currentShader.enableDepthTest()
            currentShader.uploadMat4f("uProjection", cameras[cameraID]!!.projectionMatrix )
            currentShader.uploadMat4f("uView", cameras[cameraID]!!.viewMatrix)
            currentShader.uploadMat3f("uTransform", noTransform)
            currentShader.uploadFloat("uDepth", 1f)
            currentShader.uploadInt("uInteract", 0)

            for(clickBoxMesh in flatClickBoxMeshes){
                if(!clickBoxMesh.value.second.showDebugLines) continue
                currentShader.uploadVec4f("uColor", Vector4f(1f,1f,clickBoxMesh.value.first.interact.toFloat(),1f))
                clickBoxMesh.value.second.renderClickBox()
                val transformedMousePos  = transformPoint(realMousePos, transforms[clickBoxMesh.key])
                currentShader.uploadVec4f("uColor", Vector4f(1f,0f,1f,1f))
                MouseClickBoxVisualizer.renderMouse(transformedMousePos.x, transformedMousePos.y)
            }

            currentShader.detach()
        }
    }



    private fun transformPoint(point: Vector2f, transform: TransformComponent? ): Vector2f {
        if(transform == null) return point
        //translation
        var newPoint = Vector2f(point).add(Vector2f(transform.getPosition()).mul(-1f))

        //rotate
        val angleRad = transform.getRotation(true)
        val cos = cos(angleRad.toDouble()).toFloat()
        val sin = sin(angleRad.toDouble()).toFloat()
        newPoint = Vector2f(newPoint.x * cos - newPoint.y * sin,  newPoint.x * sin + newPoint.y * cos)
        //scale
        return newPoint.mul(Vector2f(1f).div(transform.getScale()))
    }


    override fun guiOptions() {
        if (ImGui.beginTabItem("ClickBox" )) {
            ImGui.checkbox("Show ClickBoxes",showClickBox)
            ImGui.endTabItem();
        }
    }

}



//TODO:
// probably needs a hitbox of some sorts, instead of checking every triangle of the mesh, we could create square and circle hitboxes and check for them (so instead of 200 tryiangles in a curve, we can als just do 5 boxes that line up somewhat)
// also, the OpenMesh should be treaded as a FlatMesh with more colors, so not a mesh that can have multiple meshes inside
//   i mean, it can, but those will be treaded as still 1 object for hovering and other, i cant be bothered to make it work, 30fps is also fun >:(
//   but jokes aside, i dont think it is going to be a big deal to have a lot of meshes drawn, they are all static objects that get moved only when the user interacts + no detailed shadering and stuff, only colors, what can go wrong
//   otherwise i need to introduce some kind of batching system, but thats for future me when i know more about the game (so i know what i can put together easaly in 1 batch or not)
