package ui
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import model.CoordsList
import model.DataField
import model.loadDataFieldFromFile

class Field(startField : List<List<Cell>>,
            val gameRules: GameRules = GameRules()) {
    private var field by mutableStateOf (startField)
    val height = field.size
    val width = (if (height == 0) 0 else field[0].size)

    private val dataField : DataField = DataField(
        field.map { row ->
            row.map {
                it.dataCell
            }
        },
        gameRules.dataGameRules
    )

    companion object {
        private const val DEFAULT_HEIGHT = 1024
        private const val DEFAULT_WIDTH = 1024
    }
    constructor(height : Int = DEFAULT_HEIGHT, width : Int = DEFAULT_WIDTH) : this(
        List(height) {
            List(width) {
                Cell()
            }
        }
    )

    fun get(i : Int, j : Int) : Cell = field[i][j]

    fun getCellAliveCount(i : Int, j : Int) : Int = get(i, j).getAliveCount()

    private fun updateField(changedCells :Pair<CoordsList, CoordsList>) : Boolean {
        val changedColor : CoordsList = changedCells.first
        val changedCountALive : CoordsList = changedCells.second
        changedColor.forEach {
            val i = it.first
            val j = it.second
            field[i][j].getFromDataCell(dataField.get(i, j))
        }
        changedCountALive.forEach {
            val i = it.first
            val j = it.second
            field[i][j].changeCountAlive(dataField.get(i, j).countAlive)
        }
        return changedColor.isNotEmpty()
    }

    fun makeTurn() : Boolean {
        if (checkDataFieldWasBefore(dataField)) return false
        return updateField(dataField.makeTurn())
    }

    fun reset() : Boolean = updateField(dataField.reset())

    fun fillRandomColors() : Boolean = updateField(dataField.fillRandomColors())

    fun saveToFile(path : String) = dataField.saveToFile(path)

    fun getFromDataField(newDataField: DataField) {
        dataField.getFromDataField(newDataField)
        field.forEachIndexed{ i, row ->
            row.forEachIndexed { j, cell ->
                cell.getFromDataCell(dataField.get(i, j))
            }
        }
        gameRules.getFromDataGameRules(dataField.dataGameRules)
    }

    fun loadFromFile(path : String) = getFromDataField(loadDataFieldFromFile(path))
}