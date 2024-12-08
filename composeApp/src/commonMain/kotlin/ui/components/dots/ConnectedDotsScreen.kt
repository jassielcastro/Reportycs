package ui.components.dots

import androidx.annotation.IntRange
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.isActive

@Composable
fun ConnectedDotsScreen(
    modifier: Modifier,
    @IntRange(from = 1L, to = 300L)
    dotsCount: Int,
    dotColors: List<Color>
) {
    var framePosition by remember { mutableStateOf(0f) }

    LaunchedEffect(Unit) {
        while (isActive) {
            withFrameMillis {
                framePosition += 1f
            }
        }
    }

    BoxWithConstraints(
        modifier = modifier,
    ) {
        val screenWidth = maxWidth
        val screenHeight = maxHeight

        val dots by remember(screenWidth, screenHeight) {
            derivedStateOf {
                DotMatrix.buildMatrix(
                    dimension = dotColors.size,
                    numberOfDotsPerDimen = dotsCount,
                    screenWidth = screenWidth,
                    screenHeight = screenHeight,
                    initialDotColor = dotColors
                )
            }
        }

        Canvas(
            modifier = Modifier
                .fillMaxSize(),
            onDraw = {
                if (dots.isNotEmpty() && framePosition > 1f) {
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
}
