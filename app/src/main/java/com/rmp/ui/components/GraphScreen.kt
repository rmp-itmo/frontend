package com.rmp.ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.rmp.R
import com.rmp.data.ErrorMessage
import com.rmp.data.repository.heart.GraphConfigurationDto
import com.rmp.data.repository.heart.GraphOutputDto
import com.rmp.ui.components.buttons.BackButton
import com.rmp.ui.sleep.history.minutesToHoursMinutes
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.math.roundToInt

@Composable
fun GraphScreen(
    type: GraphType,
    rateChart: @Composable (value: Int, dataPoints: List<Pair<String, Float>>) -> Unit,
    graphViewModel: GraphViewModel
)
{
    val uiState by graphViewModel.uiState.collectAsStateWithLifecycle()

    AppScreen(
        leftComposable = { BackButton() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = type.pageName,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            PeriodSelector(
                selectedPeriod = uiState.selectedPeriod,
                onPeriodSelected = { newPeriod ->
                    graphViewModel.updatePeriod(newPeriod)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            var lastValue = 0F
            if (uiState.graphData.chartPoints.isNotEmpty()) {
                lastValue = uiState.graphData.chartPoints.last().second
            }
            rateChart(lastValue.roundToInt(), uiState.graphData.chartPoints)

            Spacer(modifier = Modifier.height(16.dp))

            Stats(uiState, type)
        }
    }
}

@Composable
private fun PeriodSelector(
    selectedPeriod: ViewPeriod,
    onPeriodSelected: (ViewPeriod) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        ViewPeriod.entries.forEach { period ->
            var textWidth by remember { mutableIntStateOf(0) }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(8.dp)
                    .clickable { onPeriodSelected(period) }
            ) {
                Text(
                    text = when (period) {
                        ViewPeriod.DAY -> stringResource(R.string.day)
                        ViewPeriod.MONTH -> stringResource(R.string.month)
                        ViewPeriod.YEAR -> stringResource(R.string.year)
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


@Composable
private fun Stats(
    uiState: GraphUiState,
    type: GraphType
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        StatItem(
            label = stringResource(R.string.average),
            value = resolveMeasureValue(type, uiState.graphData.average ?: 0),
            modifier = Modifier.weight(1f)
        )
        StatItem(
            label = stringResource(R.string.min),
            value = resolveMeasureValue(type, uiState.graphData.minimum ?: 0),
            modifier = Modifier.weight(1f)
        )
        StatItem(
            label = stringResource(R.string.max),
            value = resolveMeasureValue(type, uiState.graphData.maximum ?: 0),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun StatItem(
    label: String,
    value: String,
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
                    text = value,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(R.color.black)
                )
            }
        }
    }
}

abstract class GraphViewModel : ViewModel() {
    protected val _uiState = MutableStateFlow(GraphUiState())
    val uiState: StateFlow<GraphUiState> = _uiState

    abstract fun updatePeriod(newPeriod: ViewPeriod)

    protected fun loadData(
        currentPeriod: ViewPeriod,
        loadSpecificData: suspend (GraphConfigurationDto) -> GraphOutputDto?
    ) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true)
            }

            val graphData = async {
                val baseConfig = GraphConfigurationDto(year = getCurrentYear())

                val finalConfig = when (currentPeriod) {
                    ViewPeriod.MONTH -> baseConfig.copy(month = getCurrentMonth())
                    ViewPeriod.DAY -> baseConfig.copy(month = getCurrentMonth(), day = getCurrentDay())
                    ViewPeriod.YEAR -> baseConfig
                }

                loadSpecificData(finalConfig)
            }.await()

            if (graphData == null) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errors = listOf(ErrorMessage(null, R.string.error_load_data))
                    )
                }
            } else {
                _uiState.update {
                    val chartPoints = graphData.points.entries
                        .sortedBy { it.key }
                        .map { (key, value) ->
                            key.toString() to value.toFloat()
                        }

                    it.copy(graphData = GraphData(
                        average = graphData.avgValue.toInt(),
                        minimum = graphData.lowestValue.toInt(),
                        maximum = graphData.highestValue.toInt(),
                        chartPoints = chartPoints,
                        currentValue = graphData.avgValue.toInt()
                    ))
                }
            }
        }
    }
}

data class GraphUiState(
    val selectedPeriod: ViewPeriod = ViewPeriod.DAY,
    val isLoading: Boolean = false,
    val graphData: GraphData = GraphData(),
    val errors: List<ErrorMessage> = emptyList()
)

data class GraphData(
    val average: Int? = null,
    val minimum: Int? = null,
    val maximum: Int? = null,
    val chartPoints: List<Pair<String, Float>> = emptyList(),
    val currentValue: Int? = null
)

enum class ViewPeriod {
    DAY, MONTH, YEAR
}

enum class GraphType(val pageName: String) {
    HEART("Сердце"), SLEEP("Сон");
}

@Composable
fun resolveMeasureValue(type: GraphType, vararg values: Any): String {
    return when (type) {
        GraphType.HEART -> stringResource(R.string.pulse_measure).format(values[0])
        GraphType.SLEEP -> {
            val sleepDuration = minutesToHoursMinutes(values[0] as Int)
            stringResource(R.string.sleep_measure).format(sleepDuration.first, sleepDuration.second)
        }
    }
}

fun getCurrentYear(): Int {
    return LocalDate.now().year
}

@SuppressLint("DefaultLocale")
fun getCurrentMonth(): String {
    return String.format("%02d", LocalDate.now().monthValue)
}

@SuppressLint("DefaultLocale")
fun getCurrentDay(): String {
    return String.format("%02d", LocalDate.now().dayOfMonth)
}