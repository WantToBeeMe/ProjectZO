package ecs.systems

import base.shader.Shader
import base.shader.ShaderObject
import base.util.IImGuiWindow
import base.util.ImGuiController
import ecs.ECSController
import ecs.components.CameraComponent
import ecs.components.FlatMeshComponent
import ecs.components.OpenMeshComponent
import ecs.components.TransformComponent
import imgui.ImBool
import imgui.ImGui
import imgui.enums.ImGuiCond
import org.lwjgl.opengl.GL45.*

//todo: for now it takes in the first camera component that exist and takes that as base thingy
object MeshRenderSystem : IEntityComponentSystem() {
    private val flatShader : ShaderObject = Shader.FLAT_OBJECT.get()
    private val openShader : ShaderObject = Shader.OPEN_OBJECT.get()

    private val identityTransform = TransformComponent()

    private val showWireFrames = ImBool()

    override fun start(controller: ECSController) {
        super.start(controller)
    }

    override fun update( dt: Float) {
        super.update(dt)
        val flatMeshes = controller.getComponents<FlatMeshComponent>()
        val openMeshes = controller.getComponents<OpenMeshComponent>()
        val transforms = controller.getComponents<TransformComponent>()

        val cameras = controller. getComponents<CameraComponent>()
        val cameraID = cameras.keys.first()
        val firstCamera = cameras[cameraID]!!

        if(showWireFrames.get()) glPolygonMode( GL_FRONT_AND_BACK, GL_LINE );
        else glPolygonMode( GL_FRONT_AND_BACK, GL_FILL );


        var currentShader = openShader
        currentShader.use()
        currentShader.enableBlend()
        currentShader.enableDepthTest()

        currentShader.uploadMat4f("uProjection", firstCamera.projectionMatrix )
        currentShader.uploadMat4f("uView", firstCamera.viewMatrix)

        for(mesh in openMeshes){
            val transformComp = transforms[mesh.key] ?: identityTransform
            currentShader.uploadMat3f("uTransform", transformComp.transform)
            currentShader.uploadFloat("uDepth", mesh.value.depth)
            mesh.value.render()
        }

        currentShader.detach()



        currentShader = flatShader
        currentShader.use()
        currentShader.enableBlend()
        currentShader.enableDepthTest()

        currentShader.uploadMat4f("uProjection", firstCamera.projectionMatrix )
        currentShader.uploadMat4f("uView", firstCamera.viewMatrix)

        for(mesh in flatMeshes){
            val transformComp = transforms[mesh.key] ?: identityTransform
            currentShader.uploadMat3f("uTransform", transformComp.transform)
            currentShader.uploadFloat("uDepth", mesh.value.depth)
            currentShader.uploadVec4f("uColor", mesh.value.color)
            currentShader.uploadInt("uInteract", mesh.value.interact)
            mesh.value.render()
        }
        currentShader.detach()
    }

    override fun guiOptions() {
        ImGui.pushID("Mesh_Renderer_System");
        if (ImGui.beginTabItem("Renderer" )) {
            ImGui.checkbox("Show wireframe", showWireFrames)
            ImGui.endTabItem();
        }
        ImGui.popID()
    }

}