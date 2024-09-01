package ui.components.charts

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Immutable
data class BarChartData(
    val bars: List<Bar>,
    val maxBarValue: Float = bars.maxOfOrNull { it.value } ?: 0f,
    val roundToIntegers: Boolean = false,
    val barWidth: Dp,
) {
    @Immutable
    data class Bar(
        val value: Float,
        val color: Color,
        val background: Color
    )
}

@Composable
fun SimpleBarDrawer(
    modifier: Modifier = Modifier,
    barData: BarChartData.Bar,
    maxValue: Float,
    textMeasurer: TextMeasurer,
    innerTextStyle: TextStyle,
    outTextStyle: TextStyle,
    valueFormatter: (Float) -> String = { it.toString() },
) {

    var animationEnabled by remember { mutableStateOf(false) }
    val animatedHeight: Float by animateFloatAsState(
        targetValue = if (animationEnabled) 1f else 0f,
        animationSpec = tween(
            durationMillis = 700,
            delayMillis = 300,
            easing = LinearOutSlowInEasing
        )
    )

    var valueTextTop by remember { mutableStateOf(1000f) }
    val animatedTextTop: Float by animateFloatAsState(
        targetValue = valueTextTop,
        animationSpec = tween(
            durationMillis = 200,
            easing = LinearOutSlowInEasing
        )
    )

    LaunchedEffect(barData) {
        animationEnabled = true
    }

    Canvas(modifier = modifier) {
        val barContainerHeight = size.height
        val barHeight =
            ((barContainerHeight * (barData.value / maxValue)) * animatedHeight).coerceAtLeast(0f)
        val barWidth = size.width.dp
        val minTextYPosition = (barContainerHeight * 0.8f)

        val numberCenter = (size.width / 2f) - 10f
        val barTopY = barContainerHeight - barHeight
        var textStyle = outTextStyle
        if (barTopY > minTextYPosition) {
            valueTextTop = (barTopY - 28f).coerceAtLeast(0f)
        } else {
            valueTextTop = (barTopY + 8f).coerceAtMost(barContainerHeight)
            textStyle = innerTextStyle
        }

        drawRoundRect(
            color = barData.background,
            size = Size(barWidth.toPx(), barContainerHeight),
            cornerRadius = CornerRadius(8f)
        )

        drawRoundRect(
            color = barData.color,
            size = Size(barWidth.toPx(), barHeight),
            topLeft = Offset(0f, barTopY),
            cornerRadius = CornerRadius(8f)
        )

        drawContext.canvas.nativeCanvas.apply {
            drawText(
                textMeasurer = textMeasurer,
                text = AnnotatedString(valueFormatter(barData.value)),
                style = textStyle,
                topLeft = Offset(numberCenter, animatedTextTop.coerceAtMost(barContainerHeight))
            )
        }
    }
}

@Composable
fun BarChart(
    title: String,
    barChartData: BarChartData,
    labels: List<String>,
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
                .padding(16.dp)
                .wrapContentSize()
        )

        LazyRow(
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier
                .fillMaxSize()
        ) {
            item {
                Spacer(
                    modifier = Modifier
                        .width(16.dp)
                )
            }

            itemsIndexed(barChartData.bars) { index, barData ->
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(barChartData.barWidth)
                ) {
                    SimpleBarDrawer(
                        modifier = Modifier
                            .fillMaxHeight(0.85f)
                            .fillMaxWidth(),
                        barData = barData,
                        maxValue = barChartData.maxBarValue,
                        textMeasurer = textMeasure,
                        innerTextStyle = TextStyle(
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                        ),
                        outTextStyle = TextStyle(
                            color = barData.color,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                        ),
                        valueFormatter = {
                            if (barChartData.roundToIntegers) {
                                it.toInt().toString()
                            } else {
                                it.toString()
                            }
                        }
                    )

                    Text(
                        text = labels.getOrNull(index).orEmpty(),
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Clip,
                        modifier = Modifier
                            .fillMaxSize()
                    )
                }

                Spacer(
                    modifier = Modifier
                        .width(40.dp)
                )
            }
        }
    }
}
