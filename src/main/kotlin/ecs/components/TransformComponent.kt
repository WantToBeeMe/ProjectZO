package ecs.components


import org.joml.Matrix3f
import org.joml.Vector2f



class TransformComponent  {
    private var transformCreated: Boolean = false
    private val _transform: Matrix3f = Matrix3f()
    private var _position: Vector2f = Vector2f(0.0f, 0.0f)
    private var _rotation: Float = 0.0f
    private var _scale: Vector2f= Vector2f(1.0f, 1.0f)

    val transform: Matrix3f
        get() {
            if (!transformCreated) {
                _transform.identity()
                _transform.m20(_position.x)
                _transform.m21(_position.y)
                _transform.rotate(_rotation,0f,0f,-1f)
                _transform.scale(_scale.x, _scale.y,1f)
                transformCreated = true
            }
            return _transform
        }

    fun setPosition(x: Float, y: Float)  : TransformComponent {
        _position = Vector2f(x, y)
        transformCreated = false
        return this
    }
    fun setRotation(degrees: Float, inputIsRad: Boolean = false) : TransformComponent {
        _rotation = if(inputIsRad) degrees else Math.toRadians(degrees.toDouble()).toFloat()
        transformCreated = false
        return this
    }

    fun setScale(x: Float, y: Float) : TransformComponent {
        _scale = Vector2f(x, y)
        transformCreated = false
        return this
    }
    fun setScale(radius: Float) : TransformComponent {
        _scale = Vector2f(radius, radius)
        transformCreated = false
        return this
    }

    fun getRotation(getRad : Boolean = false) : Float{
        return if(getRad) _rotation else Math.toDegrees(_rotation.toDouble()).toFloat()
    }
    fun getPosition() : Vector2f{
        return _position
    }
    fun getScale() : Vector2f{
        return _scale
    }

}

