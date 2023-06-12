package ecs.singletons

import org.joml.Matrix4f
import org.joml.Vector3f


//todo: adjust projection on window resize
class Camera {
    private val _projectionMatrix : Matrix4f = Matrix4f();
    val  projectionMatrix get() = _projectionMatrix
    private val _viewMatrix : Matrix4f = Matrix4f();
    val  viewMatrix get() = _viewMatrix

    private var _aspect = 0f
    val aspect get() = _aspect
    val nearPlane = -1f
    val farPlane = 1f


    init{
        generateProjectionMatrix()
        generateViewMatrix()
    }

    fun resizeViewPort(width: Float, height:Float){
        generateProjectionMatrix(width,height)
    }


    private fun generateProjectionMatrix(width : Float =1920f, height : Float =1080f){
        _projectionMatrix.identity();
        _aspect = width / height
        val left = -1f* _aspect
        val right = 1f* _aspect
        val bottom = -1f
        val top = 1f

        _projectionMatrix.ortho(left, right, bottom, top, nearPlane, farPlane)
        //projectionMatrix.perspective(1.1f, aspect, 0.2f, 100f);
    }

    private fun generateViewMatrix(position : Vector3f = Vector3f(), direction : Vector3f = Vector3f(0f,0f,-1f)) {
        this._viewMatrix.identity()
        _viewMatrix.lookAt(
            position, //position standing
            Vector3f(position).add(direction), //looking at
            Vector3f(0f, 1f, 0f)  //up direction
        )
    }
}