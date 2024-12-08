package ui.components.dots

import androidx.annotation.IntRange
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import kotlin.random.Random

object DotMatrix {
    fun buildMatrix(
        @IntRange(from = 1, to = 2)
        dimension: Int,
        @IntRange(from = 2, to = 200)
        numberOfDotsPerDimen: Int,
        initialDotColor: List<Color>,
        screenWidth: Dp,
        screenHeight: Dp,
    ): List<List<Dot>> = (1..dimension).mapIndexed { index, _ ->
        (0 until numberOfDotsPerDimen).map {
            Dot(
                x = Random.nextInt(0, screenWidth.value.toInt()).toFloat(),
                y = Random.nextInt(0, screenHeight.value.toInt()).toFloat(),
                color = initialDotColor.getOrNull(index) ?: Color.White
            )
        }
    }
}
