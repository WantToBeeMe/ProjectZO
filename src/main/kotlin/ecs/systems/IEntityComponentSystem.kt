package ecs.systems

import ecs.ECSController

abstract class IEntityComponentSystem() {

    protected lateinit var controller : ECSController
    private var created = false
    open fun start(controller: ECSController) {
        this.controller = controller
        if(!created)
            create()
    }
    protected open fun create(){
        created = true
    }
    open fun stop(){}
    open fun update(dt: Float){}
    open fun guiOptions() {}
    open fun onWindowResize(width: Int, height: Int){}

}