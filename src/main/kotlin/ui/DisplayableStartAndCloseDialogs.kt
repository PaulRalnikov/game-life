package ui

import androidx.compose.foundation.layout.Row
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class DisplayableOnCloseDialog(private val field: Field,
                               private val exitApplication : () -> Unit,
                               startDisplayState : Boolean = false) : IDisplayable
{
    private var displayDialog by mutableStateOf(startDisplayState)

    fun makeDisplayable() {
        displayDialog = true
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    override fun display() {
        if (displayDialog) {
            AlertDialog(
                onDismissRequest = {},
                text = { Text("Сохранить игру?") },
                buttons = {
                    Row {
                        Button(
                            onClick = {
                                displayDialog = false
                                saveFieldToDefaultFile(field)
                                exitApplication()
                            }
                        ) {
                            Text("Да")
                        }
                        Button(
                            onClick = {
                                displayDialog = false
                                exitApplication()
                            }
                        ) {
                            Text("Нет")
                        }
                    }
                }
            )
        }
    }
}

class DisplayableOnStartDialog(private val field: Field,
                               private val loadFieldFromFile : (Field) -> Unit, startDisplayState: Boolean = false) : IDisplayable
{
    private var displayDialog by mutableStateOf(startDisplayState)

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    override fun display() {
        if(displayDialog) {
            AlertDialog(
                onDismissRequest = {},
                text = {Text("Загрузить последнюю сохраненную игру?")},
                buttons = {
                    Row {
                        Button(
                            onClick = {
                                displayDialog = false
                                loadFieldFromFile(field)
                            }
                        ) {
                            Text("Да")
                        }
                        Button(
                            onClick = {
                                displayDialog = false
                            }
                        ) {
                            Text("Нет")
                        }
                    }
                }
            )
        }
    }
}