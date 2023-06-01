package base.texture

import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11.*
import org.lwjgl.stb.STBImage.stbi_image_free
import org.lwjgl.stb.STBImage.stbi_load
import java.io.File

//Description: a class for loading images in to openGl
//------
//Usage: By creating a Texture it is saved in openGL or somewhere at least
// to load one you can simply use `glBindTextureUnit(index, texture.id())`
// with `index` being the index of the TextureSlots (of gl) you want to save them in

class TextureObject(private val filepath : String) {
    private var textID : Int = 0;

    init{
        textID = glGenTextures()
        if(textID != 0) init()
        else println("boilerplate.Texture did not load properly $filepath")
    }

    //makes sure the image is loaded in openGL
    private fun init(){
        glBindTexture(GL_TEXTURE_2D, textID)
        //setting the settings of this image
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST) //min is for stretching
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST) //min is for shrinking

        //load the image
        val width = BufferUtils.createIntBuffer(1)
        val height = BufferUtils.createIntBuffer(1)
        val channels = BufferUtils.createIntBuffer(1)

        val path = File(filepath).absolutePath

        val image = stbi_load(path, width, height,channels, 4) ?: run {
            println("stbi did not load properly $path")
            return
        }
        println("image: $textID you passed in is ${width.get(0)}px by ${height.get(0)}px and with ${channels.get(0)} channels ($filepath)")
        //uploads the pixels to the gpu
        glTexImage2D(GL_TEXTURE_2D, 0 , GL_RGBA, width.get(0), height.get(0), 0 , GL_RGBA, GL_UNSIGNED_BYTE, image)

        //frees the memory, we uploaded to the gpu, so we don't need this data anymore
        glBindTexture(GL_TEXTURE_2D, 0)
        stbi_image_free(image)
    }

    fun id(): Int{return textID;}
}