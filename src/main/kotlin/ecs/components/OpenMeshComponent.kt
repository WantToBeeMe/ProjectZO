package ecs.components

import org.joml.Vector4f
import org.lwjgl.opengl.GL45.*

class OpenMeshComponent {
    private val posSize = 3
    private val colorSize = 4
    private val vertexSize = (posSize+colorSize)

    var vertices : FloatArray = FloatArray(0)
    var triangles : IntArray = IntArray(0)
    private var triangleIndex = 0
    private var vertexIndex = 0

    private var vaoID : Int = 0
    private var vboID : Int = 0
    private var eboID : Int = 0
    private var created = false

    fun addQuad(startX:Float,startY:Float,endX:Float,endY:Float, color:Vector4f, height: Float = 0f) {
        val v1 = addVertex(endX, startY, color, height)
        val v2 = addVertex(endX, endY, color, height)
        val v3 = addVertex(startX, startY, color, height)
        val v4 = addVertex(startX, endY, color, height)
        addTriangle(v1,v4,v3)
        addTriangle(v1,v4,v2)
    }
    fun addVertex(x:Float, y:Float,r:Float,g:Float,b:Float,a:Float , height: Float = 0f) : Int{
        val realIndex = (vertexIndex*vertexSize)
        val newArr = FloatArray(vertices.size + vertexSize)
        vertices.copyInto(newArr)
        vertices = newArr
        vertices[realIndex + 0] = x
        vertices[realIndex + 1] = y
        vertices[realIndex + 2] = height/10000000f

        vertices[realIndex + 0 + posSize] = r
        vertices[realIndex + 1 + posSize] = g
        vertices[realIndex + 2 + posSize] = b
        vertices[realIndex + 3 + posSize] = a

        return vertexIndex++
    }
    fun addVertex(x:Float,y:Float, color: Vector4f, height: Float = 0f) :Int{
        return addVertex(x,y,color.x,color.y,color.z,color.w, height)
    }

    fun addTriangle(first: Int, second:Int, third: Int) : Int{
        val newArr = IntArray(triangles.size + 3)
        triangles.copyInto(newArr)
        triangles = newArr
        triangles[triangleIndex*3 + 0] = first
        triangles[triangleIndex*3 + 1] = second
        triangles[triangleIndex*3 + 2] = third
        return triangleIndex++
    }

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
        glVertexAttribPointer(1, colorSize, GL_FLOAT, false, vertexSizeFloats,  (posSize*Float.SIZE_BYTES).toLong())

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
        glEnableVertexAttribArray(1)
        glDrawElements(GL_TRIANGLES,triangles.size, GL_UNSIGNED_INT, 0)
        //disable them again, because they are drawn
        glDisableVertexAttribArray(0)
        glDisableVertexAttribArray(1)


        glBindVertexArray(0)
        glBindBuffer(GL_ARRAY_BUFFER, 0)
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0)
    }

    fun clear(){
        if(!created) return
        vertices = FloatArray(0)
        triangles  = IntArray(0)
        triangleIndex = 0
        vertexIndex = 0
        glDeleteVertexArrays(vaoID)
        glDeleteBuffers(vboID)
        glDeleteBuffers(eboID)
        created = false
    }


    private var _depth: Float = 0.0f
    var depth: Float
        get() = _depth
        set(value) {
            _depth = clampDepth(value)
        }
    private fun clampDepth(value: Float): Float {
        return when {
            value >= 1.0f -> 0.99999f
            value <= -1.0f -> -0.99999f
            else -> value
        }
    }



}