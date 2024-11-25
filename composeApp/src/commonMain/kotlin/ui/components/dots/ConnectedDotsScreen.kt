package ui.components.dots

import androidx.annotation.IntRange
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import kotlinx.coroutines.isActive

@Composable
fun ConnectedDotsScreen(
    modifier: Modifier,
    @IntRange(from = 1, to = 2)
    dimension: Int,
    @IntRange(from = 1L, to = 300L)
    dotsSize: Int,
    dotColors: List<Color>
) {
    var size by remember {
        mutableStateOf(IntSize.Zero)
    }

    var dots = remember { listOf<List<Dot>>() }
    var framePosition by remember { mutableStateOf(0f) }

    LaunchedEffect(Unit) {
        while (isActive) {
            withFrameMillis {
                framePosition += 1f
            }
        }
    }

    Canvas(
        modifier = modifier.onSizeChanged {
            size = it
            dots = DotMatrix.buildMatrix(
                dimension = dimension,
                numberOfDotsPerDimen = dotsSize,
                drawIntoSize = size,
                initialDotColor = dotColors
            )
        },
        onDraw = {
            if (size != IntSize.Zero && dots.isNotEmpty() && framePosition > 1f) {
                dots.forEach { dotList ->
                    dotList.forEach { startDot ->
                        dotList.forEach { targetDot ->
                            startDot.connect(this, targetDot)
                        }
                        startDot.moveInto(this.size)
                        startDot.draw(this)
                    }
                }
            }
        },
    )
}
