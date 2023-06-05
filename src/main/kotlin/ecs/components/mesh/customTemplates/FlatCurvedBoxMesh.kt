package ecs.components.mesh.customTemplates

import ecs.components.mesh.FlatMesh
import org.joml.Vector2f
import kotlin.math.cos
import kotlin.math.sin

class FlatCurvedBoxMesh(leftTop: Vector2f,rightBot: Vector2f,  cornerRadius: Float, resolution: Int = 4): FlatMesh() {

    init {
        val innerLeftTop = Vector2f(leftTop.x + cornerRadius, leftTop.y - cornerRadius)
        val innerRightTop =  Vector2f(rightBot.x - cornerRadius, leftTop.y - cornerRadius)
        val innerRightBot = Vector2f(rightBot.x - cornerRadius, rightBot.y + cornerRadius)
        val innerLeftBot =  Vector2f(leftTop.x + cornerRadius, rightBot.y + cornerRadius)

        addMesh( FlatOuterCurveMesh( innerLeftTop, -90f, 0f, cornerRadius, resolution) )
        addMesh( FlatOuterCurveMesh( innerRightTop, 0f, 90f, cornerRadius, resolution) )
        addMesh( FlatOuterCurveMesh( innerRightBot, 90f, 180f, cornerRadius, resolution) )
        addMesh( FlatOuterCurveMesh( innerLeftBot, 180f, 270f, cornerRadius, resolution) )

        if(innerRightBot.x - innerLeftTop.x > 0)
            addQuad(Vector2f(innerLeftTop.x, leftTop.y) , Vector2f(innerRightBot.x, rightBot.y ))

        if(innerLeftTop.y - innerRightBot.y  > 0)
            addQuad(Vector2f(leftTop.x, innerLeftTop.y) , Vector2f(rightBot.x, innerRightBot.y ))
    }
}