package ZO

import base.util.IImGuiWindow
import base.util.ImGuiController
import imgui.ImBool
import imgui.ImGui
import imgui.ImString
import imgui.ImVec2
import imgui.enums.ImGuiColorEditFlags
import imgui.enums.ImGuiCond
import imgui.enums.ImGuiInputTextFlags
import imgui.enums.ImGuiMouseCursor

class ImGuiTestWindow : IImGuiWindow {

    //for the custom window
    private val imguiDemoLink = "https://raw.githubusercontent.com/ocornut/imgui/v1.75/imgui_demo.cpp" // Link to put into clipboard
    private val windowSize = ImVec2() // Vector to store "Custom Window" size
    private val windowPos = ImVec2() // Vector to store "Custom Window" position
    private val testPayload = "Test Payload".toByteArray() // Test data for payload. Should be represented as raw byt array.
    private var dropTargetText = "Drop Here"
    private val backgroundColor = floatArrayOf(0.5f, 0f, 0f) // To modify background color dynamically
    private var clickCount = 0
    private val resizableStr = ImString(5)
    private val showDemoWindow = ImBool()


    fun start(){
        ImGuiController.addGui(this)
    }
    fun stop(){
        ImGuiController.removeGui(this)
    }

    override fun showUi() {
        ImGui.setNextWindowSize(600f, 300f, ImGuiCond.Once)
        ImGui.setNextWindowPos(10f, 10f, ImGuiCond.Once)
        ImGui.begin("Custom window") // Start Custom window
        // Example of how to draw an image in the bottom-right corner of the window
        //ImGui.getWindowSize(windowSize)
        //ImGui.getWindowPos(windowPos)
        //val xPoint = windowPos.x + windowSize.x - 100
        //val yPoint = windowPos.y + windowSize.y
        //ImGui.getWindowDrawList().addImage(dukeTexture, xPoint, yPoint - 180, xPoint + 100, yPoint)

        // Simple checkbox to show demo window
        ImGui.checkbox("Show demo window", showDemoWindow)
        ImGui.separator()

        // Drag'n'Drop functionality
        ImGui.button("Drag me")
        if (ImGui.beginDragDropSource()) {
            ImGui.setDragDropPayload("payload_type", testPayload, testPayload.size)
            ImGui.text("Drag started")
            ImGui.endDragDropSource()
        }
        ImGui.sameLine()
        ImGui.text(dropTargetText)
        if (ImGui.beginDragDropTarget()) {
            val payload = ImGui.acceptDragDropPayload("payload_type")
            if (payload != null) {
               dropTargetText = String(payload)
            }
            ImGui.endDragDropTarget()
        }

        // Color picker
        ImGui.alignTextToFramePadding()
        ImGui.text("Background color:")
        ImGui.sameLine()
        ImGui.colorEdit3(
            "##click_counter_col",
            backgroundColor,
            ImGuiColorEditFlags.NoInputs or ImGuiColorEditFlags.NoDragDrop
        )

        // Simple click counter
        if (ImGui.button("Click")) {
            clickCount++
        }
        if (ImGui.isItemHovered()) {
            ImGui.setMouseCursor(ImGuiMouseCursor.Hand)
        }
        ImGui.sameLine()
        ImGui.text("Count: $clickCount")
        ImGui.separator()

        // Input field with auto-resize ability
        ImGui.text("You can use text inputs with auto-resizable strings!")
        ImGui.inputText("Resizable input", resizableStr, ImGuiInputTextFlags.CallbackResize)
        ImGui.text("text len:")
        ImGui.sameLine()
        ImGui.textColored(.12f, .6f, 1f, 1f, Integer.toString(resizableStr.length))
        ImGui.sameLine()
        ImGui.text("| buffer size:")
        ImGui.sameLine()
        ImGui.textColored(1f, .6f, 0f, 1f, Integer.toString(resizableStr.bufferSize))
        ImGui.separator()
        ImGui.newLine()

        // Link to the original demo file
        ImGui.text("Consider to look the original ImGui demo: ")
        ImGui.setNextItemWidth(500f)
        ImGui.textColored(0f, .8f, 0f, 1f, imguiDemoLink)
        ImGui.sameLine()
        if (ImGui.button("Copy")) {
            ImGui.setClipboardText(imguiDemoLink)
        }

        ImGui.end() // End Custom window
        if (showDemoWindow.get()) {
            ImGui.showDemoWindow(showDemoWindow)
        }
    }
}