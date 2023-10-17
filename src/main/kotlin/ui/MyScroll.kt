package ui
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import kotlin.math.max
import kotlin.math.min

fun max(first: Offset, second: Offset) = Offset(max(first.x, second.x), max(first.y, second.y))
fun min(first: Offset, second: Offset) = Offset(min(first.x, second.x), min(first.y, second.y))
class MyScroll (private val leftTopState: State<Offset>,
                private val rightBottomState: State<Offset>,
                startOffset: Offset = Offset.Zero) {
    private val multiplier = 10f
    private val offsetState =  mutableStateOf(startOffset)

    private fun normalize() {
        offsetState.value = max(offsetState.value, leftTopState.value)
        offsetState.value = min(offsetState.value, rightBottomState.value)
    }

    operator fun plusAssign(delta: Offset) {
        offsetState.value += delta * multiplier
    }

    fun valueOffset() : Offset {
        normalize()
        return offsetState.value
    }
}