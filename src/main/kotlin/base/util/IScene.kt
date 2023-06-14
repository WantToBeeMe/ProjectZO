package base.util

import ecs.ECSController

open class IScene {
    protected val controller = ECSController()

    open fun start(){
        controller.start()
        controller.onWindowResize(Window.getWidth(), Window.getHeight())
    }

    open fun loop(dt : Float){
        controller.update(dt)
    }

    open fun stop(){
        controller.stop()
    }

    open fun setSize(width :Int, height:Int){
        controller.onWindowResize(width, height)
    }
}