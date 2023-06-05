package ecs.components.mesh

import org.joml.Vector2f
import org.joml.Vector4f

open class FlatMesh {
    protected val posSize = 2
    protected val vertexSize = (posSize)

    val color = Vector4f(1f)
    var vertices : FloatArray = FloatArray(0)
    var triangles : IntArray = IntArray(0)

    var clickable = false

    var interact = 0
        set(value) {
            if (value >= 0) {
                field = value
            } else {
                // Handle the case when a negative value is assigned
                field = 0 // Set the value to 0 as a fallback
            }
        }

    fun addMesh(mesh : FlatMesh){
        val addition = (vertices.size/vertexSize)
        val newTriangles = mesh.triangles.mapIndexed { index, num ->
            num+addition
        }.toIntArray()
        vertices += mesh.vertices
        triangles += newTriangles
    }

    fun addQuad(leftTop : Vector2f, rightBot: Vector2f){
        return addQuad(leftTop.x, leftTop.y, rightBot.x, rightBot.y)
    }
    fun addQuad(startX:Float,startY:Float,endX:Float,endY:Float) {
        val v1 = addVertex(endX, startY)
        val v2 = addVertex(endX, endY)
        val v3 = addVertex(startX, startY)
        val v4 = addVertex(startX, endY)
        addTriangle(v1,v4,v3)
        addTriangle(v1,v4,v2)
    }
    fun addVertex(x:Float, y:Float) : Int{
        val oldVertexSize  = vertices.size
        val newArr = FloatArray(oldVertexSize + vertexSize)
        vertices.copyInto(newArr)
        vertices = newArr
        vertices[oldVertexSize + 0] = x
        vertices[oldVertexSize + 1] = y
        return (oldVertexSize/2)
    }
    fun setColor(r:Float, g:Float, b:Float, a:Float) : FlatMesh {
        color.x = r
        color.y = g;
        color.z = b;
        color.w = a;
        return this;
    }
    fun setColor(c: Vector4f): FlatMesh {
        color.x = c.x
        color.y = c.y
        color.z = c.z
        color.w = c.w
        return this
    }

    fun addTriangle(first: Int, second:Int, third: Int){
        val oldTriangleSize=  triangles.size
        val newArr = IntArray(oldTriangleSize + 3)
        triangles.copyInto(newArr)
        triangles = newArr
        triangles[oldTriangleSize + 0] = first
        triangles[oldTriangleSize + 1] = second
        triangles[oldTriangleSize + 2] = third
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