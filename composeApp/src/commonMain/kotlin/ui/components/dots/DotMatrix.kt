package ui.components.dots

import androidx.annotation.IntRange
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntSize
import kotlin.random.Random

object DotMatrix {
    fun buildMatrix(
        @IntRange(from = 1, to = 2)
        dimension: Int,
        @IntRange(from = 2, to = 200)
        numberOfDotsPerDimen: Int,
        initialDotColor: List<Color>,
        drawIntoSize: IntSize
    ): List<List<Dot>> = (1..dimension).mapIndexed { index, _ ->
        (0 until numberOfDotsPerDimen).map {
            Dot(
                x = Random.nextInt(0, drawIntoSize.width).toFloat(),
                y = Random.nextInt(0, drawIntoSize.height).toFloat(),
                color = initialDotColor.getOrNull(index) ?: Color.White
            )
        }
    }
}
