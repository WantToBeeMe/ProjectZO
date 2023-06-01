package base.util

import base.input.*
import imgui.*
import imgui.callbacks.ImStrConsumer
import imgui.callbacks.ImStrSupplier
import imgui.enums.*
import imgui.gl3.ImGuiImplGl3
import org.lwjgl.BufferUtils
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL12.*
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.UncheckedIOException
import java.util.*

//Description: is the master class of the ImGui implementation
//------
//Usage: if you want to add a gui to the screen, do addGui
// if you want to remove one, use removeGui.
// use resize when the window is being resized so that the windows don't get squashed/stretched or cut off

object ImGuiController : IKeyboardObserver, IMouseClickObserver, IMouseScrollObserver {
    //all the correlations to the window itself
    private var window : Long = 0
    private var windowHeight = 0f
    private var windowWidth = 0f

    private val guiWindows: MutableList<IImGuiWindow> = mutableListOf<IImGuiWindow>()
    fun addGui(guiWindow : IImGuiWindow) {
        if(guiWindows.contains(guiWindow)) return
        guiWindows.add(guiWindow)
    }
    fun removeGui(guiWindow: IImGuiWindow) : Boolean{
        return guiWindows.remove(guiWindow)
    }

    //for custom cool cursors or something
    private val mouseCursors = LongArray(ImGuiMouseCursor.COUNT)

    // LWJGL3 rendered itself (SHOULD be initialized)
    private val imGuiGl3 = ImGuiImplGl3()

    // Here we will initialize ImGui stuff.
     fun init(w : Long) {
        window = w
        // IMPORTANT!!
        // This line is critical for Dear ImGui to work.
        ImGui.createContext()

        // ImGui provides 3 different color schemas for styling. We will use the classic one here.
        // Try others with ImGui.styleColors*() methods.
        ImGui.styleColorsClassic()

        //ImGui.colorEdit4("DragDropTarget", floatArrayOf(11f,101f,208f,255f))
        // Initialize ImGuiIO config
        initIO()
        initStyle()

        // IMPORTANT!!!
        // Method initializes renderer itself.
        // This method SHOULD be called after you've initialized your ImGui configuration (fonts and so on).
        // ImGui context should be created as well.
        imGuiGl3.init()

    }

