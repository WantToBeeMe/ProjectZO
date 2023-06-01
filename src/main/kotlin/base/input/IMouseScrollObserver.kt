package base.input

interface IMouseScrollObserver {
    fun onMouseScroll(xPos : Double, yPos: Double, xScroll: Double, yScroll : Double)
}