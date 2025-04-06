package com.rmp.ui.heart

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rmp.R
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import com.rmp.ui.components.GraphScreen
import com.rmp.ui.components.GraphType
import kotlin.math.ceil
import kotlin.math.floor

@Composable
fun HeartScreen(
    heartViewModel: HeartViewModel
)
{
    GraphScreen(
        GraphType.HEART,
        { value, dataPoints -> HeartRateChart(value, dataPoints) },
        heartViewModel
    )
}

/*private fun generateSampleData(currentValue: Int): List<Pair<Float, Float>> {
    return listOf(
        0.0f to (currentValue - 15).coerceAtLeast(60).toFloat(),
        0.2f to (currentValue - 10).toFloat(),
        0.4f to (currentValue - 5).toFloat(),
        0.6f to currentValue.toFloat(),
        0.8f to (currentValue + 5).toFloat(),
        1.0f to (currentValue + 3).toFloat()
    )
}*/

@Composable
private fun HeartRateChart(
    value: Int,
    dataPoints: List<Pair<String, Float>>,
    tickValues: List<Float> = calculateTickValues(dataPoints),
    allTickValues: List<Float> = calculateAllTickValues(dataPoints)
) {
    val lineColor = colorResource(R.color.pink_antique)
    val textColor = colorResource(R.color.black)
    val axisColor = Color.Black
    val gridColor = Color.DarkGray.copy(alpha = 0.3f)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = "$value ",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 28.dp)
            )
            Text(
                text = stringResource(R.string.pulse),
                fontSize = 18.sp,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.padding(bottom = 28.dp)
            )
        }

        Box(
            modifier = Modifier
                .size(200.dp)
                .padding(horizontal = 50.dp)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val paddingLeft = -80f
                val paddingRight = 50f
                val paddingTop = 30f
                val paddingBottom = 50f

                val drawSize = size.height - paddingTop - paddingBottom
                val drawWidth = drawSize
                val drawHeight = drawSize

                val minY = allTickValues.minOrNull() ?: 40f
                val maxY = allTickValues.maxOrNull() ?: 120f
                val rangeY = maxY - minY

                drawLine(
                    color = axisColor,
                    start = Offset(paddingLeft, paddingTop),
                    end = Offset(paddingLeft, size.height - paddingBottom),
                    strokeWidth = 2f
                )

                drawLine(
                    color = axisColor,
                    start = Offset(paddingLeft, size.height - paddingBottom),
                    end = Offset(paddingLeft + drawWidth, size.height - paddingBottom),
                    strokeWidth = 2f
                )

                val textPaint = android.graphics.Paint().apply {
                    color = textColor.toArgb()
                    textSize = 24f
                    textAlign = android.graphics.Paint.Align.RIGHT
                }

                tickValues.forEach { tickValue ->
                    val yPos = size.height - paddingBottom - ((tickValue - minY) / rangeY * drawHeight)

                    drawLine(
                        color = Color.DarkGray,
                        start = Offset(paddingLeft, yPos),
                        end = Offset(paddingLeft + drawWidth, yPos),
                        strokeWidth = 1f
                    )

                    drawContext.canvas.nativeCanvas.drawText(
                        "%.0f".format(tickValue),
                        paddingLeft - 10f,
                        yPos + 10f,
                        textPaint
                    )
                }

                when {
                    dataPoints.isEmpty() -> {
                        val noDataPaint = android.graphics.Paint().apply {
                            color = textColor.toArgb()
                            textSize = 32f
                            textAlign = android.graphics.Paint.Align.CENTER
                        }

                        drawContext.canvas.nativeCanvas.drawText(
                            "Нет данных",
                            size.width / 2f,
                            size.height / 2f,
                            noDataPaint
                        )
                    }
                    dataPoints.size == 1 -> {
                        val singleValue = dataPoints.first().second
                        val yPos = size.height - paddingBottom - ((singleValue - minY) / rangeY * drawHeight)

                        drawLine(
                            color = lineColor,
                            start = Offset(paddingLeft, yPos),
                            end = Offset(paddingLeft + drawWidth, yPos),
                            strokeWidth = 3f,
                            cap = StrokeCap.Round
                        )
                    }
                    else -> {
                        val minX = dataPoints.minOf { it.first }
                        val maxX = dataPoints.maxOf { it.first }
                        val rangeX = maxX.toFloat() - minX.toFloat()

                        fun toScreenX(x: Float) = paddingLeft + ((x.toFloat() - minX.toFloat()) / rangeX) * drawWidth

                        fun toScreenY(y: Float) = size.height - paddingBottom - ((y - minY) / rangeY) * drawHeight

                        val path = Path().apply {
                            val firstPoint = dataPoints.first()
                            moveTo(toScreenX(firstPoint.first.toFloat()), toScreenY(firstPoint.second))

                            for (i in 1 until dataPoints.size) {
                                val prev = dataPoints[i-1]
                                val curr = dataPoints[i]

                                val control1 = Offset(
                                    toScreenX(prev.first.toFloat()) + (toScreenX(curr.first.toFloat()) - toScreenX(prev.first.toFloat())) * 0.3f,
                                    toScreenY(prev.second)
                                )
                                val control2 = Offset(
                                    toScreenX(curr.first.toFloat()) - (toScreenX(curr.first.toFloat()) - toScreenX(prev.first.toFloat())) * 0.3f,
                                    toScreenY(curr.second)
                                )

                                cubicTo(
                                    control1.x, control1.y,
                                    control2.x, control2.y,
                                    toScreenX(curr.first.toFloat()), toScreenY(curr.second)
                                )
                            }
                        }

                        drawPath(
                            path = path,
                            color = lineColor,
                            style = Stroke(
                                width = 3f,
                                cap = StrokeCap.Round,
                                join = StrokeJoin.Round
                            )
                        )
                    }
                }
            }
        }
    }
}

private fun calculateTickValues(dataPoints: List<Pair<String, Float>>): List<Float> {
    if (dataPoints.isEmpty()) return listOf(60f, 80f, 100f, 120f)

    val values = dataPoints.map { it.second }
    val min = values.minOrNull() ?: 40f
    val max = values.maxOrNull() ?: 140f
    val step = ((max - min) / 4).coerceAtLeast(10f)

    return generateSequence(min) { it + step }
        .takeWhile { it <= max }
        .toList()
}

private fun calculateAllTickValues(dataPoints: List<Pair<String, Float>>): List<Float> {
    if (dataPoints.isEmpty()) return listOf(40f, 60f, 80f, 100f, 120f)

    val values = dataPoints.map { it.second }
    val min = (values.minOrNull() ?: 40f).let { floor(it / 20f) * 20f }
    val max = (values.maxOrNull() ?: 140f).let { ceil(it / 20f) * 20f }

    return generateSequence(min) { it + 20f }
        .takeWhile { it <= max }
        .toList()
}