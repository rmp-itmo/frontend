package com.rmp.ui.heart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rmp.data.AppContainer
import com.rmp.ui.components.GraphViewModel
import com.rmp.data.repository.heart.HeartRepository
import com.rmp.ui.components.ViewPeriod


class HeartViewModel(
    private val heartRepository: HeartRepository
) : GraphViewModel() {

    init {
        loadHeartData(_uiState.value.selectedPeriod)
    }

    override fun updatePeriod(newPeriod: ViewPeriod) {
        _uiState.value = _uiState.value.copy(selectedPeriod = newPeriod)
        loadHeartData(newPeriod)
    }

    private fun loadHeartData(currentPeriod: ViewPeriod) {
        loadData(currentPeriod) { config -> heartRepository.getGraphHeart(config) }
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