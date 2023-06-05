package ecs.components.mesh.customTemplates

import ecs.components.mesh.FlatMesh
import org.joml.Vector2f
import kotlin.math.cos
import kotlin.math.sin

class FlatCircleMesh(center: Vector2f, radius: Float, resolution: Int = 8) : FlatMesh() {
    init {
        val centerX = center.x
        val centerY = center.y
        val resolution = if(resolution > 2) resolution else 3
        val centerVertex = addVertex(centerX, centerY) // Add center vertex

        val angleIncrement = (2 * Math.PI / resolution)
        var angle = 0.0

        // Calculate positions of the vertices around the circle
        val vertices = mutableListOf<Int>()
        for (i in 0 until resolution) {
            val x = centerX + radius * cos(angle).toFloat()
            val y = centerY + radius * sin(angle).toFloat()

            val vertexIndex = addVertex(x, y)
            vertices.add(vertexIndex)

            angle += angleIncrement
        }

        // Create triangles using the vertices
        for (i in 0 until resolution) {
            val vertex1 = centerVertex
            val vertex2 = vertices[i]
            val vertex3 = vertices[(i + 1) % resolution]

            addTriangle(vertex1, vertex2, vertex3)
        }
    }
}