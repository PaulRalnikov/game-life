package ui
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class DisplayableGameRules(private val gameRules: GameRules) : IDisplayable {
    companion object Constants {
        private val HEADER_FONT_SIZE = 25.sp
    }

    @Composable
    override fun display() {
        val forAlive : Set<UInt> = gameRules.forAlive()
        val fromDeadToLife : Set<UInt> = gameRules.fromDeadToLife()

        val forAliveSelectableRange = remember(forAlive) {
            DisplayableSelectableRange(selectedOnStartIndexes = forAlive) {
                Text("Количество клеток из пункта 1:")
            }
        }

        val fromDeadToLifeSelectableRange = remember(fromDeadToLife) {
            DisplayableSelectableRange(selectedOnStartIndexes = fromDeadToLife) {
                Text("Количество клеток из пункта 2:")
            }
        }

        val fieldList : List<DisplayableSelectableRange> = listOf(
            forAliveSelectableRange,
            fromDeadToLifeSelectableRange
        )

        fun applyAll() {
            gameRules.changeFromDeadToLife(fromDeadToLifeSelectableRange.selectedIndexes())
            gameRules.changeForAlive(forAliveSelectableRange.selectedIndexes())
        }

        LazyColumn {
            item {Text("Правила игры:\n", fontSize = HEADER_FONT_SIZE)}
            item {
                Text("На поле есть мертвые и живые клетки.\n" +

                        "Мертвые представлены черным цветом, живые - всеми остальными.\n" +

                        "Генерация нового поколения происходит по следующим правилам:\n" +

                        "1) Если у живой клетки " +
                        forAlive.toList().sorted().joinToString(separator = ", ") { it.toString() } +
                        " соседей ее цвета, то она не меняет свое состояние, иначе умирает.\n" +

                        "2) Если у мертвой клетки " +
                        fromDeadToLife.toList().sorted().joinToString(separator = ", ") { it.toString() } +
                        " живых соседей цвета [color], то в ней может появиться цвет [color].\n" +

                        "3) Если в мертвой клетке может появиться ровно один цвет, то он в ней появляется, " +
                        "иначе она остается мертвой.\n" +

                        "Если игрок вручную изменяет цвет какой-либо клетки, что считается, что она " +
                        "ожила или умерла на данном ходу."
                )
            }

            item {Text("Изменить правила игры:\n", fontSize = HEADER_FONT_SIZE) }

            fieldList.forEach { item { it.display() }}

            item {
                Button(onClick = {applyAll()})
                {
                    Text("Применить изменения")
                }
            }
        }
    }
}

class DisplayableSelectableRange (
    private val range: UIntRange = 0u .. 8u,
    selectedOnStartIndexes : Set<UInt>,
    private val title : @Composable () -> Unit
) : IDisplayable {
    private val selectedIndexesState : MutableState<Set<UInt>> = mutableStateOf(selectedOnStartIndexes)

    companion object {
        private val ELEMENT_BORDER_WIDTH : Dp = 0.5.dp
        private val ELEMENT_BORDER_COLOR : Color = Color.Black
        private val ELEMENT_SIZE = 20.dp

        @Composable
        private fun CheckIcon() = Icon(
            Icons.Filled.Check,
            contentDescription = "OK",
            tint = Color(0xff33ffcc)
        )
    }

    fun selectedIndexes() = selectedIndexesState.value

    private fun addIndex(i : UInt) {
        selectedIndexesState.value = selectedIndexes() union setOf(i)
    }

    private fun deleteIndex(i : UInt) {
        selectedIndexesState.value = selectedIndexes() subtract setOf(i)
    }

    @Composable
    override fun display() {
        Column {
            title()
            Row {
                for (i in range)
                    Column {
                        Text("$i",
                            modifier = Modifier
                                .border(ELEMENT_BORDER_WIDTH, ELEMENT_BORDER_COLOR)
                                .size(ELEMENT_SIZE),

                            textAlign = TextAlign.Center
                        )
                        val selected = selectedIndexes().contains(i)
                        Box(modifier = Modifier
                            .clickable {
                                if (selected) deleteIndex(i)
                                else addIndex(i)
                            }
                            .size(ELEMENT_SIZE)
                            .border(ELEMENT_BORDER_WIDTH, ELEMENT_BORDER_COLOR)
                        ) {
                            if (selected) CheckIcon()
                        }
                    }
            }
        }
    }
}