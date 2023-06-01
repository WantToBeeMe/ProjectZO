package base.input

interface IKeyboardObserver {
    fun onKeyPress(key: Int);
    fun onKeyRelease(key: Int);
}