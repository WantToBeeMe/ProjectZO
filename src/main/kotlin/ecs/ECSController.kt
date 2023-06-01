package ecs

import ecs.components.*
import ecs.systems.IEntityComponentSystem
import ecs.systems.MeshInteractSystem
import ecs.systems.MeshRenderSystem
import kotlin.reflect.KClass

class ECSController {

    val componentsTypes: Map< KClass<*>, MutableMap<Int, *>> = mapOf(
        TransformComponent::class      to  mutableMapOf<Int, TransformComponent>(),
        FlatMeshComponent::class       to mutableMapOf<Int, FlatMeshComponent >(),
        OpenMeshComponent::class       to mutableMapOf<Int, FlatMeshComponent>(),
        CameraComponent::class         to  mutableMapOf<Int, CameraComponent>(),
       // MovementInputComponent::class  to  mutableMapOf<Int, MovementInputComponent>(),
    )

    private val systems : Array<IEntityComponentSystem> = arrayOf(
        MeshInteractSystem,MeshRenderSystem
    )

    private var entityIndex = 0
    fun createEntity() : Int {
        return entityIndex++ //only here to make sure no entity has the same ID
    }

    //assign an item with component T
    //for example Transform, then it gets assigned, and you might want to edit the return if you want specific init values
    //if you add something that isn't a component, then it will just crash, so we intentionally didn't make this save in that way, that may leave errors under the radar for to long
    inline fun <reified T> assign(id: Int): T {
        val componentType = componentsTypes[T::class]
        val componentMap = componentType as MutableMap<Int, T> //IDK why its complain that it should be <*, *> but it has to be this sho it needs to shush
        val instance = T::class.java.getDeclaredConstructor().newInstance()
        componentMap[id] = instance
        return instance
    }

    inline fun <reified T > getComponents() : MutableMap<Int, T> {
        return componentsTypes[T::class] as MutableMap<Int, T>
    }
    inline fun <reified A, reified B> getDoubleComponents(): MutableMap<Int, Pair<A, B>> {
        val aComponents = componentsTypes[A::class] as MutableMap<Int, A>
        val bComponents = componentsTypes[B::class] as MutableMap<Int, B>
        val result = mutableMapOf<Int, Pair<A, B>>()
        for ((entityId, aComponent) in aComponents) {
            val bComponent = bComponents[entityId]
            if (bComponent != null) {
                result[entityId] = Pair(aComponent, bComponent)
            }
        }
        return result
    }
    inline fun <reified A, reified B, reified C> getTripleComponents(): MutableMap<Int, Triple<A, B, C>> {
        val aComponents = componentsTypes[A::class] as MutableMap<Int, A>
        val bComponents = componentsTypes[B::class] as MutableMap<Int, B>
        val cComponents = componentsTypes[C::class] as MutableMap<Int, C>
        val result = mutableMapOf<Int, Triple<A, B, C>>()

        for ((entityId, aComponent) in aComponents) {
            val bComponent = bComponents[entityId]
            val cComponent = cComponents[entityId]
            if (bComponent != null && cComponent != null) {
                result[entityId] = Triple(aComponent, bComponent, cComponent)
            }
        }

        return result
    }

    fun onWindowResize(width: Int, height: Int){
        val cameras = componentsTypes[CameraComponent::class] as MutableMap<Int, CameraComponent>
        for(cam in cameras.values){
            cam.resizeViewPort(width.toFloat(), height.toFloat())
        }
    }

    fun start(){
        for(system in systems){
            system.start()
        }
    }
    fun stop(){
        for(system in systems){
            system.stop()
        }
    }

    fun update(dt:Float){
        for(system in systems){
            system.update(this, dt)
        }
    }
}