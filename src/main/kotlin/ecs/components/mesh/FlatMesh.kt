package ecs.components.mesh

import org.joml.Vector2f
import org.joml.Vector4f

open class FlatMesh {
    protected val posSize = 2
    protected val vertexSize = (posSize)

    val color = Vector4f(1f)
    var vertices: FloatArray = FloatArray(0)// Array to store the vertices of the mesh
    var triangles: IntArray = IntArray(0) // Array to store the indices of the triangles in the mesh


    // The interaction value of the mesh
    var clickBoxInteract = 0
        set(value) {
            if (value >= 0) {
                field = value
            } else {
                // Handle the case when a negative value is assigned
                field = 0 // Set the value to 0 as a fallback
            }
        }
    var visualClickBoxFeedback = true
        private set
    fun setVisualClickBox(value : Boolean ) : FlatMesh {
        visualClickBoxFeedback = value
        return this
    }

    // Adds the vertices and triangles from another mesh to this mesh
    fun addMesh(mesh: FlatMesh) {
        val addition = (vertices.size / vertexSize)
        val newTriangles = mesh.triangles.mapIndexed { index, num ->
            num + addition
        }.toIntArray()
        vertices += mesh.vertices
        triangles += newTriangles
    }

    // Adds a quad (rectangle) to the mesh using two diagonal points
    fun addQuad(leftTop: Vector2f, rightBot: Vector2f) {
        return addQuad(leftTop.x, leftTop.y, rightBot.x, rightBot.y)
    }
    // Adds a quad (rectangle) to the mesh using the coordinates of the top-left and bottom-right points
    fun addQuad(startX: Float, startY: Float, endX: Float, endY: Float) {
        // Add four vertices for the quad
        val v1 = addVertex(endX, startY)
        val v2 = addVertex(endX, endY)
        val v3 = addVertex(startX, startY)
        val v4 = addVertex(startX, endY)

        // Add two triangles to form the quad
        addTriangle(v1, v4, v3)
        addTriangle(v1, v4, v2)
    }
    // Adds a vertex to the mesh and returns its index
    fun addVertex(x: Float, y: Float): Int {
        val oldVertexSize = vertices.size
        val newArr = FloatArray(oldVertexSize + vertexSize)
        vertices.copyInto(newArr)
        vertices = newArr
        vertices[oldVertexSize + 0] = x
        vertices[oldVertexSize + 1] = y
        return (oldVertexSize / 2)
    }

    // Sets the color of the mesh using RGBA values
    fun setColor(r: Float, g: Float, b: Float, a: Float): FlatMesh {
        color.x = r
        color.y = g
        color.z = b
        color.w = a
        return this
    }

    // Sets the color of the mesh using a Vector4f color
    fun setColor(c: Vector4f): FlatMesh {
        color.x = c.x
        color.y = c.y
        color.z = c.z
        color.w = c.w
        return this
    }

    // Adds a triangle to the mesh using vertex indices
    fun addTriangle(first: Int, second: Int, third: Int) {
        val oldTriangleSize = triangles.size
        val newArr = IntArray(oldTriangleSize + 3)
        triangles.copyInto(newArr)
        triangles = newArr
        triangles[oldTriangleSize + 0] = first
        triangles[oldTriangleSize + 1] = second
        triangles[oldTriangleSize + 2] = third
    }

    // The depth of the mesh
    private var _depth: Float = 0f
    var depth: Float
        get() = _depth
        set(value) {
            _depth = clampDepth(value)
        }
    // Clamps the depth value between -1.0f and 0.99999f
    private fun clampDepth(value: Float): Float {
        return when {
            value >= 1.0f -> 0.99999f
            value <= -1.0f -> -0.99999f
            else -> value
        }
    }
}