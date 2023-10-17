package ui

import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput

@Suppress("SpellCheckingInspection")
fun Modifier.myHoverable(
    onPointerMoved: (offset: Offset) -> Unit,
    onExit: () -> Unit,
): Modifier = composed(
    inspectorInfo = {
        name = "hoverable"
        properties["onPointerMoved"] = onPointerMoved
        properties["onExit"] = onExit
    }
) {
    pointerInput(onPointerMoved, onExit) {
        awaitPointerEventScope {
            while (true) {
                val event = awaitPointerEvent()

                when (event.type) {
                    PointerEventType.Move, PointerEventType.Enter -> {
                        onPointerMoved(event.changes.first().position)
                    }

                    PointerEventType.Exit -> {
                        onExit()
                    }
                }
            }
        }
    }
}