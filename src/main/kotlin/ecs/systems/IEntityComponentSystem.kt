package ecs.systems

import ecs.ECSController

interface IEntityComponentSystem {
    fun start()
    fun stop()
    fun update(controller: ECSController, dt: Float)
}