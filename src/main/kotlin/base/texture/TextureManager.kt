package base.texture

//Description: a class to make sure all the shaders are loaded only once
//------
//Usage: dont, is done automatically in the `Shader` class

object TextureManager {
    private val textures : MutableMap<String, TextureObject> = hashMapOf()

    fun getTexture(path : String) : TextureObject {
        if(!textures.containsKey(path)){
            println("trying to get a texture that doesnt exist : $path")
        }
        return textures[path]!!
    }
    fun loadTexture(path : String ) : TextureObject {
        if(!textures.containsKey(path)){
            val shader = TextureObject(path)
            textures[path] = shader
        }
        return textures[path]!!
    }
}