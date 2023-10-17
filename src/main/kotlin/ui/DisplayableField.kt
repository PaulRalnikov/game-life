package ui
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.isCtrlPressed
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import kotlin.math.ceil
import kotlin.math.roundToInt

class DisplayableField (private val field: Field = Field()) : IDisplayable {

    companion object Constants {
//      field constants
        private val FIELD_SIZE = 600.dp
        private val FIELD_BORDER_WIDTH = 2.dp
        private val FIELD_BORDER_COLOR = Color.Black
        private val MAIN_FIELD_HORIZONTAL_PADDING = 10.dp

//      rules constants
        private val RULES_HORIZONTAL_PADDING = 10.dp

//      cell extra info constants
        private val EXTRA_CELL_INFO_BACKGROUND = Color.White
        private val EXTRA_CELL_INFO_BORDER_WIDTH = 0.5.dp
        private val EXTRA_CELL_INFO_BORDER_COLOR = Color.Black

    }

    private val displayableGameRules = DisplayableGameRules(field.gameRules)
    private val cellSize = Scale()
    private val cellsCount : Int  by derivedStateOf {
        ceil(FIELD_SIZE.value / cellSize.valueFloat()).roundToInt()
    }
    private val scrollOffset = MyScroll(
        derivedStateOf {Offset.Zero},
        derivedStateOf {
            Offset(
                (field.height - cellsCount - 1).toFloat(),
                (field.width - cellsCount - 1).toFloat()
            )
        }
    )

    private val mouseMotionAdapter = MouseMotionAdapter()
    private val displayableButtonsPanel = DisplayableButtonsPanel(field)
    @Composable
    override fun display() = Box {
        val leftY = remember(scrollOffset.valueOffset()) {
            mutableStateOf(scrollOffset.valueOffset().x.roundToInt())
        }

        val leftX = remember(scrollOffset.valueOffset()) {
            mutableStateOf(scrollOffset.valueOffset().y.roundToInt())
        }

        val rightX = remember(leftX.value, cellsCount) {
            mutableStateOf(leftX.value + cellsCount)
        }

        val rightY = remember(leftY.value, cellsCount) {
            mutableStateOf(leftY.value + cellsCount)
        }

        Column {
            displayableButtonsPanel.display()
            Row{
                Box(Modifier
                    .zIndex(1f)
                    .padding(horizontal = MAIN_FIELD_HORIZONTAL_PADDING)
                ) {
                    displayMainFieldInfo(leftX, rightX, leftY, rightY)
                    displayExtraCellInfo(leftX, leftY)
                }
                Box (modifier = Modifier.padding(RULES_HORIZONTAL_PADDING)) {
                    displayableGameRules.display()
                }
            }
        }
    }

    @OptIn(ExperimentalComposeUiApi::class)
    @Composable
    private fun displayMainFieldInfo(leftX: MutableState<Int>, rightX: MutableState<Int>,
                             leftY: MutableState<Int>, rightY: MutableState<Int>) {
        Column (
            Modifier
                .border(FIELD_BORDER_WIDTH, FIELD_BORDER_COLOR)
                .size(FIELD_SIZE)
                .onPointerEvent(PointerEventType.Scroll) { pointerEvent ->
                    if (!pointerEvent.keyboardModifiers.isCtrlPressed) pointerEvent.changes.forEach {
                        scrollOffset += it.scrollDelta / cellSize.valueFloat()
                    }
                    else {
                        pointerEvent.changes.forEach { cellSize += it.scrollDelta }
                    }
                }
                .myHoverable(
                    { mouseMotionAdapter.move(it) },
                    { mouseMotionAdapter.exit() }
                )
        ) {
            for (i in leftX.value until rightX.value) {
                Row {
                    for (j in leftY.value until rightY.value) {
                        DisplayableCell(field.get(i, j), cellSize.valueDp()).display()
                    }
                }
            }
        }
    }

    @Composable
    private fun displayExtraCellInfo(leftX: MutableState<Int>, leftY: MutableState<Int>) {
        val offsetPointer: Offset = mouseMotionAdapter.offsetPointer() / LocalDensity.current.density
        val displayCellExtraInfo : Boolean = mouseMotionAdapter.displayCellInfo()
        val coords: Pair<Int, Int> = Pair(
            (offsetPointer.y / cellSize.valueFloat()).toInt() + leftX.value,
            (offsetPointer.x / cellSize.valueFloat()).toInt() + leftY.value
        )
        val aliveCount = field.getCellAliveCount(coords.first, coords.second)
        if (displayCellExtraInfo && aliveCount != 0) {
            Box(
                Modifier
                    .offset(offsetPointer.x.dp, offsetPointer.y.dp)
                    .background(EXTRA_CELL_INFO_BACKGROUND)
                    .border(EXTRA_CELL_INFO_BORDER_WIDTH, EXTRA_CELL_INFO_BORDER_COLOR)
            ) {
                Text("  клетка (${coords.first + 1}, ${coords.second + 1}) жива $aliveCount поколений подряд")
            }
        }
    }
}