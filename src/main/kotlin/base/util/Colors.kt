package base.util

import org.joml.Vector4f

enum class Colors(r:Float, g:Float, b:Float, a:Float ) {
    GRAY_SUPER_DARK(19f/255f, 21f/255f,25f/255f,1f),
    GRAY_DARK(23f/255f, 26f/255f,31f/255f,1f),
    GRAY_NORMAL(30f/255f, 34f/255f,40f/255f,1f),
    GRAY_LIGHT(47f/255f, 53f/255f,61f/255f,1f),

    BLUE(109f/255f, 103f/255f, 228f/255f, 1f),
    CYAN(70f/255f, 194f/255f, 203f/255f, 1f),
    YELLOW(242f/255f, 247f/255f, 161f/255f, 1f),
    ;

    private val color : Vector4f
    val get get() = color
    init{
        color = Vector4f(r,g,b,a)
    }
}