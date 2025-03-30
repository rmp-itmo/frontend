package com.rmp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.rmp.R
import com.rmp.data.AppContainer
import com.rmp.data.ErrorMessage
import com.rmp.data.database.auth.AuthTokenDao
import com.rmp.data.repository.signup.DateDto
import com.rmp.data.repository.signup.UserRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * An internal representation of the Home route state, in a raw form
 */
data class HealthData(
    val calories: Pair<Int, Int>? = null,
    val water: Pair<Float, Float>? = null,
    val steps: Pair<Int, Int>? = null,
    val sleep: String? = null,
    val heartRate: String? = null,
    val nutrition: String? = null
)

data class HomeUiState(
    val isLoading: Boolean = false,
    val userName: String = "",
    val healthData: HealthData = HealthData(),
    val errors: List<ErrorMessage> = emptyList()
)

/**
 * ViewModel that handles the business logic of the Home screen
 */
class HomeViewModel(
    private val authTokenDao: AuthTokenDao,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState(isLoading = true))
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            fetchUserData()
            fetchUserStatSummary()
        }
    }

    private fun fetchUserStatSummary() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true)
            }

            val userStatSummaryData = async { userRepository.getMeStatSummary(DateDto(getCurrentDateFormatted())) }.await()

            if (userStatSummaryData == null) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errors = listOf(ErrorMessage(null, R.string.error_load_data))
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        healthData = HealthData(
                            calories = userStatSummaryData.caloriesCurrent.toInt() to userStatSummaryData.caloriesTarget.toInt(),
                            water = (userStatSummaryData.glassesOfWater / 4f).toFloat() to 2f,
                            steps = userStatSummaryData.stepsCurrent to userStatSummaryData.stepsTarget,
                            sleep = "%s ч %s мин".format(userStatSummaryData.sleepHours, userStatSummaryData.sleepMinutes),
                            heartRate = userStatSummaryData.heartRate.toString(),
                            nutrition = userStatSummaryData.caloriesCurrent.toInt().toString()
                        ),
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun fetchUserData() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true)
            }

            val userData = async { userRepository.getMe() }.await()

            if (userData == null) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errors = listOf(ErrorMessage(null, R.string.error_load_data))
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        userName = userData.name,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun clearTokens() {
        viewModelScope.launch {
            authTokenDao.clearTokens()
        }
    }

    private fun getCurrentDateFormatted(): String {
        val currentDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
        return currentDate.format(formatter)
    }

    companion object {
        fun factory(appContainer: AppContainer): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return HomeViewModel(appContainer.database.authTokenDao(), appContainer.userRepository) as T
            }
        }
    }
}