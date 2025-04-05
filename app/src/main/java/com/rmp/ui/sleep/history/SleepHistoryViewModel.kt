package com.rmp.ui.sleep.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rmp.data.AppContainer
import com.rmp.data.repository.sleep.SleepRepository
import com.rmp.ui.components.GraphViewModel
import com.rmp.ui.components.ViewPeriod


class SleepHistoryViewModel(
    private val sleepRepository: SleepRepository
) : GraphViewModel() {

    init {
        loadSleepData(_uiState.value.selectedPeriod)
    }

    override fun updatePeriod(newPeriod: ViewPeriod) {
        _uiState.value = _uiState.value.copy(selectedPeriod = newPeriod)
        loadSleepData(newPeriod)
    }

    private fun loadSleepData(currentPeriod: ViewPeriod) {
        loadData(currentPeriod) { config -> sleepRepository.getGraphSleep(config) }
    }

    companion object {
        fun factory(appContainer: AppContainer): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return SleepHistoryViewModel(appContainer.sleepRepository) as T
            }
        }
    }
}