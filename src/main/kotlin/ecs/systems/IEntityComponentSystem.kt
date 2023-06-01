package ecs.systems

import ecs.ECSController

abstract class IEntityComponentSystem {
    protected lateinit var controller : ECSController
    open fun start(controller: ECSController) {
        this.controller = controller
    }
    open fun stop(){}
    open fun update(dt: Float){}
}