    private fun initIO() {
        // Initialize ImGuiIO config
        val io = ImGui.getIO()
        io.iniFilename = null // We don't want to save .ini file
        io.configFlags = ImGuiConfigFlags.NavEnableKeyboard // Navigation with keyboard
        io.backendFlags = ImGuiBackendFlags.HasMouseCursors // Mouse cursors to display while resizing windows etc.
        io.backendPlatformName = "imgui_java_impl_glfw" // For clarity reasons
        io.backendRendererName = "imgui_java_impl_lwjgl" // For clarity reasons

        // Keyboard mapping. ImGui will use those indices to peek into the io.KeysDown[] array.
        val keyMap = IntArray(ImGuiKey.COUNT)
        keyMap[ImGuiKey.Tab] = GLFW.GLFW_KEY_TAB
        keyMap[ImGuiKey.LeftArrow] = GLFW.GLFW_KEY_LEFT
        keyMap[ImGuiKey.RightArrow] = GLFW.GLFW_KEY_RIGHT
        keyMap[ImGuiKey.UpArrow] = GLFW.GLFW_KEY_UP
        keyMap[ImGuiKey.DownArrow] = GLFW.GLFW_KEY_DOWN
        keyMap[ImGuiKey.PageUp] = GLFW.GLFW_KEY_PAGE_UP
        keyMap[ImGuiKey.PageDown] = GLFW.GLFW_KEY_PAGE_DOWN
        keyMap[ImGuiKey.Home] = GLFW.GLFW_KEY_HOME
        keyMap[ImGuiKey.End] = GLFW.GLFW_KEY_END
        keyMap[ImGuiKey.Insert] = GLFW.GLFW_KEY_INSERT
        keyMap[ImGuiKey.Delete] = GLFW.GLFW_KEY_DELETE
        keyMap[ImGuiKey.Backspace] = GLFW.GLFW_KEY_BACKSPACE
        keyMap[ImGuiKey.Space] = GLFW.GLFW_KEY_SPACE
        keyMap[ImGuiKey.Enter] = GLFW.GLFW_KEY_ENTER
        keyMap[ImGuiKey.Escape] = GLFW.GLFW_KEY_ESCAPE
        keyMap[ImGuiKey.KeyPadEnter] = GLFW.GLFW_KEY_KP_ENTER
        keyMap[ImGuiKey.A] = GLFW.GLFW_KEY_A
        keyMap[ImGuiKey.C] = GLFW.GLFW_KEY_C
        keyMap[ImGuiKey.V] = GLFW.GLFW_KEY_V
        keyMap[ImGuiKey.X] = GLFW.GLFW_KEY_X
        keyMap[ImGuiKey.Y] = GLFW.GLFW_KEY_Y
        keyMap[ImGuiKey.Z] = GLFW.GLFW_KEY_Z
        io.setKeyMap(keyMap)

        // Mouse cursors mapping
        mouseCursors[ImGuiMouseCursor.Arrow] = GLFW.glfwCreateStandardCursor(GLFW.GLFW_ARROW_CURSOR)
        mouseCursors[ImGuiMouseCursor.TextInput] = GLFW.glfwCreateStandardCursor(GLFW.GLFW_IBEAM_CURSOR)
        mouseCursors[ImGuiMouseCursor.ResizeAll] = GLFW.glfwCreateStandardCursor(GLFW.GLFW_ARROW_CURSOR)
        mouseCursors[ImGuiMouseCursor.ResizeNS] = GLFW.glfwCreateStandardCursor(GLFW.GLFW_VRESIZE_CURSOR)
        mouseCursors[ImGuiMouseCursor.ResizeEW] = GLFW.glfwCreateStandardCursor(GLFW.GLFW_HRESIZE_CURSOR)
        mouseCursors[ImGuiMouseCursor.ResizeNESW] = GLFW.glfwCreateStandardCursor(GLFW.GLFW_ARROW_CURSOR)
        mouseCursors[ImGuiMouseCursor.ResizeNWSE] = GLFW.glfwCreateStandardCursor(GLFW.GLFW_ARROW_CURSOR)
        mouseCursors[ImGuiMouseCursor.Hand] = GLFW.glfwCreateStandardCursor(GLFW.GLFW_HAND_CURSOR)
        mouseCursors[ImGuiMouseCursor.NotAllowed] = GLFW.glfwCreateStandardCursor(GLFW.GLFW_ARROW_CURSOR)

        // ------------------------------------------------------------
        // Here goes GLFW callbacks to update user input in Dear ImGui
        GLFW.glfwSetCharCallback(window) { w: Long, c: Int ->
            if (c != GLFW.GLFW_KEY_DELETE) {
                io.addInputCharacter(c)
            }
        }
        io.setSetClipboardTextFn(object : ImStrConsumer() {
            override fun accept(s: String) {
                GLFW.glfwSetClipboardString(window, s)
            }
        })
        io.setGetClipboardTextFn(object : ImStrSupplier() {
            override fun get(): String {
                return GLFW.glfwGetClipboardString(window)!!
            }
        })

        Keyboard.subscribe(this)
        Mouse.subscribe(this as IMouseClickObserver)
        Mouse.subscribe(this as IMouseScrollObserver)
    }
    private fun initFonts() {
        val io = ImGui.getIO()
        val fontAtlas = io.fonts
        // First of all we add a default font, which is 'ProggyClean.ttf, 13px'
        fontAtlas.addFontDefault()

        val fontConfig = ImFontConfig() // Keep in mind that creation of the ImFontConfig will allocate native memory
        fontConfig.mergeMode = true // All fonts added while this mode is turned on will be merged with the previously added font
        fontConfig.pixelSnapH = true
        fontConfig.glyphRanges = fontAtlas.glyphRangesCyrillic // Additional glyphs could be added like this or in addFontFrom*() methods
        fontConfig.mergeMode = false// Disable merged mode and add all other fonts normally
        fontConfig.pixelSnapH = false
        fontConfig.rasterizerMultiply = 1.6f // This will make fonts a bit more readable

        fontConfig.setName("Roboto-Regular, 14px") // We can apply a new config value every time we add a new font
        val f= fontAtlas.addFontFromMemoryTTF(loadFromResources("fonts/Roboto-Regular.ttf"), 14f, fontConfig)
        fontConfig.destroy() // After all fonts were added we don't need this config more

        io.setFontDefault(f)
    }
    private fun initStyle(){
        val n = 255f //to device by n to make it normalized, because I calculated every color in big values, whoops
        val a = 1f
        val s = ImGui.getStyle()
        s.setColor(ImGuiCol.Text, 208/n, 207/n, 207/n, a)
        s.setColor(ImGuiCol.TextDisabled,119/n, 122/n, 132/n, a )
        s.setColor(ImGuiCol.WindowBg, 43/n,45/n,49/n,a)
        s.setColor(ImGuiCol.ChildBg, 46/n,48/n,53/n,a)
        s.setColor(ImGuiCol.PopupBg, 35/n,36/n,40/n,a)
        s.setColor(ImGuiCol.Border, 56/n,58/n,64/n,a)
        s.setColor(ImGuiCol.BorderShadow,0f,0f,0f,0f)
        s.setColor(ImGuiCol.FrameBg, 56/n,58/n,64/n,a)
        s.setColor(ImGuiCol.FrameBgHovered, 78/n,80/n,88/n,a)
        s.setColor(ImGuiCol.FrameBgActive, 89/n,91/n,99/n,a)

        s.setColor(ImGuiCol.TitleBg,30/n,31/n,34/n,a)
        s.setColor(ImGuiCol.TitleBgActive,30/n,31/n,34/n,a)
        s.setColor(ImGuiCol.TitleBgCollapsed,30/n,31/n,34/n,a/1.4f)
        s.setColor(ImGuiCol.MenuBarBg,30/n,31/n,34/n,a)

        s.setColor(ImGuiCol.ScrollbarBg,30/n,31/n,34/n,a)
        s.setColor(ImGuiCol.ScrollbarGrab, 43/n,45/n,49/n,a)
        s.setColor(ImGuiCol.ScrollbarGrabHovered, 48/n,50/n,55/n,a)
        s.setColor(ImGuiCol.ScrollbarGrabActive, 56/n,58/n,64/n,a)

        s.setColor(ImGuiCol.CheckMark,128/n,132/n,142/n,a)
        s.setColor(ImGuiCol.SliderGrab,128/n,132/n,142/n,a)
        s.setColor(ImGuiCol.SliderGrabActive,181/n,186/n,193/n,a)
        s.setColor(ImGuiCol.Button,78/n,80/n,88/n,a)
        s.setColor(ImGuiCol.ButtonHovered,89/n,91/n,99/n,a)
        s.setColor(ImGuiCol.ButtonActive,128/n,132/n,142/n,a)

        s.setColor(ImGuiCol.Header,56/n,58/n,64/n,a)
        s.setColor(ImGuiCol.HeaderHovered,64/n,66/n,73/n,a)
        s.setColor(ImGuiCol.HeaderActive,64/n,66/n,73/n,a)
        s.setColor(ImGuiCol.Separator, 63/n,65/n,73/n,a)
        s.setColor(ImGuiCol.SeparatorHovered,63/n,65/n,73/n,a)
        s.setColor(ImGuiCol.SeparatorActive,89/n,91/n,99/n,a)

        s.setColor(ImGuiCol.ResizeGrip,56/n,58/n,64/n,a)
        s.setColor(ImGuiCol.ResizeGripHovered,82/n,84/n,92/n,a)
        s.setColor(ImGuiCol.ResizeGripActive,128/n,132/n,142/n,a)
        s.setColor(ImGuiCol.Tab,56/n,58/n,64/n,a)
        s.setColor(ImGuiCol.TabHovered,78/n,80/n,88/n,a)
        s.setColor(ImGuiCol.TabActive,89/n,91/n,99/n,a)
        s.setColor(ImGuiCol.TabUnfocused, 0f,0f,0f,0f)
        s.setColor(ImGuiCol.TabUnfocusedActive,0f,0f,0f,0f)

        s.setColor(ImGuiCol.TextSelectedBg,11/n,101/n,208/n,a)
        s.setColor(ImGuiCol.DragDropTarget,11/n,101/n,208/n,a)

        //s.windowMenuButtonPosition = 1//the minimise button on the right
        s.tabRounding = 2f
        s.grabRounding = 13f
        s.scrollbarRounding = 15f
        s.popupRounding = 2f
        s.frameRounding = 3f
        s.childRounding = 2f
        s.windowRounding = 3f
        s.popupBorderSize = 0f
        s.windowBorderSize = 0f
        s.childBorderSize =2f
        s.grabMinSize = 9f
        s.scrollbarSize = 17f

        initFonts()
    }
    
