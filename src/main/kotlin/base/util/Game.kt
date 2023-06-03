package base.util

import ZO.game.InGameScene
import base.input.IKeyboardObserver
import base.input.Keyboard
import base.input.Mouse
import org.lwjgl.glfw.GLFW
import ZO.home.HomeScene

//Description: the literal game (so the thing inside the window)
//------
//Usage: --

//todo: cleanup and updatable probably away
object Game : IKeyboardObserver {
    private var _paused = true
    val paused get() = _paused

    private lateinit var scene : IScene
    fun setPaused(p : Boolean){
        _paused = p
        Mouse.setHide(!p)
    }

    fun init(){
        Keyboard.subscribe(this)
        changeScene(HomeScene())
    }

    fun changeScene(newScene : IScene){
        if(::scene.isInitialized) scene.stop()
        scene = newScene
        scene.start()
    }

    fun loop(dt : Float){
        scene.loop(dt)
    }

    fun setSize(width : Int, height: Int){
        if(::scene.isInitialized) scene.setSize(width, height)
    }

    override fun onKeyPress(key: Int) {}

    override fun onKeyRelease(key: Int) {
        if(key == GLFW.GLFW_KEY_ESCAPE) {
            Window.setFocus(true)
            setPaused(!_paused)
        }
    }
}