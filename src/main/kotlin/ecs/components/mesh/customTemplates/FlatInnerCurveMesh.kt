package ecs.components.mesh.customTemplates

import ecs.components.mesh.FlatMesh
import org.joml.Vector2f
import kotlin.math.cos
import kotlin.math.sin

class FlatInnerCurveMesh(rotateAround: Vector2f, degreesAngleStart: Float, degreesAngleEnd: Float, radius: Float, resolution: Int = 5) : FlatMesh() {
    init {
        val centerX = rotateAround.x
        val centerY = rotateAround.y
        val resolution = if(resolution > 0) resolution else 1
        val diff = (degreesAngleEnd - degreesAngleStart + 180) % 360f
        val middle = (degreesAngleStart + diff / 2 + 360) % 360f
        val middleRad=  Math.toRadians(middle.toDouble())
        //this is obviously not correct, but oh well, who caress
        val amountX = -cos(middleRad).toFloat()*1.4f
        val amountY = sin(middleRad).toFloat()* 1.4f
        val cornerVertex = addVertex(centerX + radius * amountX, centerY+ radius*amountY)


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