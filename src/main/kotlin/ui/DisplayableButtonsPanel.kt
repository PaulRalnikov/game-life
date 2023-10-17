package ui
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.darkrockstudios.libraries.mpfilepicker.FilePicker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DisplayableButtonsPanel(
    private var field: Field
) : IDisplayable {

    companion object {
        private val DEFAULT_BUTTON_PADDING = 5.dp
        private const val DELAY_ON_TURNS_RUNNING = 200L
    }

    @Composable
    private fun defaultButton(text: String, onClick : () -> Unit) = Button(
        modifier = Modifier.padding(DEFAULT_BUTTON_PADDING),
        onClick = { onClick() }
    ) {
        Text(text,
            textAlign = TextAlign.Center)
    }

    private enum class SaveResult(val result : String) {
        NoFileSelected("Файл не выбран!"),
        CanNotSave("Ошибка при записи в файл!"),
        OtherError("Произошла ошибка!"),
        OK("Игра успешно сохранена!")
    }
    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun displaySaveToFileButton() {
        var showFilePicker by remember { mutableStateOf ( false ) }
        var showAlertDialog by remember { mutableStateOf(false) }
        defaultButton("Сохранить игру в файл") {
            showFilePicker = true
        }

        var path : String? by remember {mutableStateOf(null)}

        var saveResult by remember {mutableStateOf(SaveResult.OtherError)}

        FilePicker(showFilePicker) { pickedPath ->
            showFilePicker = false
            showAlertDialog = true
            path = pickedPath?.path
            if (path != null) {
                saveResult = SaveResult.OK
                try {
                    field.saveToFile(path!!)
                }
                catch (exception : Exception) {
                    exception.printStackTrace()
                    saveResult = SaveResult.CanNotSave
                }
            } else {
                saveResult = SaveResult.NoFileSelected
            }
        }

        if (showAlertDialog) {
            AlertDialog(
                onDismissRequest = {
                    showAlertDialog =false
                    showFilePicker =false
                                   },
                buttons = {},
                text = {Text(saveResult.result)}
            )
        }
    }

    private enum class LoadResult(val result : String) {
        NoFileSelected("Файл не выбран!"),
        CanNotParse("Ошибка при чтении файла!"),
        OtherError("Произошла ошибка!"),
        OK("Игра успешно загружена!"),
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun displayLoadFromFileButton() {
        var showFilePicker by remember {mutableStateOf ( false )}
        defaultButton("Загрузить игру из файла") {
            showFilePicker = true
        }

        var showAlertDialog by remember { mutableStateOf(false) }

        var path : String? by remember { mutableStateOf(null) }

        var loadResult by remember {mutableStateOf(LoadResult.OtherError)}

        FilePicker(showFilePicker) { pickedPath ->
            showFilePicker = false
            showAlertDialog = true
            path = pickedPath?.path
            if (path != null) {
                loadResult = LoadResult.OK
                try {
                    field.loadFromFile(path!!)
                }
                catch (exception : Exception) {
                    println(exception.message)
                    loadResult = LoadResult.CanNotParse
                }
            } else {
                loadResult = LoadResult.NoFileSelected
            }
        }

        if (showAlertDialog) {
            AlertDialog(
                onDismissRequest = { showAlertDialog = false },
                buttons = {},
                text = {Text(loadResult.result)}
            )
        }
    }

    inner class TurnsRunningButtons(private val displayStopRunningAlertDialogState : MutableState<Boolean>,
                                    startRunning : Boolean = false) {

        private var running by mutableStateOf(startRunning)

        private var coroutineScope : CoroutineScope? = null

        private fun runTurns(atEndOfWhile : () -> Unit = {}) {
            coroutineScope!!.launch {
                while (running) {
                    delay(DELAY_ON_TURNS_RUNNING)
                    if (!field.makeTurn()) {
                        displayStopRunningAlertDialogState.value = true
                        running = false
                    }
                    atEndOfWhile()
                }
            }
        }
        @Composable
        fun displayRunningButton() {
            if (coroutineScope == null) coroutineScope = rememberCoroutineScope()
            defaultButton("Запустить автоматическое выполнение ходов") {
                if (!running) {
                    running = true
                    runTurns()
                }
            }
        }

        @Composable
        fun displayStopRunningButton() = defaultButton("Остановить выполнение ходов") { running = false }

        @OptIn(ExperimentalMaterialApi::class)
        @Composable
        fun displayRunGivenCountGenerations() {
            var showAlertDialog by remember { mutableStateOf(false)}
            var iterationCount = 0u
            if (coroutineScope == null) coroutineScope = rememberCoroutineScope()
            defaultButton("Запустить фиксированное число итераций") {
                showAlertDialog = true
            }
            if (showAlertDialog) {
                val displayableUintField = remember (iterationCount) {
                    DisplayableUintField {
                        Text("Число ходов:") }
                }

                val isCorrect = remember(displayableUintField.isCorrect()) {
                    mutableStateOf(displayableUintField.isCorrect())
                }

                fun apply() {
                    if (isCorrect.value) {
                        iterationCount = displayableUintField.value()
                        println(iterationCount)
                        running = (iterationCount > 0u)
                        runTurns {
                            iterationCount--
                            if (iterationCount == 0u) running = false
                        }
                    }
                }

                AlertDialog(
                    onDismissRequest = {showAlertDialog = false},
                    buttons = {
                        Column{
                            displayableUintField.display()
                            Button(onClick = {
                                apply()
                                showAlertDialog = false
                            }) {
                                Text("Запустить",
                                    color = ( if (isCorrect.value) Color.Green else Color.Red)
                                )
                            }
                        }
                    }
                )
            }
        }

        @OptIn(ExperimentalMaterialApi::class)
        @Composable
        fun displayStopRunningAlertDialog() {
            AlertDialog(
                onDismissRequest = { displayStopRunningAlertDialogState.value = false },
                buttons = {},
                text = {Text("Автоматическое проигрывание ходов остановлено: позиция повторилась")}
            )
        }
    }

    @Composable
    override fun display() {
        val displayStopRunningAlertDialogState = remember {mutableStateOf(false)}

        val turnsRunningButtons = TurnsRunningButtons(displayStopRunningAlertDialogState)

        LazyRow {
            item {
                defaultButton("Следующее поколение") {
                    field.makeTurn()
                }
            }

            item {
                defaultButton("Сбросить цвета") {
                    field.reset()
                }
            }

            item {
                defaultButton("Заполнить клетки случайно") {
                    field.fillRandomColors()
                }
            }

            item {
                turnsRunningButtons.displayRunningButton()
            }

            item {
                turnsRunningButtons.displayRunGivenCountGenerations()
            }

            item {
                turnsRunningButtons.displayStopRunningButton()
            }

            item {
                displaySaveToFileButton()
            }

            item {
                displayLoadFromFileButton()
            }
        }

        if (displayStopRunningAlertDialogState.value) {
            turnsRunningButtons.displayStopRunningAlertDialog()
        }
    }
}