    private fun loadFromResources(fileName: String): ByteArray {
        try {
            Objects.requireNonNull<InputStream>(this::class.java.getClassLoader().getResourceAsStream(fileName))
                .use { `is` ->
                    ByteArrayOutputStream().use { buffer ->
                        val data = ByteArray(16384)
                        var nRead: Int
                        while (`is`.read(data, 0, data.size).also { nRead = it } != -1) {
                            buffer.write(data, 0, nRead)
                        }
                        return buffer.toByteArray()
                    }
                }
        } catch (e: IOException) {
            throw UncheckedIOException(e)
        }
    }
    private fun loadTexture(image: BufferedImage): Int {
        val pixels = IntArray(image.width * image.height)
        image.getRGB(0, 0, image.width, image.height, pixels, 0, image.width)
        val buffer = BufferUtils.createByteBuffer(image.width * image.height * 4) // 4 for RGBA, 3 for RGB
        for (y in 0 until image.height) {
            for (x in 0 until image.width) {
                val pixel = pixels[y * image.width + x]
                buffer.put((pixel shr 16 and 0xFF).toByte())
                buffer.put((pixel shr 8 and 0xFF).toByte())
                buffer.put((pixel and 0xFF).toByte())
                buffer.put((pixel shr 24 and 0xFF).toByte())
            }
        }
        buffer.flip()
        val textureID = glGenTextures()
        glBindTexture(GL_TEXTURE_2D, textureID)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8,
            image.width, image.height, 0,
            GL_RGBA, GL_UNSIGNED_BYTE, buffer)
        return textureID
    }

    fun render(dt: Float){
        if(!Game.paused) return
        val io = ImGui.getIO()
        io.setDisplaySize(windowWidth,windowHeight)
        io.setDisplayFramebufferScale(1f,1f)
        io.setMousePos(Mouse.getX(), Mouse.getY())
        io.deltaTime = dt
        //window Size and Mouse Position are all checked

        // IMPORTANT!!
        // Any Dear ImGui code SHOULD go between NewFrame()/Render() methods
        ImGui.newFrame()
        for(gui in guiWindows){
            gui.showUi()
        }
        ImGui.render()

        // Update mouse cursor //if you want to change the coolness of the cursor, here you go
        val imguiCursor = ImGui.getMouseCursor()
        GLFW.glfwSetCursor(window, mouseCursors[imguiCursor])
        GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL)

        // After ImGui#render call we provide draw data into LWJGL3 renderer.
        // At that moment ImGui will be rendered to the current OpenGL context.
        imGuiGl3.render(ImGui.getDrawData())
    }

    fun windowResize(width: Float, height:Float){
        windowWidth  = width
        windowHeight = height
    }

    fun destroy(){
        imGuiGl3.dispose()
        ImGui.destroyContext()
        window = 0
    }

    override fun onKeyPress(key: Int) {
        val io = ImGui.getIO()
        io.setKeysDown(key, true)
        io.keyCtrl = io.getKeysDown(GLFW.GLFW_KEY_LEFT_CONTROL) || io.getKeysDown(GLFW.GLFW_KEY_RIGHT_CONTROL)
        io.keyShift = io.getKeysDown(GLFW.GLFW_KEY_LEFT_SHIFT) || io.getKeysDown(GLFW.GLFW_KEY_RIGHT_SHIFT)
        io.keyAlt = io.getKeysDown(GLFW.GLFW_KEY_LEFT_ALT) || io.getKeysDown(GLFW.GLFW_KEY_RIGHT_ALT)
        io.keySuper = io.getKeysDown(GLFW.GLFW_KEY_LEFT_SUPER) || io.getKeysDown(GLFW.GLFW_KEY_RIGHT_SUPER)
    }
    override fun onKeyRelease(key: Int) {
        val io = ImGui.getIO()
        io.setKeysDown(key, false)
        io.keyCtrl = io.getKeysDown(GLFW.GLFW_KEY_LEFT_CONTROL) || io.getKeysDown(GLFW.GLFW_KEY_RIGHT_CONTROL)
        io.keyShift = io.getKeysDown(GLFW.GLFW_KEY_LEFT_SHIFT) || io.getKeysDown(GLFW.GLFW_KEY_RIGHT_SHIFT)
        io.keyAlt = io.getKeysDown(GLFW.GLFW_KEY_LEFT_ALT) || io.getKeysDown(GLFW.GLFW_KEY_RIGHT_ALT)
        io.keySuper = io.getKeysDown(GLFW.GLFW_KEY_LEFT_SUPER) || io.getKeysDown(GLFW.GLFW_KEY_RIGHT_SUPER)
    }

    override fun onMouseClick(xPos: Double, yPos: Double, button: Int) {
        val io = ImGui.getIO()
        val mouseDown = BooleanArray(5)
        mouseDown[0] = button == GLFW.GLFW_MOUSE_BUTTON_1
        mouseDown[1] = button == GLFW.GLFW_MOUSE_BUTTON_2
        mouseDown[2] = button == GLFW.GLFW_MOUSE_BUTTON_3
        mouseDown[3] = button == GLFW.GLFW_MOUSE_BUTTON_4
        mouseDown[4] = button == GLFW.GLFW_MOUSE_BUTTON_5
        io.setMouseDown(mouseDown)
        if (!io.wantCaptureMouse && mouseDown[1]) {
            ImGui.setWindowFocus(null)
        }
    }
    override fun onMouseRelease(xPos: Double, yPos: Double, button: Int) {
        val io = ImGui.getIO()
        val mouseDown = BooleanArray(5)
        mouseDown[0] = false
        mouseDown[1] = false
        mouseDown[2] = false
        mouseDown[3] = false
        mouseDown[4] = false
        io.setMouseDown(mouseDown)
        if (!io.wantCaptureMouse && mouseDown[1]) {
            ImGui.setWindowFocus(null)
        }
    }

    override fun onMouseScroll(xPos: Double, yPos: Double, xScroll: Double, yScroll: Double) {
        val io = ImGui.getIO()
        io.mouseWheelH = io.mouseWheelH + xScroll.toFloat()
        io.mouseWheel = io.mouseWheel + yScroll.toFloat()
    }
}