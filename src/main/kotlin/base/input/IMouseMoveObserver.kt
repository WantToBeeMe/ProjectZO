package base.input

interface IMouseMoveObserver {
    fun onMouseMove(xPos : Double, yPos: Double, dx : Double, dy : Double)
}