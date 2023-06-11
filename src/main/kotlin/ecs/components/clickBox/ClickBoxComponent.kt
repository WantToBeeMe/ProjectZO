package ecs.components.clickBox

import ecs.components.TransformComponent
import org.joml.Vector2f
import org.lwjgl.opengl.GL45.*
import kotlin.math.cos
import kotlin.math.sin

class ClickBoxComponent {
    private val clickBoxes : MutableList<IClickBox> = mutableListOf()
    var hold = false

    private var vertices : FloatArray = FloatArray(0)
    private var lines : IntArray = IntArray(0)
    private var vaoID : Int = 0
    private var vboID : Int = 0
    private var eboID : Int = 0
    private var created = false

    fun addClickBox(box : IClickBox) : ClickBoxComponent{
        clickBoxes.add(box)
        return this
    }
    fun removeClickBox(box : IClickBox) : Boolean{
        return clickBoxes.remove(box)
    }

    var onEnterEvent: ((Vector2f, Vector2f) -> Unit)? = null; private set
    var onLeaveEvent: ((Vector2f, Vector2f) -> Unit)? = null; private set
    var onClickEvent: ((Vector2f, Vector2f, Int) -> Unit)? = null; private set
    var onReleaseEvent: ((Vector2f, Vector2f, Int) -> Unit)? = null; private set
    var whileHoverEvent: ((Vector2f, Vector2f) -> Unit)? = null; private set
    var whileClickEvent: ((Vector2f, Vector2f) -> Unit)? = null; private set
    fun setOnEnter(event :  ((Vector2f, Vector2f) -> Unit)? ) : ClickBoxComponent{
        onEnterEvent = event
        return this
    }
    fun setOnLeave(event :  ((Vector2f, Vector2f) -> Unit)?): ClickBoxComponent{
        onLeaveEvent = event
        return this
    }
    fun setOnClick(event :  ((Vector2f, Vector2f, Int) -> Unit)?) : ClickBoxComponent{
        onClickEvent = event
        return this
    }
    fun setOnRelease(event :  ((Vector2f, Vector2f, Int) -> Unit)?) : ClickBoxComponent{
        onReleaseEvent = event
        return this
    }
    fun setWhileClick(event :  ((Vector2f, Vector2f) -> Unit)?) :  ClickBoxComponent{
        whileClickEvent = event
        return this
    }
    fun setWhileHover(event :  ((Vector2f, Vector2f) -> Unit)?):  ClickBoxComponent{
        whileHoverEvent = event
        return this
    }

    fun isInside(point: Vector2f, transform: TransformComponent?): Boolean {
        val realPoint = transformPoint(point, transform)
        for(box in clickBoxes)
            if( box.isInside(realPoint) )
                return true
        return false
    }

    var showDebugLines = false
    fun renderClickBox(){
        if(!created) createRenderData()
        glBindBuffer(GL_ARRAY_BUFFER,vboID)
        //glBufferSubData(GL_ARRAY_BUFFER, 0, vertices)

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER,eboID)
        //glBufferSubData(GL_ELEMENT_ARRAY_BUFFER, 0, triangles)

        glBindVertexArray(vaoID)

        glEnableVertexAttribArray(0)
        glDrawElements(GL_LINES,lines.size, GL_UNSIGNED_INT, 0)
        //disable them again, because they are drawn
        glDisableVertexAttribArray(0)

        glBindVertexArray(0)
        glBindBuffer(GL_ARRAY_BUFFER, 0)
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0)
    }
    private fun createRenderData(){
        if(created) return
        created = true

        for(box in clickBoxes){
            val boxOutline = box.getBoxOutline()
            val newLines = boxOutline.second.mapIndexed { index, num ->
                num + (vertices.size / 2)
            }.toIntArray()
            vertices += boxOutline.first
            lines += newLines
        }

        //generate and bind a vertex array object
        vaoID = glGenVertexArrays()
        glBindVertexArray(vaoID)

        //allocate space for the vertices
        vboID = glGenBuffers()
        glBindBuffer(GL_ARRAY_BUFFER, vboID)
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW)

        //create and upload indices buffer
        eboID = glGenBuffers()
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID)
        glBufferData(GL_ELEMENT_ARRAY_BUFFER,lines, GL_STATIC_DRAW)

        val posSize= 2
        val vertexSizeFloats= (posSize)*Float.SIZE_BYTES // (posSize + colorSize)*Float.SIZE_BYTES
        glVertexAttribPointer(0, posSize, GL_FLOAT, false, vertexSizeFloats, 0.toLong())

        //done creating, reset everything just to be clean
        glBindVertexArray( 0)
        glBindBuffer(GL_ARRAY_BUFFER, 0)
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0)
    }

    private fun transformPoint(point: Vector2f, transform: TransformComponent? ): Vector2f {
        if(transform == null) return point
        //translation
        var newPoint = Vector2f(point).add(Vector2f(transform.getPosition()).mul(-1f))

        //rotate
        val angleRad = transform.getRotation(true)
        val cos = cos(angleRad.toDouble()).toFloat()
        val sin = sin(angleRad.toDouble()).toFloat()
        newPoint = Vector2f(newPoint.x * cos - newPoint.y * sin,  newPoint.x * sin + newPoint.y * cos)
        //scale
        return newPoint.mul(Vector2f(1f).div(transform.getScale()))
    }

}