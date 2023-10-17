package model

class Hasher(startHash: Int = 0, private val multiplier : Int = 31, private val module: Int = 1_000_000_007) {
    var hash : Int = startHash
        private set

    fun addElement(element : Int) {
        hash = ((hash.toLong() * multiplier + element) % module).toInt()
    }
}

interface IHashable {
    fun addToHash(hasher: Hasher)
}

