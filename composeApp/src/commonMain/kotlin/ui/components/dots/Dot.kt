package ui.components.dots

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

data class Dot(
    var x: Float,
    var y: Float,
    val radio: Float = 5f,
    var dirX: Float = Random.nextInt(-1, 1).toFloat(),
    var dirY: Float = Random.nextInt(-1, 1).toFloat(),
    val velocity: Float = 0.1f,
    val color: Color
) {
    fun draw(scope: DrawScope) = with(scope) {
        drawCircle(
            color = color,
            radius = radio,
            center = Offset(x, y),
            style = Stroke(width = 3.5f)
        )
    }

    fun connect(scope: DrawScope, targetDot: Dot) = with(scope) {
        if (targetDot == this@Dot) return@with

        val dx = targetDot.x - x
        val dy = targetDot.y - y

        val distance = sqrt(dx.pow(2) + dy.pow(2))

        if (distance < 80) {
            val angle = atan2(dy, dx)

            val startX = x + cos(angle) * radio
            val startY = y + sin(angle) * radio

            val endX = targetDot.x - cos(angle) * targetDot.radio
            val endY = targetDot.y - sin(angle) * targetDot.radio

            drawLine(
                color = color,
                start = Offset(startX, startY),
                end = Offset(endX, endY),
                strokeWidth = 2f
            )
        }
    }

    fun moveInto(size: Size) {
        if (dirX == 0f) {
            dirX = 1f
        }

        if (dirY == 0f) {
            dirY = 1f
        }

        x += dirX * velocity
        y += dirY * velocity

        if (x + radio > size.width || x < 0) {
            dirX *= -1
        }

        if (y + radio > size.height || y < 0) {
            dirY *= -1
        }
    }
}
