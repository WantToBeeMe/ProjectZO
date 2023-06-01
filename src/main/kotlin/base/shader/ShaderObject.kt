package base.shader

import org.joml.*
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL20.*

//Description: a class that loads and saves the whole shader program
//------
//Usage:  use the `Shader` enum class to get a shader (or make an instance and call .compile(), but dont plz, thanks)
// then do .use() before the preparation of every draw call (so probably on top of the loop), and .detach() after the draw call is done to make room for other shaders again
// upload any uniforms by one of the UploadX() methods

class ShaderObject(private val vertexPath : String, private val fragmentPath : String) {
    private var vertexSource = "";
    private var fragmentSource = "";
    var shaderProgramID : Int = 0;
    private var beingUsed = false;

    init{
        val vertexStream = this::class.java.getResourceAsStream(vertexPath)
        vertexSource = vertexStream?.bufferedReader()?.use { it.readText() } ?:
                run { println("wrong path $vertexPath"); assert(false); "" }

        val fragmentStream = this::class.java.getResourceAsStream(fragmentPath)
        fragmentSource = fragmentStream?.bufferedReader()?.use { it.readText() } ?:
                run { println("wrong path $fragmentPath"); assert(false); "" }
    }


    //only once after creating the shader
    //makes sure the shader is loaded in openGL
    fun compile(){
        //compile and link the shaders
        //load and compile the vertex shaders
        val vertexID : Int = glCreateShader(GL_VERTEX_SHADER)
        //pass the shader to the gpu
        glShaderSource(vertexID, vertexSource)
        glCompileShader(vertexID)
        //check for errors in compilation presses
        val successVertex = glGetShaderi(vertexID, GL_COMPILE_STATUS)
        if(successVertex == GL_FALSE){
            val len = glGetShaderi(vertexID, GL_INFO_LOG_LENGTH)
            println("ERROR : vertex shader compilation failed \n ${glGetShaderInfoLog(vertexID, len)}")
            glDeleteShader(vertexID)//taking the shader out of memory again (it will assert false so it's not really needed, but it's not bad to do it anyway)
            assert(false);
        }

        //load and compile the vertex shaders
        val fragmentID : Int = glCreateShader(GL_FRAGMENT_SHADER)
        //pass the shader to the gpu
        glShaderSource(fragmentID, fragmentSource)
        glCompileShader(fragmentID)
        //check for errors in compilation presses
        val successFragment = glGetShaderi(fragmentID, GL_COMPILE_STATUS)
        if(successFragment == GL_FALSE){
            val len = glGetShaderi(fragmentID, GL_INFO_LOG_LENGTH)
            println("ERROR : fragment shader compilation failed \n ${glGetShaderInfoLog(fragmentID, len)}")
            glDeleteShader(fragmentID)//taking the shader out of memory again (it will assert false so it's not really needed, but it's not bad to do it anyway)
            assert(false);
        }

        //link shaders and check for errors
        shaderProgramID = glCreateProgram()
        //attach the 2 shaders to this program
        glAttachShader(shaderProgramID, vertexID)
        glAttachShader(shaderProgramID, fragmentID)
        //try to link the program again
        glLinkProgram(shaderProgramID)
        val successLink = glGetProgrami(shaderProgramID, GL_LINK_STATUS)
        if(successLink == GL_FALSE){
            val len = glGetProgrami(shaderProgramID, GL_INFO_LOG_LENGTH)
            println("ERROR : linking shader failed \n ${glGetProgramInfoLog(fragmentID, len)}")
            glDeleteProgram(shaderProgramID)
            glDeleteShader(vertexID)
            glDeleteShader(fragmentID)
            assert(false);
        }

        //the program has successfully compiled which means we don't really need the shaders anymore.
        //therefore we can just detach and remove the shaders to free up some memory
        glDetachShader(shaderProgramID, vertexID)
        glDetachShader(shaderProgramID, fragmentID)
        glDeleteShader(vertexID)
        glDeleteShader(fragmentID)


    }


    //at every start of every loop when using this shader.
    // to make sure openGL knows we want to use this shader for its drawing call
    private var depthTest = false
    private var blend = false
    fun use(){
        if(beingUsed) return
        //Bind shader program
        glUseProgram(shaderProgramID)
        beingUsed = true
    }

    fun enableBlend(){
        glEnable(GL_BLEND)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
        blend = true;
    }
    fun enableDepthTest(){
        glEnable(GL_DEPTH_TEST)
        glDepthFunc(GL_LESS)
        depthTest = true;
    }
    //at every end of every loop
    // to make sure if there is no shader used in the following iteration, that this one is also gone
    fun detach(){
        glUseProgram(0)
        beingUsed = false
        if(blend) glDisable(GL_BLEND)
        if(depthTest) glDisable(GL_DEPTH_TEST)
    }



    //uploading uniforms to the shader, speaks for itself I think
    fun  uploadMat4f(varName : String, mat4 : Matrix4f){
        val varLocation = glGetUniformLocation(shaderProgramID, varName)
        use(); //makes sure you are using the shader
        val matBuffer = BufferUtils.createFloatBuffer(16)
        mat4.get(matBuffer)
        glUniformMatrix4fv(varLocation, false, matBuffer)
    }
    fun  uploadMat3f(varName : String, mat3 : Matrix3f){
        val varLocation = glGetUniformLocation(shaderProgramID, varName)
        use(); //makes sure you are using the shader
        val matBuffer = BufferUtils.createFloatBuffer(9)
        mat3.get(matBuffer)
        glUniformMatrix3fv(varLocation, false, matBuffer)
    }

    fun uploadVec4f(varName : String, vec4 : Vector4f){
        val varLocation = glGetUniformLocation(shaderProgramID, varName)
        use()
        glUniform4f(varLocation, vec4.x, vec4.y, vec4.z, vec4.w)
    }
    fun uploadVec3f(varName : String, vec3 : Vector3f){
        val varLocation = glGetUniformLocation(shaderProgramID, varName)
        use()
        glUniform3f(varLocation, vec3.x, vec3.y, vec3.z)
    }
    fun uploadVec2f(varName : String, vec2 : Vector2f){
        val varLocation = glGetUniformLocation(shaderProgramID, varName)
        use()
        glUniform2f(varLocation, vec2.x, vec2.y)
    }

    fun uploadFloat(varName: String, fl : Float){
        val varLocation = glGetUniformLocation(shaderProgramID, varName)
        use()
        glUniform1f(varLocation, fl)
    }
    fun uploadInt(varName : String, intt : Int){
        val varLocation = glGetUniformLocation(shaderProgramID, varName)
        use()
        glUniform1i(varLocation, intt)
    }
    //the same as uploading an int, but I know I will forget so that's why I have this func
    fun uploadText(varName : String, slot : Int){
        uploadInt(varName, slot)
    }

    fun uploadIntArray(varName : String,array : IntArray ){
        val varLocation = glGetUniformLocation(shaderProgramID, varName)
        use()
        glUniform1iv(varLocation, array)
        //v means Value
        //that means its a value pointer,
    }

}