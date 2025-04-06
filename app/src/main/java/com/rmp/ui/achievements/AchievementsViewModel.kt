package com.rmp.ui.achievements

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.rmp.R
import com.rmp.data.AppContainer
import com.rmp.data.ErrorMessage
import com.rmp.data.repository.achievements.AchievementsDto
import com.rmp.data.repository.achievements.AchievementsRepository
import com.rmp.data.repository.achievements.ShareAchievementDto
import com.rmp.ui.forum.profile.ProfileUiState
import com.rmp.ui.nutrition.NutritionViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ShareDescription(
    val type: Int,
    val show: Boolean = false
)

data class AchievementsUiState(
    val isLoading: Boolean = false,
    val shared: ShareDescription = ShareDescription(0, false),
    val achievements: AchievementsDto? = null,
    val errors: List<ErrorMessage> = emptyList()
)

class AchievementsViewModel(
    private val achievementsRepository: AchievementsRepository
): ViewModel() {
    private val _uiState = MutableStateFlow(AchievementsUiState())
    val uiState: StateFlow<AchievementsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            fetchAchievements()
        }
    }

    companion object {
        fun factory(appContainer: AppContainer): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return AchievementsViewModel(appContainer.achievementsRepository) as T
                }
            }
    }

    fun fetchAchievements() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true)
            }

            val loaded = async { achievementsRepository.fetchAchievements() }.await() ?: run {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errors = listOf(ErrorMessage(null, R.string.error_load_data))
                    )
                }
                return@launch
            }

            _uiState.update {
                it.copy(
                    isLoading = false,
                    achievements = loaded,
                    errors = emptyList()
                )
            }
        }
    }

    fun shareAchievement(ctx: Context, shareAchievementDto: ShareAchievementDto) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true)
            }

            async { achievementsRepository.shareAchievement(shareAchievementDto) }.await() ?: run {
                _uiState.update {
                    //TODO: Change error to "Failed save" data
                    it.copy(
                        isLoading = false,
                        errors = listOf(ErrorMessage(null, R.string.error_load_data))
                    )
                }
                return@launch
            }

            Toast
                .makeText(ctx, "Пост с вашими достижениями опубликован!", Toast.LENGTH_SHORT)
                .show()

            _uiState.update {
                it.copy(
                    isLoading = false,
                    errors = emptyList()
                )
            }
        }
    }
}