package ecs.components.mesh.customTemplates

import ecs.components.mesh.FlatMesh
import org.joml.Vector2f
import kotlin.math.cos
import kotlin.math.sin

class FlatCustomCurveMesh(rotateAround: Vector2f,connectPoint: Vector2f, degreesAngleStart: Float, degreesAngleEnd: Float, radius: Float, resolution: Int = 5) : FlatMesh() {
    init {
        val centerX = rotateAround.x
        val centerY = rotateAround.y
        val resolution = if(resolution > 0) resolution else 1

        val cornerVertex = addVertex(connectPoint.x, connectPoint.y)


        var totalRotationAngle = (degreesAngleEnd - degreesAngleStart) % 360f
        if (totalRotationAngle < 0)  totalRotationAngle += 360f
        val angleIncrement = (Math.toRadians(totalRotationAngle.toDouble()) / resolution)
        var angle = Math.toRadians(degreesAngleStart.toDouble())

        // Calculate positions of the vertices around the circle
        val vertices = mutableListOf<Int>()
        for (i in 0 .. resolution) {
            val rotatedAngle = (90 - Math.toDegrees(angle)) % 360
            val x = centerX + radius * cos(Math.toRadians(rotatedAngle)).toFloat()
            val y = centerY + radius * sin(Math.toRadians(rotatedAngle)).toFloat()

            val vertexIndex = addVertex(x, y)
            vertices.add(vertexIndex)
            angle += angleIncrement

            if(i != 0)
                addTriangle(cornerVertex,vertexIndex, vertexIndex-1 )
        }
    }
}