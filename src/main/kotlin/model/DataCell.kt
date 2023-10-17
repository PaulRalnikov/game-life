package model

import kotlinx.serialization.Serializable

@Serializable
data class DataCell(var myColor: MyColor = MyColor.DeadColor, var countAlive : Int = 0): IHashable {
    fun resetAliveCount() {
        countAlive = if(myColor == MyColor.DeadColor) 0 else 1
    }

    fun changeColorAndUpdateAliveCount(newColor: MyColor) {
        myColor = newColor
        if (newColor == MyColor.DeadColor) {
            countAlive = 0
        }
        else {
            countAlive++
        }
    }

    fun changeColorAndResetAliveCount(newColor: MyColor) {
        myColor = newColor
        resetAliveCount()
    }

    fun getFromDataCell(dataCell: DataCell) {
        myColor = dataCell.myColor
        countAlive = dataCell.countAlive
    }

    override fun addToHash(hasher: Hasher) {
        hasher.addElement(this.myColor.ordinal + 1)
    }
}