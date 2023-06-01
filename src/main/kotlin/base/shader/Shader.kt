package base.shader

//Description: a class to make sure all the shaders have easy access
//------
//Usage: just put the shader with the path in here
// then you only have to type `Shader.DEFAULT.get()` where you want the shader

enum class Shader(vertexPath:String, fragmentPath : String ) {
    FLAT_OBJECT("/shaders/flatObject.vert", "/shaders/object.frag"),
    OPEN_OBJECT("/shaders/openObject.vert", "/shaders/object.frag"),
    ;

    init{
        ShaderManager.loadShader(vertexPath, fragmentPath, name)
    }
    fun get() : ShaderObject {
        return ShaderManager.getShader(name)
    }
}