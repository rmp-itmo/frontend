package com.rmp.ui.sleep

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.rmp.R
import com.rmp.data.repository.sleep.SleepRepository
import com.rmp.data.repository.sleep.SleepResponseDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch



interface SleepUiState {
    val goBadTime: String
    val wakeUpTime: String
    val isFirstTime: Boolean
    val dailyGoal: Float
    val sleepRecords: List<SleepResponseDto>
    val errorMessage: String?
    val timeError: Int?
    val quality: Int
}


private class SleepViewModelState(
    override val goBadTime: String = "00:00",
    override val wakeUpTime: String = "08:00",
    override val isFirstTime: Boolean = true,
    override val dailyGoal: Float = 8f,
    override val sleepRecords: List<SleepResponseDto> = emptyList(),
    override val errorMessage: String? = null,
    override val timeError: Int? = null,
    override val quality: Int = 0

) : SleepUiState {
    fun toUiState(): SleepUiState = this

    fun copy(
        goBadTime: String = this.goBadTime,
        wakeUpTime: String = this.wakeUpTime,
        isFirstTime: Boolean = this.isFirstTime,
        dailyGoal: Float = this.dailyGoal,
        sleepRecords: List<SleepResponseDto> = this.sleepRecords,
        errorMessage: String? = this.errorMessage,
        timeError: Int? = this.timeError,
        quality: Int = this.quality
    ) = SleepViewModelState(goBadTime, wakeUpTime, isFirstTime, dailyGoal,
        sleepRecords, errorMessage, timeError, quality)
}


class SleepViewModel(private val sleepRepository: SleepRepository): ViewModel() {
    private val viewModelState = MutableStateFlow(
        SleepViewModelState()
    )

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


    private fun checkTime(time: String): Boolean {
        val split = time.split(":")
        return if (time.length == 4 && split.size == 2) {
            try {
                split.first().toInt()
                split[1].toInt()
            } catch (e: Exception){
                return false
            }
            return split.first().length == 2 && split.first().toInt() < 24 && split[1].toInt() < 60
        }else {
            false
        }
    }

    fun onGoBadTimeChange(newTime: String) {
        val timeError = if (checkTime(newTime)) null else R.string.error_time_provided
        updateState(viewModelState.value.copy(goBadTime = newTime, timeError = timeError))
    }

    fun onWakeUpChange(newTime: String) {
        val timeError = if (checkTime(newTime)) null else R.string.error_time_provided
        updateState(viewModelState.value.copy(wakeUpTime = newTime, timeError = timeError))
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
            errorMessage = newState.errorMessage,
            timeError = newState.timeError,
            quality = newState.quality
        )
    }
}