package model

import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ui.cacheDataField
import java.io.File
import java.io.FileWriter
import java.io.PrintWriter

typealias CoordsList = List<Pair<Int, Int>>
typealias MutableCoordsList = MutableList<Pair<Int, Int>>

@Serializable
class DataField(private val field : List<List<DataCell>>,
                     val dataGameRules: DataGameRules) : IHashable {
    val height = field.size
    val width = (if (height == 0) 0 else field[0].size)

    private val newField: List<List<DataCell>> = List(height) {
        List(width) {
            DataCell()
        }
    }

    fun saveToFile(path: String) {
        PrintWriter(FileWriter(path)).use {
            it.write(Json.encodeToString(this))
        }
    }

    fun get(i: Int, j: Int): DataCell = field[i][j]

    fun getCellCountAlive(i: Int, j: Int): Int = get(i, j).countAlive

    fun getCellMyColor(i: Int, j: Int): MyColor = get(i, j).myColor

    fun reset(): Pair<CoordsList, CoordsList> = Pair(
        field.mapIndexed { i, row ->
            row.mapIndexedNotNull { j, cell ->
                val add: Boolean = cell.countAlive != 0 || cell.myColor != MyColor.DeadColor
                cell.countAlive = 0
                cell.myColor = MyColor.DeadColor
                if (add) Pair(i, j) else null
            }.toList()
        }.toList().flatten(),

        listOf()
    )

    private fun updateCell(i: Int, j: Int) {
        field[i][j].getFromDataCell(newField[i][j])
    }

    private fun updateCellCountAlive(i: Int, j: Int) {
        get(i, j).countAlive = newField[i][j].countAlive
    }

    private fun getNextGenColor(i: Int, j: Int): MyColor {
        val count = mutableMapOf<MyColor, UInt>()
        for (dx in -1..1) {
            for (dy in -1..1) {
                if (dx == 0 && dy == 0) continue
                val i1 = (i + dx + height) % height
                val j1 = (j + dy + width) % width
                val color = getCellMyColor(i1, j1)
                count[color] = count.getOrDefault(color, 0u) + 1u
            }
        }
        val cellColor = getCellMyColor(i, j)
        if (cellColor == MyColor.DeadColor) {
            val possibleColors =
                count.entries.mapNotNull { if (it.value in dataGameRules.fromDeadToLife) it.key else null }
            if (possibleColors.size == 1) return possibleColors[0]
            return cellColor
        } else {
            val cnt = count.getOrDefault(cellColor, 0u)
            if (cnt in dataGameRules.forAlive) {
                return cellColor
            }
            return MyColor.DeadColor
        }
    }

    //    returns two lists of coords: first list is about cells changed color,
    //    second list is about cells changed countAlive
    private fun changeField(changeCell: (DataCell, Int, Int) -> Unit): Pair<CoordsList, CoordsList> {
        val changedColor: MutableCoordsList = mutableListOf()
        val changedCountAlive: MutableCoordsList = mutableListOf()
        var oldCell: DataCell
        runBlocking {
            newField.forEachIndexed { i, row ->
                async {
                    row.forEachIndexed { j, cell ->
                        changeCell(cell, i, j)
                        oldCell = get(i, j)
                        val coords = Pair(i, j)
                        if (cell != oldCell) {
                            if (cell.myColor == oldCell.myColor)
                                changedCountAlive.add(coords)
                            else
                                changedColor.add(coords)
                        }
                    }
                }.join()
            }
            async {
                changedColor.forEach { updateCell(it.first, it.second) }
            }.join()
            async {
                changedCountAlive.forEach { updateCellCountAlive(it.first, it.second) }
            }.join()
        }
        return Pair(changedColor, changedCountAlive)
    }

    fun makeTurn(cache : Boolean = true): Pair<CoordsList, CoordsList> {
        if (cache) cacheDataField(this)
        val result = changeField { cell, i, j ->
            cell.countAlive = getCellCountAlive(i, j)
            cell.changeColorAndUpdateAliveCount(getNextGenColor(i, j))
        }
        return result
    }

    fun fillRandomColors(): Pair<CoordsList, CoordsList> = changeField { cell, _, _ ->
        cell.changeColorAndResetAliveCount(randomColor())
    }

    fun getFromDataField(dataField: DataField) {
        field.forEachIndexed { i, row ->
            row.forEachIndexed { j, cell ->
                cell.getFromDataCell(dataField.get(i, j))
            }
        }
        dataGameRules.getFromDataGameRules(dataField.dataGameRules)
    }

    override fun addToHash(hasher: Hasher) {
        field.flatten().forEach {it.addToHash(hasher)}
        dataGameRules.addToHash(hasher)
    }
}

fun loadDataFieldFromFile(path : String) : DataField = Json.decodeFromString(
    File(path).bufferedReader().readText()
)

fun getDataFieldHash(dataField: DataField) : Int {
    val hasher = Hasher()
    dataField.addToHash(hasher)
    return hasher.hash
}
