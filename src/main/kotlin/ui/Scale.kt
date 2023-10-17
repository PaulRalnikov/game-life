package ui

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.min

class Scale(startValue: Dp = 20.dp, private val minimum: Dp = 10.dp, private val maximum: Dp = 50.dp) {

    private val valueState = mutableStateOf(startValue)
    operator fun plusAssign(delta: Offset) {
        valueState.value -= Dp(delta.y)
        valueState.value = max(valueState.value, minimum)
        valueState.value = min(valueState.value, maximum)
    }

    fun valueDp() : Dp = valueState.value

    fun valueFloat() : Float = valueDp().value
}