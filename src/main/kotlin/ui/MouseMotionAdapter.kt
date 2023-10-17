package ui
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset

class MouseMotionAdapter {
    private val offsetPointerState = mutableStateOf(Offset(0f, 0f))

    private val displayCellExtraInfo = mutableStateOf(false)

    fun move(offset: Offset) {
        offsetPointerState.value = offset
        displayCellExtraInfo.value = true
    }

    fun exit() {
        displayCellExtraInfo.value = false
    }

    fun offsetPointer() = offsetPointerState.value

    fun displayCellInfo() = displayCellExtraInfo.value
}