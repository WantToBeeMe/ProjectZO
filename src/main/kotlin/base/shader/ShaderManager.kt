package base.shader

//Description: a class to make sure all the shaders are loaded only once
//------
//Usage: dont, is done automatically in the `Shader` class

object ShaderManager {
    private val shaders : MutableMap<String, ShaderObject> = hashMapOf()

    fun getShader(name : String) : ShaderObject {
        if(!shaders.containsKey(name)){
            println("trying to get a shader that doesnt exist : $name")
        }
        return shaders[name]!!
    }
    fun loadShader(verPath : String, fragPath :String, name : String ) : ShaderObject {
        if(!shaders.containsKey(name)){
            val shader = ShaderObject(verPath, fragPath)
            shader.compile()
            shaders[name] = shader
        }
        return shaders[name]!!
    }
}