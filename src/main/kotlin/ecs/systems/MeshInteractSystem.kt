package ecs.systems

import base.input.Mouse
import ecs.ECSController
import ecs.components.FlatMeshComponent
import ecs.components.TransformComponent

object MeshInteractSystem : IEntityComponentSystem{
    override fun start() {}
    override fun stop() {}

    override fun update(controller: ECSController, dt: Float) {
        val transforms = controller.getComponents<TransformComponent>()
        val flatMeshes = controller.getComponents<FlatMeshComponent>()

        //TODO:
        // probably needs a hitbox of some sorts, instead of checking every triangle of the mesh, we could create square and circle hitboxes and check for them (so instead of 200 tryiangles in a curve, we can als just do 5 boxes that line up somewhat)
        // also, the OpenMesh should be treaded as a FlatMesh with more colors, so not a mesh that can have multiple meshes inside
        //          i mean, it can, but those will be treaded as still 1 object for hovering and other, i cant be bothered to make it work, 30fps is also fun >:(
        //          but jokes aside, i dont think it is going to be a big deal to have a lot of meshes drawn, they are all static objects that get moved only when the user interacts + no detailed shadering and stuff, only colors, what can go wrong
        //          otherwise i need to introduce some kind of batching system, but thats for future me when i know more about the game (so i know what i can put together easaly in 1 batch or not)
        println(Mouse.getX())
    }
}