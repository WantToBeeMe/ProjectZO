package ecs.components.mesh

import ecs.components.mesh.customTemplates.FlatMesh
import org.lwjgl.opengl.GL45.*

class FlatMeshComponent : FlatMesh() {

    private var vaoID : Int = 0
    private var vboID : Int = 0
    private var eboID : Int = 0
    private var created = false

    fun create(){
        if(created) return
        created = true
        //generate and bind a vertex array object
        vaoID = glGenVertexArrays()
        glBindVertexArray(vaoID)

        //allocate space for the vertices
        vboID = glGenBuffers()
        glBindBuffer(GL_ARRAY_BUFFER, vboID)
        glBufferData(GL_ARRAY_BUFFER, (vertices.size * Float.SIZE_BYTES).toLong(), GL_DYNAMIC_DRAW)

        //create and upload indices buffer
        eboID = glGenBuffers()
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID)
        glBufferData(GL_ELEMENT_ARRAY_BUFFER,triangles, GL_STATIC_DRAW)

        val vertexSizeFloats= (vertexSize)*Float.SIZE_BYTES // (posSize + colorSize)*Float.SIZE_BYTES
        glVertexAttribPointer(0, posSize, GL_FLOAT, false, vertexSizeFloats, 0.toLong())
        //glVertexAttribPointer(1, colorSize, GL_FLOAT, false, vertexSizeFloats,  (posSize*Float.SIZE_BYTES).toLong())

        //done creating, reset everything just to be clean
        glBindVertexArray( 0)
        glBindBuffer(GL_ARRAY_BUFFER, 0)
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0)
    }

    fun render(){
        if(!created) return
        glBindBuffer(GL_ARRAY_BUFFER,vboID)
        glBufferSubData(GL_ARRAY_BUFFER, 0, vertices)

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER,eboID)
        //glBufferSubData(GL_ELEMENT_ARRAY_BUFFER, 0, triangles)

        glBindVertexArray(vaoID)

        //val v= if(verticesIndex-2 < 0) 0 else verticesIndex-2
        //Enable vertex attributes pointers
        glEnableVertexAttribArray(0)
        glDrawElements(GL_TRIANGLES,triangles.size, GL_UNSIGNED_INT, 0)
        //disable them again, because they are drawn
        glDisableVertexAttribArray(0)


        glBindVertexArray(0)
        glBindBuffer(GL_ARRAY_BUFFER, 0)
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0)
    }

    fun clear(){
        if(!created) return
        vertices = FloatArray(0)
        triangles  = IntArray(0)
        glDeleteVertexArrays(vaoID)
        glDeleteBuffers(vboID)
        glDeleteBuffers(eboID)
        created = false
    }
}