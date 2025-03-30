package com.rmp.ui.water

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.rmp.data.AppContainer
import com.rmp.data.repository.water.WaterDailyRecord
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import com.rmp.data.repository.water.WaterStatRequest
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*


interface WaterUiState {
    val currentAmount: Float
    val dailyGoal: Int
    val waterRecords: List<WaterDailyRecord>
    val errorMessage: String?
}

sealed class WaterHistoryState {
    data object Loading : WaterHistoryState()
    data object Empty : WaterHistoryState()
    data class Success(
        val date: LocalDate,
        val totalAmount: Float,
        val records: List<WaterDailyRecord>
    ) : WaterHistoryState()
    data class Error(val message: String) : WaterHistoryState()
}

private class WaterViewModelState(
    override val currentAmount: Float = 0f,
    override val dailyGoal: Int = 2,
    override val waterRecords: List<WaterDailyRecord> = emptyList(),
    override val errorMessage: String? = null
) : WaterUiState {
    fun toUiState(): WaterUiState = this

    fun copy(
        currentAmount: Float = this.currentAmount,
        dailyGoal: Int = this.dailyGoal,
        waterRecords: List<WaterDailyRecord> = this.waterRecords,
        errorMessage: String? = this.errorMessage
    ) = WaterViewModelState(currentAmount, dailyGoal, waterRecords, errorMessage)
}

class WaterViewModel(private val container: AppContainer) : ViewModel() {
    private val _historyState = MutableStateFlow<WaterHistoryState>(WaterHistoryState.Loading)
    val historyState = _historyState.asStateFlow()
    private val viewModelState = MutableStateFlow(
        WaterViewModelState()
    )

    val uiState = viewModelState
        .map(WaterViewModelState::toUiState)
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value.toUiState()
        )

    init {
        loadWaterData()
    }

    fun loadDailyStats(date: LocalDate) {
        viewModelScope.launch {
            _historyState.value = WaterHistoryState.Loading
            try {
                val dateInt = date.format(DateTimeFormatter.BASIC_ISO_DATE).toInt()
                val response = container.waterRepository.getDailyWaterStats(WaterStatRequest(dateInt))

                if (response != null && response.records.isNotEmpty()) {
                    val totalAmount = response.records.sumOf { it.volume.toDouble() }.toFloat()
                    _historyState.value = WaterHistoryState.Success(
                        date = date,
                        totalAmount = totalAmount,
                        records = response.records.map {
                            WaterDailyRecord(it.date, it.time, (it.volume * 1000))
                        }
                    )
                } else {
                    _historyState.value = WaterHistoryState.Empty
                }
            } catch (e: Exception) {
                _historyState.value = WaterHistoryState.Error("Ошибка загрузки: ${e.message}")
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun loadWaterData() {
        viewModelScope.launch {
            try {
                val currentDate = SimpleDateFormat("yyyyMMdd").format(Date()).toInt()
                val response = container.waterRepository.getDailyWaterStats(
                    WaterStatRequest(currentDate)
                )

                response?.let { stats ->
                    val totalAmount = stats.records.sumOf { it.volume.toDouble() }.toFloat()

                    val records = stats.records.map { record ->
                        WaterDailyRecord(
                            date = record.date,
                            time = record.time,
                            volume = (record.volume * 1000)
                        )
                    }

                    updateState(
                        viewModelState.value.copy(
                            currentAmount = totalAmount,
                            waterRecords = records,
                            errorMessage = null
                        )
                    )
                }
            } catch (e: Exception) {
                updateState(
                    viewModelState.value.copy(
                        errorMessage = "Failed to load water data: ${e.message}"
                    )
                )
            }
        }
    }

    fun addWaterRecord(amount: Int) {
        viewModelScope.launch {
            try {
                val currentDate = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
                val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
                val volumeInLiters = amount / 1000.0

                val response = container.waterRepository.logWater(
                    WaterDailyRecord(
                        date = currentDate.toInt(),
                        time = currentTime,
                        volume = volumeInLiters.toFloat()
                    )
                )

                if (response != null) {
                    updateState(
                        viewModelState.value.copy(
                            currentAmount = (viewModelState.value.currentAmount + amount/1000f),
                            waterRecords = viewModelState.value.waterRecords +
                                    WaterDailyRecord(currentDate.toInt(), currentTime,
                                        amount.toFloat()
                                    ),
                            errorMessage = null
                        )
                    )
                } else {
                    updateState(
                        viewModelState.value.copy(
                            errorMessage = "Failed to log water"
                        )
                    )
                }
            } catch (e: Exception) {
                updateState(
                    viewModelState.value.copy(
                        errorMessage = "Network error"
                    )
                )
            }
        }
    }

    private fun updateState(newState: WaterViewModelState) {
        viewModelState.value = newState
    }

    companion object {
        fun factory(appContainer: AppContainer): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return WaterViewModel(appContainer) as T
                }
            }
    }
}