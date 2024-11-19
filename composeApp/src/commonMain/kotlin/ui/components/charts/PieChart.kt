package ui.components.charts

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Immutable
data class PieChartData(
    val slices: List<Slice>,
    val total: Int = slices.sumOf { it.value },
    val max: Int = slices.maxByOrNull { it.value }?.value ?: 0,
) {
    @Immutable
    data class Slice(
        val title: String,
        val value: Int,
        val color: Color,
    )
}

@Composable
fun PieChart(
    modifier: Modifier = Modifier,
    data: PieChartData,
    gapAngle: Float = 2f,
    highlightFactor: Float = 1.05f,
    strokeWidth: Float = 65f
) {
    Canvas(modifier = modifier) {
        val chartRadius = size.minDimension / 2.5f
        var startAngle = -90f

        data.slices.forEach { slice ->
            val percentage = slice.value.toFloat() / data.total.toFloat()
            val sweepAngle = percentage * 360f - gapAngle
            val isHighlight = slice.value == data.max

            val sliceRadius = if (isHighlight) chartRadius * highlightFactor else chartRadius
            val offset = if (isHighlight) sliceRadius * 0.1f else 0f

            drawArc(
                color = slice.color,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = strokeWidth),
                size = Size(sliceRadius * 2, sliceRadius * 2),
                topLeft = Offset(
                    (size.width - sliceRadius * 2) / 2 + offset * cos(Math.toRadians(startAngle.toDouble() + sweepAngle / 2)).toFloat(),
                    (size.height - sliceRadius * 2) / 2 + offset * sin(Math.toRadians(startAngle.toDouble() + sweepAngle / 2)).toFloat()
                )
            )

            startAngle += sweepAngle + gapAngle
        }
    }
}

@Composable
fun PieChart(
    modifier: Modifier = Modifier,
    title: String,
    pieChartData: PieChartData
) {

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
            modifier = Modifier
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            PieChart(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.5f),
                data = pieChartData,
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center
            ) {
                items(pieChartData.slices) { slice ->
                    Spacer(
                        modifier = Modifier
                            .height(8.dp)
                            .fillMaxWidth()
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                    ) {

                        Spacer(
                            modifier = Modifier
                                .size(24.dp)
                                .background(slice.color, shape = MaterialTheme.shapes.small)
                        )

                        Text(
                            modifier = Modifier
                                .padding(start = 16.dp),
                            text = "${slice.value}:",
                            color = MaterialTheme.colorScheme.onPrimary,
                            maxLines = 1
                        )

                        Text(
                            modifier = Modifier
                                .padding(start = 8.dp, end = 16.dp)
                                .width(200.dp),
                            text = slice.title,
                            color = MaterialTheme.colorScheme.onPrimary,
                            maxLines = 1
                        )
                    }

                    Spacer(
                        modifier = Modifier
                            .height(8.dp)
                            .fillMaxWidth()
                    )
                }
            }
        }
    }
}
