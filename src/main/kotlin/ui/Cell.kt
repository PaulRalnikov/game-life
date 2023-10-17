package ui
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import model.DataCell
import model.MyColor

class Cell(color: MyColor = MyColor.DeadColor, countAlive : Int = 0) {

    private val myColorState: MutableState<MyColor> = mutableStateOf(color)
    private val countAliveState : MutableState<Int> = mutableStateOf(countAlive)
    val dataCell = DataCell(color, countAlive)

    override fun toString() : String = myColorState.value.toString() + " ${countAliveState.value}"

    fun myColor() : MyColor = myColorState.value

    fun color() : Color = myColor().color

    fun countAlive() = countAliveState.value

    fun changeMyColor(newColor : MyColor) {
        myColorState.value = newColor
        dataCell.myColor = newColor
    }

    fun changeCountAlive(newCountAlive: Int) {
        countAliveState.value = newCountAlive
        dataCell.countAlive = newCountAlive
    }

    fun resetAliveCount() {
        countAliveState.value = if(myColor() == MyColor.DeadColor) 0 else 1
        dataCell.countAlive = countAlive()
    }

    fun changeColorAndResetAliveCount(newColor: MyColor) {
        changeMyColor(newColor)
        resetAliveCount()
    }

    fun getAliveCount() = countAliveState.value

    fun setNextColor() = changeColorAndResetAliveCount(myColor().getNextColor())

    fun getFromDataCell(newDataCell: DataCell) {
        changeMyColor(newDataCell.myColor)
        changeCountAlive(newDataCell.countAlive)
    }
}
