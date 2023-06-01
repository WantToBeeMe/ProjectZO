package base.input

interface IMouseClickObserver {
    fun onMouseClick(xPos : Double, yPos: Double, button : Int)
    fun onMouseRelease(xPos : Double, yPos: Double, button : Int)
}