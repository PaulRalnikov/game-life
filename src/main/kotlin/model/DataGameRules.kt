package model

import kotlinx.serialization.Serializable

@Serializable
data class DataGameRules(var fromDeadToLife : Set<UInt> = setOf(3u),
                         var forAlive : Set<UInt> = setOf(2u, 3u)
) : IHashable {
    fun getFromDataGameRules(dataGameRules: DataGameRules) {
        fromDeadToLife = dataGameRules.fromDeadToLife
        forAlive = dataGameRules.forAlive
    }

    override fun addToHash(hasher: Hasher) {
        fromDeadToLife.forEach {hasher.addElement(it.toInt() + 1)}
        hasher.addElement(-1) //this made to separate two sets
        fromDeadToLife.forEach {hasher.addElement(it.toInt() + 1)}
    }
}