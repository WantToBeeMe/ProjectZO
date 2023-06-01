package base.input

import org.lwjgl.glfw.GLFW.*

//Description: a class handles the callbacks for the mouse
//------
//Usage:  create an object that has one of the IMouseObserver inherited
//then you can subscribe for those subjects to start reacting to any mouse callbacks,
// or you can use the getters, though that one is not preferable

object Mouse {
    private var scrollX: Double = 0.0
    private var scrollY: Double = 0.0
    private var xPos: Double = 0.0
    private var yPos: Double = 0.0
    private var lastY: Double = 0.0
    private var lastX: Double = 0.0
    private val mouseButtonPressed =  BooleanArray(3)

    private var glfwWindow : Long = 0
    private var hide = false
    private var ignoreX : Double = 0.0
    private var ignoreY : Double = 0.0

    private val scrollObservers : MutableList<IMouseScrollObserver> = mutableListOf()
    private val clickObservers : MutableList<IMouseClickObserver> = mutableListOf()
    private val moveObservers : MutableList<IMouseMoveObserver> = mutableListOf()
    fun subscribe(iso : IMouseScrollObserver){ if(scrollObservers.contains(iso))return;scrollObservers.add(iso) }
    fun subscribe(ico : IMouseClickObserver){ if(clickObservers.contains(ico))return;clickObservers.add(ico) }
    fun subscribe(imo : IMouseMoveObserver){ if(moveObservers.contains(imo))return;moveObservers.add(imo) }
    fun unSubscribe(iso : IMouseScrollObserver): Boolean{ return scrollObservers.remove(iso) }
    fun unSubscribe(ico : IMouseClickObserver) : Boolean{ return clickObservers.remove(ico) }
    fun unSubscribe(imo : IMouseMoveObserver) : Boolean{ return moveObservers.remove(imo) }


    fun reset(){
        scrollX = 0.0
        scrollY = 0.0
        lastX = xPos
        lastY = yPos
    }

    fun setHide(h : Boolean){
        hide = h;

      if(glfwWindow > 0 ){
          if(hide) glfwSetInputMode(glfwWindow, GLFW_CURSOR, GLFW_CURSOR_HIDDEN)
          else {
              glfwSetInputMode(glfwWindow, GLFW_CURSOR, GLFW_CURSOR_CAPTURED)
              ignoreX = 0.0
              ignoreY = 0.0
          };
      };
    }

    fun setWindow(window : Long){
        glfwWindow = window} //dont use theseonces yet, are broken.. sadly

    fun mousePosCallback(window: Long, xpos: Double, ypos: Double){
        if(glfwWindow > 0 && hide){
            val height = IntArray(1)
            val width = IntArray(1)
            glfwGetWindowSize(glfwWindow,width,height)
            glfwSetCursorPos(glfwWindow,width[0]/2.0,height[0]/2.0);
            ignoreX = width[0]/2.0 - xpos
            ignoreY = height[0]/2.0 - ypos
        }
        lastX = xPos  + ignoreX;
        lastY = yPos  + ignoreY;
        xPos = xpos + ignoreX;
        yPos = ypos + ignoreY;
        for(i in 0 until moveObservers.size){
            moveObservers[i].onMouseMove(xpos, ypos, lastX -xpos, lastY -ypos)
        }


    }
    fun mouseButtonCallback(window : Long, button : Int, action: Int, mods : Int ){
        if(button >= mouseButtonPressed.size) return
        if(action == GLFW_PRESS){
            mouseButtonPressed[button] = true;
            for(i in 0 until clickObservers.size){
                clickObservers[i].onMouseClick(xPos, yPos, button)
            }
        }
        else if(action == GLFW_RELEASE){
            mouseButtonPressed[button] = false;
            for(i in 0 until clickObservers.size){
                clickObservers[i].onMouseRelease(xPos, yPos, button)
            }
        }
    }
    fun mouseScrollCallback(window : Long, xoffset: Double, yoffset:Double){
        scrollX = xoffset
        scrollY = yoffset
        for(i in 0 until scrollObservers.size){
            scrollObservers[i].onMouseScroll(xPos, yPos, xoffset,yoffset)
        }
    }


    fun getX(): Float { return xPos.toFloat() }
    fun getY(): Float { return yPos.toFloat() }
    fun getDx(): Float { return (lastX - xPos).toFloat() }
    fun getDy(): Float { return (lastY - yPos).toFloat() }
    fun getScrollX(): Float { return scrollX.toFloat() }
    fun getScrollY(): Float { return scrollY.toFloat() }
    fun mouseButtonPressed(button : Int) : Boolean {
        if(button > mouseButtonPressed.size) return false
        return mouseButtonPressed[button]
    }

}