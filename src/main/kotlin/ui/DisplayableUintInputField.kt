package ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Icon
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color

class DisplayableUintField (defaultValue: UInt = 0u,
                            private val title: @Composable () -> Unit = {}
) : IDisplayable {
    private val input = mutableStateOf("$defaultValue")
    private val isCorrectState = mutableStateOf(true)
    private val valueState = mutableStateOf(defaultValue)

    companion object Constants {
        @Composable
        private fun CorrectStateIcon() = Icon(
            Icons.Filled.Check,
            contentDescription = "OK",
            tint = Color(0xff33ffcc)
        )

        @Composable
        private fun IncorrectStateIcon() = Icon(
            Icons.Filled.Warning,
            contentDescription = "Не является числом от 0!",
            tint = Color.Red
        )

    }
    private fun process(newInput: String) {
        input.value = newInput
        println("new input: ${input.value}")
        val processed : UInt? = input.value.toUIntOrNull()
        if (processed != null) {
            isCorrectState.value = true
            valueState.value = processed
        } else {
            isCorrectState.value = false
        }
    }

    fun isCorrect() = isCorrectState.value

    fun value() = valueState.value

    @Composable
    override fun display() {
        Column {
            title()
            TextField(value = input.value,
                onValueChange = {process(it)},
                trailingIcon = {
                    if (isCorrectState.value)
                        CorrectStateIcon()
                    else
                        IncorrectStateIcon()
                }
            )
        }

    }
}