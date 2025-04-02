package com.rmp.ui.heart

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.rmp.ui.components.AppScreen
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rmp.ui.LocalNavController
import com.rmp.ui.RmpDestinations
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.roundToInt

@Composable
fun HeartScreen(
    heartViewModel: HeartViewModel
)
{
    val uiState by heartViewModel.uiState.collectAsStateWithLifecycle()

    AppScreen(
        showButtons = false,
        showButtonHome = true
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.heart),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            PeriodSelector(
                selectedPeriod = uiState.selectedPeriod,
                onPeriodSelected = { newPeriod ->
                    heartViewModel.updatePeriod(newPeriod)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            var lastHeartValue = 0F
            if (!uiState.heartData.chartPoints.isEmpty()) {
                lastHeartValue = uiState.heartData.chartPoints.last().second
            }
            HeartRateChart(lastHeartValue.roundToInt(), uiState.heartData.chartPoints)

            Spacer(modifier = Modifier.height(16.dp))

            HeartStats(uiState)
        }
    }
}

@Composable
private fun PeriodSelector(
    selectedPeriod: HeartViewPeriod,
    onPeriodSelected: (HeartViewPeriod) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        HeartViewPeriod.entries.forEach { period ->
            var textWidth by remember { mutableIntStateOf(0) }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(8.dp)
                    .clickable { onPeriodSelected(period) }
            ) {
                Text(
                    text = when (period) {
                        HeartViewPeriod.DAY -> stringResource(R.string.day)
                        HeartViewPeriod.MONTH -> stringResource(R.string.month)
                        HeartViewPeriod.YEAR -> stringResource(R.string.year)
                    },
                    color = if (period == selectedPeriod) colorResource(R.color.pink_antique) else colorResource(R.color.black),
                    fontWeight = if (period == selectedPeriod) FontWeight.Bold else FontWeight.Normal,
                    onTextLayout = { textLayoutResult ->
                        textWidth = textLayoutResult.size.width
                    }
                )

                if (period == selectedPeriod) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Box(
                        modifier = Modifier
                            .height(2.dp)
                            .width(with(LocalDensity.current) { textWidth.toDp() + 8.dp })
                            .background(colorResource(R.color.pink_antique))
                    )
                }
            }
        }
    }
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
    dataPoints: List<Pair<Float, Float>>,
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

                if (dataPoints.isNotEmpty()) {
                    val minX = dataPoints.minOf { it.first }
                    val maxX = dataPoints.maxOf { it.first }
                    val rangeX = maxX - minX

                    fun toScreenX(x: Float) = paddingLeft + ((x - minX) / rangeX) * drawWidth

                    fun toScreenY(y: Float) = size.height - paddingBottom - ((y - minY) / rangeY) * drawHeight

                    val path = Path().apply {
                        val firstPoint = dataPoints.first()
                        moveTo(toScreenX(firstPoint.first), toScreenY(firstPoint.second))

                        for (i in 1 until dataPoints.size) {
                            val prev = dataPoints[i-1]
                            val curr = dataPoints[i]

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
                } else {
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
            }
        }
    }
}

private fun calculateTickValues(dataPoints: List<Pair<Float, Float>>): List<Float> {
    if (dataPoints.isEmpty()) return listOf(60f, 80f, 100f, 120f)

    val values = dataPoints.map { it.second }
    val min = values.minOrNull() ?: 40f
    val max = values.maxOrNull() ?: 140f
    val step = ((max - min) / 4).coerceAtLeast(10f)

    return generateSequence(min) { it + step }
        .takeWhile { it <= max }
        .toList()
}

private fun calculateAllTickValues(dataPoints: List<Pair<Float, Float>>): List<Float> {
    if (dataPoints.isEmpty()) return listOf(40f, 60f, 80f, 100f, 120f)

    val values = dataPoints.map { it.second }
    val min = (values.minOrNull() ?: 40f).let { floor(it / 20f) * 20f }
    val max = (values.maxOrNull() ?: 140f).let { ceil(it / 20f) * 20f }

    return generateSequence(min) { it + 20f }
        .takeWhile { it <= max }
        .toList()
}

    @Composable
private fun HeartStats(uiState: HeartUiState) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        HeartStatItem(
            label = stringResource(R.string.average),
            value = uiState.heartData.average ?: 0,
            modifier = Modifier.weight(1f)
        )
        HeartStatItem(
            label = stringResource(R.string.min),
            value = uiState.heartData.minimum ?: 0,
            modifier = Modifier.weight(1f)
        )
        HeartStatItem(
            label = stringResource(R.string.max),
            value = uiState.heartData.maximum ?: 0,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun HeartStatItem(
    label: String,
    value: Int,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colorResource(R.color.white)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                fontSize = 14.sp,
                color = colorResource(R.color.black),
                fontWeight = FontWeight.Bold
            )
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = "$value ",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(R.color.black)
                )
                Text(
                    text = stringResource(R.string.pulse),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.Black
                )
            }
        }
    }
}

@Composable
fun HomeButton() {
    val navigator = LocalNavController.current

    IconButton(
        onClick = { navigator.navigate(RmpDestinations.HOME_ROUTE) },
        modifier = Modifier
            .wrapContentSize()
            .padding(start = 24.dp, top = 16.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_home),
            contentDescription = (stringResource(R.string.home)),
            modifier = Modifier.size(22.dp)
        )
    }
}