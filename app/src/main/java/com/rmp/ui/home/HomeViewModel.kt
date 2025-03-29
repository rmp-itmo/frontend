package com.rmp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.rmp.R
import com.rmp.data.AppContainer
import com.rmp.data.ErrorMessage
import com.rmp.data.database.auth.AuthTokenDao
import com.rmp.data.repository.signup.UserRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * An internal representation of the Home route state, in a raw form
 */
data class HealthData(
    val calories: Pair<Int, Int> = 1800 to 2000,
    val water: Pair<Float, Float> = 1.2f to 2f,
    val steps: Pair<Int, Int> = 4300 to 8000,
    val sleep: String = "5 - 32 минуты",
    val nutrition: String = "1678 мол",
    val workouts: String = "Достижения"
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
        }
    }

    fun fetchUserData() {
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
                        healthData = HealthData(), // Здесь можно загружать реальные данные
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

    companion object {
        fun factory(appContainer: AppContainer): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return HomeViewModel(appContainer.database.authTokenDao(), appContainer.userRepository) as T
            }
        }
    }
}