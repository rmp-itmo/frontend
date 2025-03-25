package com.rmp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.rmp.data.AppContainer
import com.rmp.data.database.AppDatabase
import com.rmp.data.database.auth.AuthTokenDao
import com.rmp.data.repository.signup.UserRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * An internal representation of the Home route state, in a raw form
 */
data class HomeUiState(
    val isLoading: Boolean = false,
    val userName: String = "",
)

/**
 * ViewModel that handles the business logic of the Home screen
 */
class HomeViewModel(
    private val authTokenDao: AuthTokenDao,
    private val userRepository: UserRepository
) : ViewModel() {

    // UI state exposed to the UI
    private val _uiState = MutableStateFlow(HomeUiState(isLoading = true))
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {}
    }

    fun fetchUserName() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true)
            }

            val userData = async { userRepository.getMe() }.await()

            _uiState.update {
                it.copy(userName = userData.name, isLoading = false)
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