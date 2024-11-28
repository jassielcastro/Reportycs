package ui.components.charts

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import ui.theme.chartBarsColor
import ui.theme.primaryLight

@Immutable
data class BarChartData(
    val bars: List<Bar>,
    val maxBarValue: Float = bars.maxOfOrNull { it.value } ?: 0f,
    val roundToIntegers: Boolean = false,
    val barSize: Dp = 50.dp,
) {
    @Immutable
    data class Bar(
        val key: String,
        val value: Float,
        val color: Color = chartBarsColor,
        val background: Color = primaryLight
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
    isVertical: Boolean = true,
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
        val barContainerLongitude = if (isVertical) size.height else size.width
        val barLongitude =
            ((barContainerLongitude * (barData.value / maxValue)) * animatedHeight).coerceAtLeast(0f)
        val barSize = if (isVertical) size.width.dp else size.height.dp
        val minTextYPosition = (barContainerLongitude * 0.8f)

        val numberCenter = if (isVertical) {
            size.width
        } else {
            size.height
        }.div(2).minus(10f)

        val barEnd = if (isVertical) {
            barContainerLongitude - barLongitude
        } else {
            barLongitude
        }

        var textStyle = outTextStyle
        when {
            isVertical && barEnd > minTextYPosition -> {
                valueTextTop = (barEnd - 28f).coerceAtLeast(0f)
            }

            isVertical && barEnd < minTextYPosition -> {
                valueTextTop = (barEnd + 8f).coerceAtMost(barContainerLongitude)
                textStyle = innerTextStyle
            }

            !isVertical && barEnd < minTextYPosition -> {
                valueTextTop = (barEnd + 12f).coerceAtMost(barContainerLongitude)
            }

            !isVertical && barEnd > minTextYPosition -> {
                valueTextTop = (barEnd - 28f).coerceAtLeast(0f)
                textStyle = innerTextStyle
            }
        }

        drawRoundRect(
            color = barData.background,
            size = if (isVertical) {
                Size(barSize.toPx(), barContainerLongitude)
            } else {
                Size(barContainerLongitude, barSize.toPx())
            },
            cornerRadius = CornerRadius(8f)
        )

        if (isVertical) {
            drawRoundRect(
                color = barData.color,
                size = Size(barSize.toPx(), barLongitude),
                topLeft = Offset(0f, barEnd),
                cornerRadius = CornerRadius(8f)
            )
        } else {
            drawRoundRect(
                color = barData.color,
                size = Size(barLongitude, barSize.toPx()),
                cornerRadius = CornerRadius(8f)
            )
        }

        drawContext.canvas.nativeCanvas.apply {
            drawText(
                textMeasurer = textMeasurer,
                text = AnnotatedString(valueFormatter(barData.value)),
                style = textStyle,
                topLeft = if (isVertical) {
                    Offset(numberCenter, animatedTextTop.coerceAtMost(barContainerLongitude))
                } else {
                    Offset(animatedTextTop.coerceAtMost(barContainerLongitude), numberCenter)
                }
            )
        }
    }
}

@Composable
fun BarChart(
    title: String,
    barChartData: BarChartData,
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
                .padding(12.dp)
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

            items(barChartData.bars) { barData ->
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(barChartData.barSize)
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
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                        ),
                        outTextStyle = TextStyle(
                            color = barData.color,
                            fontSize = 16.sp,
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
                        text = barData.key,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 12.sp,
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
                        .width(24.dp)
                )
            }
        }
    }
}

@Composable
fun HorizontalBarChart(
    title: String,
    barChartData: BarChartData,
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
                .padding(12.dp)
                .wrapContentSize()
        )

        LazyColumn(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 16.dp)
        ) {
            item {
                Spacer(
                    modifier = Modifier
                        .height(16.dp)
                )
            }

            items(barChartData.bars) { barData ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                ) {
                    Text(
                        text = barData.key,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.End,
                        maxLines = 1,
                        overflow = TextOverflow.Clip,
                        modifier = Modifier
                            .wrapContentSize()
                            .padding(bottom = 8.dp)
                    )

                    SimpleBarDrawer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(barChartData.barSize),
                        barData = barData,
                        maxValue = barChartData.maxBarValue,
                        textMeasurer = textMeasure,
                        innerTextStyle = TextStyle(
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                        ),
                        outTextStyle = TextStyle(
                            color = barData.color,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                        ),
                        valueFormatter = {
                            if (barChartData.roundToIntegers) {
                                it.toInt().toString()
                            } else {
                                it.toString()
                            }
                        },
                        isVertical = false
                    )
                }

                Spacer(
                    modifier = Modifier
                        .height(20.dp)
                )
            }
        }
    }
}
