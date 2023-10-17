package ui

import androidx.compose.runtime.mutableStateOf
import model.DataGameRules

class GameRules(fromDeadToLife : Set<UInt> = setOf(3u), forAlive : Set<UInt> = setOf(2u, 3u)) {
    private val fromDeadToLifeState = mutableStateOf(fromDeadToLife)
    private val forAliveState = mutableStateOf(forAlive)

    val dataGameRules = DataGameRules(fromDeadToLife, forAlive)
    fun fromDeadToLife() = fromDeadToLifeState.value

    fun forAlive() = forAliveState.value

    fun changeFromDeadToLife(newValue: Set<UInt>) {
        fromDeadToLifeState.value = newValue
        dataGameRules.fromDeadToLife = newValue
    }

    fun changeForAlive(newValue: Set<UInt>) {
        forAliveState.value = newValue
        dataGameRules.forAlive = newValue
    }

    fun getFromDataGameRules(newDataGameRules: DataGameRules) {
        changeFromDeadToLife(newDataGameRules.fromDeadToLife)
        changeForAlive(newDataGameRules.forAlive)
    }
}
