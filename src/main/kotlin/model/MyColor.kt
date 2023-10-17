package model
import androidx.compose.ui.graphics.Color
import kotlinx.serialization.Serializable
import kotlin.random.Random

@Serializable
enum class MyColor(var color: Color) {
    Black(Color.Black),
    White(Color.White),
    Yellow(Color.Yellow),
    Green(Color.Green),
    Red(Color.Red);

    fun getNextColor(): MyColor {
        val values = MyColor.values()
        return values[(this.ordinal + 1) % values.size]
    }

    companion object {
        val DeadColor = Black
    }
}

fun randomColor() : MyColor {
    val values = MyColor.values()
    return values[Random.nextInt(0, values.size)]
}
