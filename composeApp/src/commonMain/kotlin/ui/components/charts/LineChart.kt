package ui.components.charts

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ui.theme.errorLight

sealed class BezierCurveStyle {
    class Fill(val brush: Brush) : BezierCurveStyle()

    class CurveStroke(
        val brush: Brush,
        val stroke: Stroke,
    ) : BezierCurveStyle()

    class StrokeAndFill(
        val fillBrush: Brush,
        val strokeBrush: Brush,
        val stroke: Stroke,
    ) : BezierCurveStyle()
}

@Composable
fun BezierCurve(
    modifier: Modifier,
    points: List<Float>,
    minPoint: Float? = null,
    maxPoint: Float? = null,
    style: BezierCurveStyle,
    textMeasurer: TextMeasurer,
    textStyle: TextStyle,
    valueFormatter: (Float) -> String = { it.toString() },
) {
    var size by remember {
        mutableStateOf(IntSize.Zero)
    }

    var animationEnabled by remember { mutableStateOf(false) }
    val animatedHeight: Float by animateFloatAsState(
        targetValue = if (animationEnabled) 1f else 0f,
        animationSpec = tween(
            durationMillis = 700,
            delayMillis = 300,
            easing = LinearOutSlowInEasing
        )
    )

    LaunchedEffect(points) {
        animationEnabled = true
    }

    Canvas(
        modifier = modifier.onSizeChanged {
            size = it
        },
        onDraw = {
            if (size != IntSize.Zero && points.size > 1) {
                drawBezierCurve(
                    size = size,
                    heightPercent = animatedHeight,
                    points = points,
                    fixedMinPoint = minPoint,
                    fixedMaxPoint = maxPoint,
                    style = style,
                )

                drawDottedLines(
                    maxValue = points.max().coerceAtLeast(10f),
                    textMeasurer = textMeasurer,
                    textStyle = textStyle,
                    valueFormatter = valueFormatter,
                )
            }
        },
    )
}

private fun DrawScope.drawDottedLines(
    maxValue: Float,
    textMeasurer: TextMeasurer,
    textStyle: TextStyle,
    valueFormatter: (Float) -> String = { it.toString() },
) {
    val dotRadius = 5.dp.toPx()
    val spacing = 10.dp.toPx()

    val dashPaint = Paint().apply {
        color = Color.Black
        strokeWidth = 2.dp.toPx()
        pathEffect = PathEffect.dashPathEffect(floatArrayOf(dotRadius, spacing))
    }

    val linePosition = listOf(
        1f to 0f,
        0.5f to 0.5f
    )

    linePosition.forEach { position ->
        // Draw number
        drawContext.canvas.nativeCanvas.apply {
            drawText(
                textMeasurer = textMeasurer,
                text = AnnotatedString(valueFormatter(maxValue * position.first)),
                style = textStyle,
                topLeft = Offset(10f, position.second * size.height)
            )
        }

        // Draw dotted line
        drawLine(
            color = Color.White.copy(alpha = 0.5f),
            start = Offset(50.dp.toPx(), (position.second * size.height).plus(12f)),
            end = Offset(size.width, (position.second * size.height).plus(12f)),
            strokeWidth = 2.dp.toPx(),
            pathEffect = dashPaint.pathEffect,
        )
    }
}

private fun DrawScope.drawBezierCurve(
    size: IntSize,
    heightPercent: Float,
    points: List<Float>,
    fixedMinPoint: Float? = null,
    fixedMaxPoint: Float? = null,
    style: BezierCurveStyle,
) {
    val maxPoint = (fixedMaxPoint ?: points.max()).coerceAtLeast(10f)
    val minPoint = fixedMinPoint ?: points.min()
    val total = maxPoint - minPoint
    val height = size.height.toFloat()
    val width = size.width.toFloat()
    val xSpacing = width / (points.size - 1F)
    var lastPoint: Offset? = null
    val path = Path()
    var firstPoint = Offset(0F, 0F)
    for (index in points.indices) {
        val x = index * xSpacing
        val y = (height - 10f) - height * ((points[index] - minPoint) / total) * heightPercent
        if (lastPoint != null) {
            buildCurveLine(path, lastPoint, Offset(x, y))
        }
        lastPoint = Offset(x, y)
        if (index == 0) {
            path.moveTo(x, y)
            firstPoint = Offset(x, y)
        }
    }
    fun closeWithBottomLine() {
        path.lineTo(width, height)
        path.lineTo(0F, height)
        path.lineTo(firstPoint.x, firstPoint.y)
    }

    when (style) {
        is BezierCurveStyle.Fill -> {
            closeWithBottomLine()
            drawPath(
                path = path,
                style = Fill,
                brush = style.brush,
            )
        }

        is BezierCurveStyle.CurveStroke -> {
            drawPath(
                path = path,
                brush = style.brush,
                style = style.stroke,
            )
        }

        is BezierCurveStyle.StrokeAndFill -> {
            drawPath(
                path = path,
                brush = style.strokeBrush,
                style = style.stroke,
            )
            closeWithBottomLine()
            drawPath(
                path = path,
                brush = style.fillBrush,
                style = Fill,
            )
        }
    }
}

private fun buildCurveLine(path: Path, startPoint: Offset, endPoint: Offset) {
    val firstControlPoint = Offset(
        x = startPoint.x + (endPoint.x - startPoint.x) / 2F,
        y = startPoint.y,
    )
    val secondControlPoint = Offset(
        x = startPoint.x + (endPoint.x - startPoint.x) / 2F,
        y = endPoint.y,
    )
    path.cubicTo(
        x1 = firstControlPoint.x,
        y1 = firstControlPoint.y,
        x2 = secondControlPoint.x,
        y2 = secondControlPoint.y,
        x3 = endPoint.x,
        y3 = endPoint.y,
    )
}

@Composable
fun LineChart(
    title: String,
    message: String,
    dataPoints: List<Int>,
    modifier: Modifier = Modifier,
) {
    val textMeasure = rememberTextMeasurer()

    Column(
        modifier = modifier
    ) {

        Text(
            text = title,
            color = MaterialTheme.colorScheme.onPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(start = 16.dp, top = 16.dp)
                .wrapContentSize()
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .wrapContentSize(align = Alignment.Center)
        ) {
            Spacer(
                modifier = Modifier
                    .padding(start = 12.dp, end = 8.dp)
                    .size(8.dp)
                    .clip(MaterialTheme.shapes.small)
                    .background(MaterialTheme.colorScheme.error)
            )

            Text(
                text = message,
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .wrapContentSize()
            )
        }

        LazyRow(
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier
                .fillMaxSize()
        ) {
            item {
                BezierCurve(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .fillMaxHeight()
                        .width((20 * dataPoints.size).dp),
                    points = dataPoints.map { it.toFloat() },
                    style = BezierCurveStyle.Fill(
                        brush = Brush.verticalGradient(
                            listOf(
                                errorLight,
                                Color(0xffbb582e),
                                Color(0xfffebe54)
                            )
                        )
                    ),
                    textMeasurer = textMeasure,
                    textStyle = TextStyle(
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                    ),
                    valueFormatter = { value ->
                        value.toInt().toString()
                    },
                )
            }
        }
    }
}
