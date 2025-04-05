package com.rmp.ui.sleep.history

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rmp.R
import com.rmp.ui.components.GraphScreen
import com.rmp.ui.components.GraphType
import com.rmp.ui.components.ViewPeriod
import kotlin.math.ceil
import kotlin.math.floor

@Composable
fun SleepHistoryScreen(
    sleepHistoryViewModel: SleepHistoryViewModel
)
{
    GraphScreen(
        GraphType.SLEEP,
        { value, dataPoints -> SleepChart(value, dataPoints) },
        sleepHistoryViewModel
    )
}

@Composable
private fun SleepChart(
    totalSleep: Int,
    dataPoints: List<Pair<String, Float>>,
    viewPeriod: ViewPeriod = ViewPeriod.DAY,
    tickValues: List<Float> = calculateSleepTickValues(dataPoints.map { it.second }),
    allTickValues: List<Float> = calculateAllSleepTickValues(dataPoints.map { it.second }, viewPeriod)
) {
    val lineColor = colorResource(R.color.blue)
    val textColor = colorResource(R.color.black)
    val axisColor = Color.Black
    val gridColor = Color.DarkGray.copy(alpha = 0.3f)

    val numericDataPoints = dataPoints.map { (date, minutes) ->
        val numericValue = when (viewPeriod) {
            ViewPeriod.DAY -> date.takeLast(2).toFloat()
            ViewPeriod.MONTH -> date.takeLast(2).toFloat()
            ViewPeriod.YEAR -> date.take(4).toFloat()
        }
        numericValue to minutes
    }

    Log.d("Points", dataPoints.toString())

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        Row(verticalAlignment = Alignment.Bottom) {
            val sleepDuration = minutesToHoursMinutes(totalSleep)

            Text(
                text = stringResource(R.string.sleep_measure).format(sleepDuration.first, sleepDuration.second),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
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

                val minY = allTickValues.minOrNull() ?: 0f
                val maxY = allTickValues.maxOrNull() ?: 600f
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
                        color = gridColor,
                        start = Offset(paddingLeft, yPos),
                        end = Offset(paddingLeft + drawWidth, yPos),
                        strokeWidth = 1f
                    )

                    val hours = (tickValue / 60).toInt()
                    val minutes = (tickValue % 60).toInt()
                    val timeText = when {
                        hours > 0 -> "${hours}ч"
                        minutes > 0 -> "${minutes}м"
                        else -> "0"
                    }

                    drawContext.canvas.nativeCanvas.drawText(
                        timeText,
                        paddingLeft - 10f,
                        yPos + 10f,
                        textPaint
                    )
                }

                when {
                    numericDataPoints.isEmpty() -> {
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
                    numericDataPoints.size == 1 -> {
                        val singleValue = numericDataPoints.first().second
                        val yPos = size.height - paddingBottom - ((singleValue - minY) / (maxY - minY)) * drawHeight

                        val visibleYPos = yPos.coerceIn(
                            paddingTop,
                            size.height - paddingBottom
                        )

                        drawLine(
                            color = lineColor,
                            start = Offset(paddingLeft, visibleYPos),
                            end = Offset(paddingLeft + drawWidth, visibleYPos),
                            strokeWidth = 3f,
                            cap = StrokeCap.Round
                        )
                    }
                    else -> {
                        val minX = numericDataPoints.minOf { it.first }
                        val maxX = numericDataPoints.maxOf { it.first }
                        val rangeX = maxX - minX

                        fun toScreenX(x: Float) = paddingLeft + ((x - minX) / rangeX) * drawWidth
                        fun toScreenY(y: Float) = size.height - paddingBottom - ((y - minY) / rangeY) * drawHeight

                        val path = Path().apply {
                            val firstPoint = numericDataPoints.first()
                            moveTo(toScreenX(firstPoint.first), toScreenY(firstPoint.second))

                            for (i in 1 until numericDataPoints.size) {
                                val prev = numericDataPoints[i-1]
                                val curr = numericDataPoints[i]

                                val control1 = Offset(
                                    toScreenX(prev.first) + (toScreenX(curr.first) - toScreenX(prev.first)) * 0.3f,
                                    toScreenY(prev.second)
                                )
                                val control2 = Offset(
                                    toScreenX(curr.first) - (toScreenX(curr.first) - toScreenX(prev.first)) * 0.3f,
                                    toScreenY(curr.second)
                                )

                                cubicTo(
                                    control1.x, control1.y,
                                    control2.x, control2.y,
                                    toScreenX(curr.first), toScreenY(curr.second)
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

private fun calculateSleepTickValues(sleepMinutes: List<Float>): List<Float> {
    if (sleepMinutes.isEmpty()) return listOf(120f, 240f, 360f, 480f)

    val values = sleepMinutes
    val min = (values.minOrNull() ?: 0f).let { floor(it / 60f) * 60f }
    val max = (values.maxOrNull() ?: 600f).let { ceil(it / 60f) * 60f }
    val step = if (max - min > 360f) 120f else 60f

    return generateSequence(min) { it + step }
        .takeWhile { it <= max }
        .toList()
}

private fun calculateAllSleepTickValues(sleepMinutes: List<Float>, viewPeriod: ViewPeriod): List<Float> {
    val defaultValues = when (viewPeriod) {
        ViewPeriod.DAY -> listOf(0f, 120f, 240f, 360f, 480f, 600f)
        ViewPeriod.MONTH -> listOf(0f, 180f, 360f, 540f)
        ViewPeriod.YEAR -> listOf(0f, 240f, 480f)
    }

    if (sleepMinutes.isEmpty()) return defaultValues

    val minValue = maxOf(floor((sleepMinutes.minOrNull() ?: 0f) / 60f) * 60f, 0f)
    val maxValue = maxOf(ceil((sleepMinutes.maxOrNull() ?: 600f) / 60f) * 60f, 60f)

    return if (minValue == maxValue) {
        listOf(
            maxOf(minValue - 120f, 0f),
            minValue,
            minValue + 120f
        ).filter { it >= 0f }
    } else {
        generateSequence(minValue) { it + 60f }
            .takeWhile { it <= maxValue }
            .toList()
    }
}

fun minutesToHoursMinutes(totalMinutes: Int): Pair<Int, Int> {
    return Pair(totalMinutes / 60, totalMinutes % 60)
}