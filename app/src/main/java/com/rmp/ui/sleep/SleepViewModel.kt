package com.rmp.ui.sleep

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.rmp.R
import com.rmp.data.ErrorMessage
import com.rmp.data.repository.sleep.SleepGetHistoryDto
import com.rmp.data.repository.sleep.SleepRepository
import com.rmp.data.repository.sleep.SleepResponseDto
import com.rmp.data.repository.sleep.SleepUploadDto
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import java.time.Duration


interface SleepUiState {
    val isLoading: Boolean
    val goBadTime: String
    val wakeUpTime: String
    val isFirstTime: Boolean
    val dailyGoal: Float
    val sleepRecords: List<SleepResponseDto>
    val errors: List<ErrorMessage>
    val timeError: Int?
    val quality: Int
}


data class SleepLogDto(
    val startHours: Int,
    val startMinutes: Int,
    val finishHours: Int,
    val finishMinutes: Int
)

private class SleepViewModelState(
    override val isLoading: Boolean = false,
    override val goBadTime: String = "00:00",
    override val wakeUpTime: String = "08:00",
    override val isFirstTime: Boolean = false,
    override val dailyGoal: Float = 8f,
    override val sleepRecords: List<SleepResponseDto> = emptyList(),
    override val errors: List<ErrorMessage> = emptyList(),
    override val timeError: Int? = null,
    override val quality: Int = 1

) : SleepUiState {

    fun toUiState(): SleepUiState = this

    fun copy(
        isLoading: Boolean = this.isLoading,
        goBadTime: String = this.goBadTime,
        wakeUpTime: String = this.wakeUpTime,
        isFirstTime: Boolean = this.isFirstTime,
        dailyGoal: Float = this.dailyGoal,
        sleepRecords: List<SleepResponseDto> = this.sleepRecords,
        errors: List<ErrorMessage> = this.errors,
        timeError: Int? = this.timeError,
        quality: Int = this.quality
    ) = SleepViewModelState(isLoading, goBadTime, wakeUpTime, isFirstTime, dailyGoal,
        sleepRecords, errors, timeError, quality)
}


class SleepViewModel(private val sleepRepository: SleepRepository): ViewModel() {
    private val viewModelState = MutableStateFlow(
        SleepViewModelState()
    )

    init {
        viewModelScope.launch {
            checkFirstTime()
            loadMonthHistory()
        }
    }

    val uiState = viewModelState
        .map(SleepViewModelState::toUiState)
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value.toUiState()
        )

    init {
        viewModelScope.launch {}
    }

    fun onQualityChange(newValue: Int) {
        updateState(viewModelState.value.copy(quality = newValue))
    }

    companion object {
        fun factory(sleepRepository: SleepRepository): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return SleepViewModel(sleepRepository) as T
            }
        }
    }

    private fun updateState(newState: SleepViewModelState) {
        viewModelState.value = newState.copy(
            goBadTime = newState.goBadTime,
            isFirstTime = newState.isFirstTime,
            dailyGoal = newState.dailyGoal,
            sleepRecords = newState.sleepRecords,
            errors = newState.errors,
            timeError = newState.timeError,
            quality = newState.quality
        )
    }

    private fun checkFirstTime() {
        viewModelScope.launch {
            val currentDate = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date()).toInt()

            val history = async { sleepRepository.getSleepHistory(
                SleepGetHistoryDto(
                    currentDate,
                    currentDate
                ))
            }.await()

            if (history == null) {
                viewModelState.update {
                    it.copy(
                        isLoading = false,
                        errors = listOf(ErrorMessage(null, R.string.error_load_data))
                    )
                }
            } else {
                if (history.data.isEmpty()) {
                    viewModelState.update {
                        it.copy(
                            isFirstTime = true,
                        )
                    }
                } else {
                    viewModelState.update {
                        it.copy(
                            isFirstTime = false,
                        )
                    }
                }
            }
        }
    }

    private fun loadMonthHistory() {
        viewModelScope.launch {
            val currentDate = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date()).toInt()
            val oneMonthAgo = LocalDate.now().minusMonths(1)
                .format(DateTimeFormatter.ofPattern("yyyyMMdd")).toInt()

            val history = async { sleepRepository.getSleepHistory(
                SleepGetHistoryDto(
                    dateFrom = oneMonthAgo,
                    dateTo = currentDate
                ))
            }.await()

            if (history == null) {
                viewModelState.update {
                    it.copy(
                        isLoading = false,
                        errors = listOf(ErrorMessage(null, R.string.error_load_data))
                    )
                }
            } else {
                viewModelState.update {
                    it.copy(
                        sleepRecords = history.data
                    )
                }
            }
            viewModelState.update {
                it.copy(
                    isLoading = true
                )
            }
        }
    }

    fun saveSleep(time: SleepLogDto) {
        viewModelScope.launch {
            val goBadTime = LocalTime.of(time.startHours, time.startMinutes)
            val wakeUpTime = LocalTime.of(time.finishHours, time.finishMinutes)

            val duration = if (wakeUpTime.isBefore(goBadTime)) {
                Duration.between(goBadTime, LocalTime.MAX).plus(
                    Duration.between(LocalTime.MIN, wakeUpTime).plusMinutes(1)
                )
            } else {
                Duration.between(goBadTime, wakeUpTime)
            }

            val currentDate = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date()).toInt()

            Log.d("sleep-log-dto", "${duration.toMinutes().toInt() % 60}")

            val sleep =  async { sleepRepository.logSleep(
                SleepUploadDto(
                    duration.toHours().toInt(),
                    duration.toMinutes().toInt() % 60,
                    currentDate,
                    viewModelState.value.quality
                )
            )
            }.await()

            if (sleep == null) {
                viewModelState.update {
                    it.copy(
                        errors = listOf(ErrorMessage(null, R.string.error_load_data))
                    )
                }
            } else {
                val list = viewModelState.value.sleepRecords as MutableList
                list.add(0, sleep)
                viewModelState.update {
                    it.copy(
                        isFirstTime = false,
                        sleepRecords = list.toList()
                    )
                }
            }
        }
    }
}
