package ecs.components.clickBox

import org.lwjgl.opengl.GL45.*

object MouseClickBoxVisualizer {

    private var vaoID : Int = 0
    private var vboID : Int = 0
    private var eboID : Int = 0
    private var created = false
    fun renderMouse(x:Float, y:Float){
        if(!created) createMouseRenderStuff()


        glBindBuffer(GL_ARRAY_BUFFER,vboID)
        glBufferSubData(GL_ARRAY_BUFFER, 0, floatArrayOf(x-0.005f,y+0.005f))

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER,eboID)
        //glBufferSubData(GL_ELEMENT_ARRAY_BUFFER, 0, triangles)
        glBindVertexArray(vaoID)

        glEnableVertexAttribArray(0)
        glDrawElements(GL_POINTS,1, GL_UNSIGNED_INT, 0)
        //disable them again, because they are drawn
        glDisableVertexAttribArray(0)

        glBindVertexArray(0)
        glBindBuffer(GL_ARRAY_BUFFER, 0)
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0)

    }
    private fun createMouseRenderStuff(){
        if(created) return
        created = true

        //generate and bind a vertex array object
        vaoID = glGenVertexArrays()
        glBindVertexArray(vaoID)

        //allocate space for the vertices
        vboID = glGenBuffers()
        glBindBuffer(GL_ARRAY_BUFFER, vboID)
        glBufferData(GL_ARRAY_BUFFER, (2 * Float.SIZE_BYTES).toLong(), GL_DYNAMIC_DRAW)

        //create and upload indices buffer
        eboID = glGenBuffers()
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID)
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, intArrayOf(0), GL_STATIC_DRAW)

        val posSize=2
        val vertexSizeFloats= (posSize)*Float.SIZE_BYTES // (posSize + colorSize)*Float.SIZE_BYTES
        glVertexAttribPointer(0, posSize, GL_FLOAT, false, vertexSizeFloats, 0.toLong())

        //done creating, reset everything just to be clean
        glBindVertexArray( 0)
        glBindBuffer(GL_ARRAY_BUFFER, 0)
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0)

    }
}