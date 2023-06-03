package ecs.components.clickBox

import org.joml.Vector2f
import org.lwjgl.opengl.GL45.*

class ClickBoxComponent {
    private val clickBoxes : MutableList<IClickBox> = mutableListOf()

    private var vertices : FloatArray = FloatArray(0)
    private var lines : IntArray = IntArray(0)
    private var vaoID : Int = 0
    private var vboID : Int = 0
    private var eboID : Int = 0
    private var created = false

    fun addClickBox(box : IClickBox) : ClickBoxComponent{
        clickBoxes.add(box)
        return this
    }
    fun removeClickBox(box : IClickBox) : Boolean{
        return clickBoxes.remove(box)
    }

    fun isInside(point: Vector2f): Boolean {
        for(box in clickBoxes)
            if( box.isInside(point) )
                return true
        return false
    }

    var showDebugLines = false
    fun renderClickBox(){
        if(!created) createRenderData()
        glBindBuffer(GL_ARRAY_BUFFER,vboID)
        //glBufferSubData(GL_ARRAY_BUFFER, 0, vertices)

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER,eboID)
        //glBufferSubData(GL_ELEMENT_ARRAY_BUFFER, 0, triangles)

        glBindVertexArray(vaoID)

        glEnableVertexAttribArray(0)
        glDrawElements(GL_LINES,lines.size, GL_UNSIGNED_INT, 0)
        //disable them again, because they are drawn
        glDisableVertexAttribArray(0)

        glBindVertexArray(0)
        glBindBuffer(GL_ARRAY_BUFFER, 0)
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0)
    }
    private fun createRenderData(){
        if(created) return
        created = true

        for(box in clickBoxes){
            val a = box.getBoxOutline()
            vertices += a.first
            a.second.forEach { num -> num+lines.size }
            lines += a.second
        }

        //generate and bind a vertex array object
        vaoID = glGenVertexArrays()
        glBindVertexArray(vaoID)

        //allocate space for the vertices
        vboID = glGenBuffers()
        glBindBuffer(GL_ARRAY_BUFFER, vboID)
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW)

        //create and upload indices buffer
        eboID = glGenBuffers()
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID)
        glBufferData(GL_ELEMENT_ARRAY_BUFFER,lines, GL_STATIC_DRAW)

        val posSize= 2
        val vertexSizeFloats= (posSize)*Float.SIZE_BYTES // (posSize + colorSize)*Float.SIZE_BYTES
        glVertexAttribPointer(0, posSize, GL_FLOAT, false, vertexSizeFloats, 0.toLong())

        //done creating, reset everything just to be clean
        glBindVertexArray( 0)
        glBindBuffer(GL_ARRAY_BUFFER, 0)
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0)
    }


}