package base.util

import base.input.Keyboard
import base.input.Mouse
import org.lwjgl.Version
import org.lwjgl.glfw.*
import org.lwjgl.glfw.Callbacks.glfwFreeCallbacks
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11.*
import org.lwjgl.system.MemoryUtil
import java.util.*


//Description: the literal window object, setting it up and keeping it running
//------
//Usage: --

object Window {
    private val title = "Project ZO"
    private var glfwWindow : Long? = null

    private var width = 1920
    fun getWidth() : Int {return width}
    private var height = 1080
    fun getHeight() : Int {return height}
    private var focus = false
    fun isFocused() : Boolean {return focus}

    private fun onWindowResize(w: Long, width:Int, height:Int ){
        this.width = width
        this.height = height
        glViewport(0, 0, width, height);
        //GuiMaster.resize(width,height)
        Game.setSize(width,height)
        ImGuiController.windowResize(width.toFloat(), height.toFloat())
    }

    private fun setFocus(w : Long, focus : Boolean){
        this.setFocus(focus)
    }
    fun setFocus(f : Boolean){
        focus = f
        if(!f) Game.setPaused(true)
    }

    private fun init(){
        //setup an error callback
        GLFWErrorCallback.createPrint(System.err).set()

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        check(glfwInit()) { "Unable to initialize GLFW" }

        //Configure GLFW settings/hints
        // (before creating, creation is going to use these hints)
        glfwDefaultWindowHints()
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE)
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE)

        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4)
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 6)
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_COMPAT_PROFILE)  // this makes it possible to use depricated openGl stuff, that's because ImGui, because ImGui old

        //Creating the window
        glfwWindow = glfwCreateWindow(width, height, title, MemoryUtil.NULL, MemoryUtil.NULL)
        glfwSetCursorPosCallback(glfwWindow!!, Mouse::mousePosCallback )
        glfwSetMouseButtonCallback(glfwWindow!!, Mouse::mouseButtonCallback )
        glfwSetScrollCallback(glfwWindow!!, Mouse::mouseScrollCallback )
        glfwSetKeyCallback(glfwWindow!!, Keyboard::keyCallback )
        glfwSetFramebufferSizeCallback(glfwWindow!!, this::onWindowResize)
        glfwSetWindowFocusCallback(glfwWindow!!, this::setFocus)

        glfwMakeContextCurrent(glfwWindow!!)
        glfwSwapInterval(GLFW_TRUE) //Enable v-sync

        if (glfwRawMouseMotionSupported()) glfwSetInputMode(glfwWindow!!, GLFW_RAW_MOUSE_MOTION, GLFW_TRUE);
        //make window visible again
        //glfwSetInputMode(glfwWindow!!, GLFW_CURSOR, GLFW_CURSOR_CAPTURED);                        //capturing the mouse inside
        glfwShowWindow(glfwWindow!!)

        /* This line is critical for LWJGL's interoperation with GLFW's
        OpenGL context, or any context that is managed externally.
        LWJGL detects the context that is current in the current thread,
        creates the GLCapabilities instance and makes the OpenGL
         bindings available for use.*/
        GL.createCapabilities();

        //makes sure that everyone who is dependent on the right window size gets good initialized
        val widthBuffer = IntArray(1)
        val heightBuffer= IntArray(1)
        glfwGetWindowSize(glfwWindow!!, widthBuffer,heightBuffer)
        onWindowResize(glfwWindow!!, widthBuffer[0], heightBuffer[0] )
    }
    private fun loop(){
        var beginTime = glfwGetTime().toFloat();
        var endTime = 0f;
        var dt = 1f/60f;
        Game.init()
        Mouse.setWindow(glfwWindow!!)
        while (!glfwWindowShouldClose(glfwWindow!!)){
            glClearColor(30/225f, 31/255f, 34/255f, 1f)
            glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

            Game.loop(dt)
            ImGuiController.render(dt)
            Mouse.reset()

            glfwSwapBuffers(glfwWindow!!)
            glfwPollEvents()

            endTime= glfwGetTime().toFloat()
            dt = endTime - beginTime
            beginTime = endTime
        }
    }

    private fun destroy() {
        //if you created custom cursors, then also destroy those for extra bonus points
        //for (mouseCursor in mouseCursors) {
        //    GLFW.glfwDestroyCursor(mouseCursor)
        //}
        glfwFreeCallbacks(glfwWindow!!)
        glfwDestroyWindow(glfwWindow!!)
        glfwTerminate()
        Objects.requireNonNull(glfwSetErrorCallback(null))?.free()
    }

    fun run() {
        println("$title window is running with LWJGL version: ${Version.getVersion()}")
        init()
        println("with openGL version: ${glGetString(GL_VERSION)}")
        ImGuiController.init(glfwWindow!!)

        loop()

        ImGuiController.destroy()
        destroy()

    }



}
