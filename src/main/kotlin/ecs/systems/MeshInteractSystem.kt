package ecs.systems

import base.input.Mouse
import base.util.Window
import ecs.ECSController
import ecs.components.CameraComponent
import ecs.components.FlatMeshComponent
import ecs.components.TransformComponent
import ecs.components.hitbox.HitBoxComponent
import org.joml.Vector2f

object MeshInteractSystem : IEntityComponentSystem(){

    override fun update(dt: Float) {
        super.update(dt)
        val transforms = controller.getComponents<TransformComponent>()
        val flatHitBoxMeshes = controller.getDoubleComponents<FlatMeshComponent, HitBoxComponent>()

        val cameras = controller. getComponents<CameraComponent>()
        val cameraID = cameras.keys.first()
        val mousePos = Vector2f( ((Mouse.getX()/Window.getWidth())*2f -1f)* cameras[cameraID]!!.aspect ,-(Mouse.getY()/Window.getHeight() ) *2f +1f)
        for(hitBoxMesh in flatHitBoxMeshes){
            val newMousePoint  =transformPoint(mousePos, transforms[hitBoxMesh.key])

            if( hitBoxMesh.value.second.isInside(newMousePoint) ){
                hitBoxMesh.value.first.interact = 1
            }
            else hitBoxMesh.value.first.interact = 0

        }
    }


    private fun transformPoint(point: Vector2f, transform: TransformComponent? ): Vector2f {
        if(transform == null) return point
        val rotatedPoint = rotatePoint(point, transform.getRotation(true))
        val scaledPoint = scalePoint(rotatedPoint, transform.getScale())
        return translatePoint(scaledPoint, transform.getPosition())
    }

    private fun rotatePoint(point: Vector2f, rotation: Float): Vector2f {
        val angleRad = Math.toRadians(rotation.toDouble()).toFloat()
        val cos = Math.cos(angleRad.toDouble()).toFloat()
        val sin = Math.sin(angleRad.toDouble()).toFloat()
        val x = point.x * cos - point.y * sin
        val y = point.x * sin + point.y * cos
        return Vector2f(x, y)
    }
    private fun scalePoint(point: Vector2f, scale: Vector2f): Vector2f {
        val x = point.x * scale.x
        val y = point.y * scale.y
        return Vector2f(x, y)
    }
    private fun translatePoint(point: Vector2f, translation: Vector2f): Vector2f {
        val x = point.x + translation.x
        val y = point.y + translation.y
        return Vector2f(x, y)
    }
}




//TODO:
// probably needs a hitbox of some sorts, instead of checking every triangle of the mesh, we could create square and circle hitboxes and check for them (so instead of 200 tryiangles in a curve, we can als just do 5 boxes that line up somewhat)
// also, the OpenMesh should be treaded as a FlatMesh with more colors, so not a mesh that can have multiple meshes inside
//   i mean, it can, but those will be treaded as still 1 object for hovering and other, i cant be bothered to make it work, 30fps is also fun >:(
//   but jokes aside, i dont think it is going to be a big deal to have a lot of meshes drawn, they are all static objects that get moved only when the user interacts + no detailed shadering and stuff, only colors, what can go wrong
//   otherwise i need to introduce some kind of batching system, but thats for future me when i know more about the game (so i know what i can put together easaly in 1 batch or not)
