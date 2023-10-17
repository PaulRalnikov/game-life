package ui
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

class DisplayableCell(private val cell : Cell = Cell(), private val cellSize : Dp) : IDisplayable {

    companion object Constants {
        private val BORDER_WIDTH = 0.5.dp
        private val CELL_SIZE: Dp = 20.dp
        private val BORDER_COLOR = Color.DarkGray
    }
    @Composable
    override fun display() = Box(
        Modifier.size(cellSize)
        .background(cell.color())
        .clickable { cell.setNextColor() }
        .border(BORDER_WIDTH, BORDER_COLOR)
    )
}