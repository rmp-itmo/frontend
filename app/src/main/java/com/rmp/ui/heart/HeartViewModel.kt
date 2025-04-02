package com.rmp.ui.heart

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.rmp.R
import com.rmp.data.AppContainer
import com.rmp.data.ErrorMessage
import com.rmp.data.repository.heart.GraphConfigurationDto
import com.rmp.data.repository.heart.HeartRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import kotlin.collections.listOf


class HeartViewModel(
    private val heartRepository: HeartRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HeartUiState())
    val uiState: StateFlow<HeartUiState> = _uiState

    init {
        loadHeartData(_uiState.value.selectedPeriod)
    }

    fun updatePeriod(newPeriod: HeartViewPeriod) {
        _uiState.value = _uiState.value.copy(selectedPeriod = newPeriod)
        loadHeartData(newPeriod)
    }

    private fun loadHeartData(currentPeriod: HeartViewPeriod) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true)
            }

            val graphData = async {
                val baseConfig = GraphConfigurationDto(year = getCurrentYear())

                val finalConfig = when (currentPeriod) {
                    HeartViewPeriod.MONTH -> baseConfig.copy(month = getCurrentMonth())
                    HeartViewPeriod.DAY -> baseConfig.copy(month = getCurrentMonth(), day = getCurrentDay())
                    HeartViewPeriod.YEAR -> baseConfig
                }

                heartRepository.getGraphHeart(finalConfig)
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
                            key.toFloat() to value.toFloat()
                        }

                    it.copy(heartData = HeartData(
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

    private fun getCurrentYear(): Int {
        return LocalDate.now().year
    }

    @SuppressLint("DefaultLocale")
    private fun getCurrentMonth(): String {
        return String.format("%02d", LocalDate.now().monthValue)
    }

    @SuppressLint("DefaultLocale")
    private fun getCurrentDay(): String {
        return String.format("%02d", LocalDate.now().dayOfMonth)
    }

    companion object {
        fun factory(appContainer: AppContainer): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return HeartViewModel(appContainer.heartRepository) as T
            }
        }
    }
}

data class HeartData(
    val average: Int? = null,
    val minimum: Int? = null,
    val maximum: Int? = null,
    val chartPoints: List<Pair<Float, Float>> = emptyList(),
    val currentValue: Int? = null
)

data class HeartUiState(
    val selectedPeriod: HeartViewPeriod = HeartViewPeriod.DAY,
    val isLoading: Boolean = false,
    val heartData: HeartData = HeartData(),
    val errors: List<ErrorMessage> = emptyList()
)

enum class HeartViewPeriod {
    DAY, MONTH, YEAR
}