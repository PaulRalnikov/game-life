package ui

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState

fun main() {

    val field = Field()

    application {
        val displayableOnCloseDialog = DisplayableOnCloseDialog(
            field, {
                clearCache()
                exitApplication()
            }
        )

        val displayableOnStartDialog = DisplayableOnStartDialog(
            field,
            ::loadFieldFromDefaultPath,
            checkDefaultSaveFileExists()
        )

        Window(
            onCloseRequest = {
                displayableOnCloseDialog.makeDisplayable()
            },
            title = "Game life",
            state = rememberWindowState(width = 1000.dp, height = 1000.dp)
        ) {

            val displayableField = remember { DisplayableField(field) }

            MaterialTheme {
                displayableOnStartDialog.display()
                displayableField.display()
                displayableOnCloseDialog.display()
            }
        }
    }
}