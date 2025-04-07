package com.rmp.ui.trainings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rmp.R
import com.rmp.data.AppContainer
import com.rmp.data.ErrorMessage
import com.rmp.data.getCurrentYearPlusMonth
import com.rmp.data.repository.training.SelectorItem
import com.rmp.data.repository.training.SetStepsTargetDto
import com.rmp.data.repository.training.TrainingFilterDto
import com.rmp.data.repository.training.TrainingListDto
import com.rmp.data.repository.training.TrainingLogDto
import com.rmp.data.repository.training.TrainingRepository
import com.rmp.ui.achievements.AchievementsViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TrainingsUiState(
    val curYear: Int,
    val curMonth: String,
    val types: List<SelectorItem> = emptyList(),
    val intensities: List<SelectorItem> = emptyList(),
    val stepsTarget: Int = 0,
    val stepsCurrent: Int = 0,
    val trainings: Map<String, List<TrainingListDto.Training>> = mapOf(),
    val isLoadings: Boolean = false,
    val errors: List<ErrorMessage> = emptyList()
) {
    fun todayTrainings(today: Int) =
        trainings.filter { (key, value) ->
            key == today.toString()
        }
}


class TrainingsViewModel(
    val curYear: Int,
    val curMonth: String,
    private val trainingRepository: TrainingRepository
): ViewModel() {
    private val _uiState = MutableStateFlow(TrainingsUiState(curYear, curMonth))
    private lateinit var _intensity: Map<String, Int>
    private lateinit var _type: Map<String, Int>
    val uiState: StateFlow<TrainingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            fetchLog(fetchSelectors = true)
        }
    }

    companion object {
        fun factory(appContainer: AppContainer): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    val (year, month) = getCurrentYearPlusMonth()
                    return TrainingsViewModel(year, month, appContainer.trainingsRepository) as T
                }
            }
    }

    fun fetchLog(year: Int? = null, month: String? = null, fetchSelectors: Boolean = false) {
        viewModelScope.launch {
            val curYear = year ?: _uiState.value.curYear
            val curMonth = month ?: _uiState.value.curMonth

            val date = "$curYear$curMonth".toInt()

            _uiState.update {
                it.copy(isLoadings = true)
            }

            val fetchData = async { trainingRepository.fetchTrainings(TrainingFilterDto(date)) }.await()

            val (types, intensities) = if (fetchSelectors) {
                val types = async { trainingRepository.fetchTypes() }.await()
                val intensities = async { trainingRepository.fetchIntensities() }.await()
                types?.types to intensities?.intensities
            } else {
                _uiState.value.types to _uiState.value.intensities
            }

            if (types == null || intensities == null || fetchData == null) {
                _uiState.update {
                    it.copy(
                        isLoadings = false,
                        errors = listOf(ErrorMessage(null, R.string.error_load_data))
                    )
                }
                return@launch
            }

            _intensity = intensities.groupBy({ it.name }) { it.id }.map { (k, v) -> k to v.first() }.toMap()
            _type = types.groupBy({ it.name }) { it.id }.map { (k, v) -> k to v.first() }.toMap()

            _uiState.update {
                it.copy(
                    isLoadings = false,
                    errors = emptyList(),
                    curYear = curYear,
                    curMonth = curMonth,
                    trainings = (it.trainings + fetchData.trainings).toSortedMap(),
                    stepsCurrent = fetchData.stepsCurrent,
                    stepsTarget = fetchData.stepsTarget,
                    intensities = intensities,
                    types = types
                )
            }

        }
    }

    fun logTraining(trainingLogDto: TrainingLogDto) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoadings = true
                )
            }

            async { trainingRepository.logTraining(trainingLogDto) }.await() ?: run {
                _uiState.update {
                    it.copy(
                        isLoadings = false,
                        errors = listOf(ErrorMessage(null, R.string.error_load_data))
                    )
                }
                return@launch
            }

            fetchLog(this@TrainingsViewModel.curYear, this@TrainingsViewModel.curMonth)

            _uiState.update {
                it.copy(
                    isLoadings = false,
                    errors = emptyList()
                )
            }
        }
    }

    fun updateStepsTarget(setStepsTargetDto: SetStepsTargetDto) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoadings = true
                )
            }

            async { trainingRepository.updateStepTarget(setStepsTargetDto) }.await() ?: run {
                _uiState.update {
                    it.copy(
                        isLoadings = false,
                        errors = listOf(ErrorMessage(null, R.string.error_load_data))
                    )
                }
                return@launch
            }

            _uiState.update {
                it.copy(
                    isLoadings = false,
                    stepsTarget = setStepsTargetDto.steps,
                    errors = emptyList()
                )
            }
        }
    }

    fun intensityIdByName(name: String) =
        _intensity[name]!!

    fun typeIdByName(name: String) =
        _type[name]!!

    fun intensityNameById(id: Int) =
        _intensity.firstNotNullOf { (k, v) -> if (v == id) k else null }

    fun typeNameById(id: Int) =
        _type.firstNotNullOf { (k, v) -> if (v == id) k else null }
}