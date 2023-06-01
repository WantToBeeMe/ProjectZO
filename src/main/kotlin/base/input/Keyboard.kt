package base.input

import org.lwjgl.glfw.GLFW.*

//Description: a class handles the callbacks for the keyboard
//------
//Usage:  create an object that has the IKeyboardObserver
//then you can subscribe to start listening when a key gets pressed or released,
// or you can use the getters, though that one is not preferable

object Keyboard {
    private val keyPressed  = BooleanArray(350)
    private val keyboardObservers : MutableList<IKeyboardObserver> = mutableListOf()

    fun subscribe(iko : IKeyboardObserver){
        if(keyboardObservers.contains(iko))return
        keyboardObservers.add(iko)
    }
    fun unSubscribe(iko: IKeyboardObserver) : Boolean{
        return keyboardObservers.remove(iko)
    }

    fun keyCallback(window : Long, key : Int, scancode : Int, action : Int, mods : Int){
        if(key > keyPressed.size || key <  0) return
        if(action == GLFW_PRESS){
            keyPressed[key] = true
            for(i in 0 until keyboardObservers.size){
                keyboardObservers[i].onKeyPress(key)
            }
        }
        else if (action == GLFW_RELEASE){
            keyPressed[key] = false
            for(i in 0 until keyboardObservers.size){
                keyboardObservers[i].onKeyRelease(key)
            }


        }

    }

    //for the non subscribers, super sick and all
    fun isKeyPressed(key : Int) : Boolean{
        //if(key > keyPressed.size) return false //may not return false but an error just so the user knows what going on, why it's not working or something, idk maybe I will turn this on again. who knows
        return keyPressed[key]
    }

}