package com.rmp.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.rmp.R
import com.rmp.data.AppContainer
import com.rmp.data.repository.settings.UpdateSettingsRequest
import com.rmp.data.repository.settings.UserSettingsDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private class SettingsViewModelState(
    override val name: SettingsField = SettingsField(hint = "Введите имя"),
    override val gender: Gender = Gender.MALE,
    override val age: String = "",
    override val height: SettingsField = SettingsField(hint = "Введите рост (см)"),
    override val weight: SettingsField = SettingsField(hint = "Введите вес (кг)"),
    override val activityLevel: ActivityLevel = ActivityLevel.Medium,
    override val goal: Goal = Goal.Maintain,
    override val email: SettingsField = SettingsField(hint = "Введите email"),
    override val password: SettingsField = SettingsField(hint = "Введите пароль"),
    override val nickname: SettingsField = SettingsField(hint = "Придумайте себе никнейм"),
    override val isLoading: Boolean = false,
    override val errorMessage: String? = null
) : SettingsUiState {
    fun copy(
        name: SettingsField = this.name,
        gender: Gender = this.gender,
        age: String = this.age,
        height: SettingsField = this.height,
        weight: SettingsField = this.weight,
        activityLevel: ActivityLevel = this.activityLevel,
        goal: Goal = this.goal,
        email: SettingsField = this.email,
        password: SettingsField = this.password,
        nickname: SettingsField = this.nickname,
        isLoading: Boolean = this.isLoading,
        errorMessage: String? = this.errorMessage
    ) = SettingsViewModelState(
        name = name.copy(hint = this.name.hint),
        gender = gender,
        age = age,
        height = height.copy(hint = this.height.hint),
        weight = weight.copy(hint = this.weight.hint),
        activityLevel = activityLevel,
        goal = goal,
        email = email.copy(hint = this.email.hint),
        password = password.copy(hint = this.password.hint),
        nickname = nickname.copy(hint = this.nickname.hint),
        isLoading = isLoading,
        errorMessage = errorMessage
    )
}

class SettingsViewModel(private val container: AppContainer) : ViewModel() {
    private val _state = MutableStateFlow<SettingsViewModelState>(SettingsViewModelState())
    private var _originalSettings: UserSettingsDto? = null
    val uiState: StateFlow<SettingsUiState> = _state.asStateFlow()

    private suspend fun loadSettings() {
        updateState(_state.value.copy(isLoading = true))
        try {
            val settings = container.settingsRepository.getSettings()
            _originalSettings = settings
            settings?.let {
                updateState(
                    _state.value.copy(
                        name = SettingsField(value = it.name),
                        gender = if (it.isMale) Gender.MALE else Gender.FEMALE,
                        age = it.age.toString(),
                        height = SettingsField(value = it.height.toString()),
                        weight = SettingsField(value = it.weight.toString()),
                        activityLevel = ActivityLevel.valueOf(it.activityType),
                        goal = Goal.valueOf(it.goalType),
                        email = SettingsField(value = it.email),
                        password = SettingsField(value = ""),
                        nickname = SettingsField(value = it.nickName),
                        isLoading = false
                    )
                )
            }
        } catch (e: Exception) {
            updateState(
                _state.value.copy(
                    isLoading = false,
                    errorMessage = "Ошибка загрузки настроек"
                )
            )
        }
    }
    init {
        viewModelScope.launch {
            loadSettings()
        }
    }

    fun saveSettings() {
        viewModelScope.launch {
            updateState(_state.value.copy(isLoading = true))
            try {
                var requestBuilder = UpdateSettingsRequest(date = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date()).toInt())
                if (_state.value.name.value != _originalSettings?.name) {
                    requestBuilder = requestBuilder.copy(name = _state.value.name.value)
                }
                if (_state.value.email.value != _originalSettings?.email) {
                    requestBuilder = requestBuilder.copy(email = _state.value.email.value)
                }
                if ((_state.value.gender == Gender.MALE) != _originalSettings?.isMale) {
                    requestBuilder = requestBuilder.copy(isMale = _state.value.gender == Gender.MALE)
                }
                if (_state.value.age.toIntOrNull() != _originalSettings?.age) {
                    requestBuilder = requestBuilder.copy(age = _state.value.age.toIntOrNull())
                }
                if (_state.value.height.value.toDoubleOrNull() != _originalSettings?.height) {
                    requestBuilder = requestBuilder.copy(height = _state.value.height.value.toDoubleOrNull())
                }
                if (_state.value.weight.value.toDoubleOrNull() != _originalSettings?.weight) {
                    requestBuilder = requestBuilder.copy(weight = _state.value.weight.value.toDoubleOrNull())
                }
                if (_state.value.activityLevel.name != _originalSettings?.activityType) {
                    requestBuilder = requestBuilder.copy(activityType = _state.value.activityLevel.name)
                }
                if (_state.value.goal.name != _originalSettings?.goalType) {
                    requestBuilder = requestBuilder.copy(goalType = _state.value.goal.name)
                }
                if (_state.value.nickname.value != _originalSettings?.nickName) {
                    requestBuilder = requestBuilder.copy(nickName = _state.value.nickname.value)
                }
                val request = if (_state.value.password.value.isNotEmpty()) {
                    requestBuilder.copy(password = _state.value.password.value)
                } else {
                    requestBuilder
                }
                val response = container.settingsRepository.updateSettings(request)
                if (response != null) {
                    updateState(_state.value.copy(
                        isLoading = false,
                        errorMessage = null
                    ))
                } else {
                    updateState(_state.value.copy(
                        isLoading = false,
                        errorMessage = "Failed to save settings"
                    ))
                }
            } catch (e: Exception) {
                updateState(_state.value.copy(
                    isLoading = false,
                    errorMessage = "Error saving settings: ${e.message}"
                ))
            }
        }
    }


    fun updateName(newName: String) {
        updateState(_state.value.copy(name = _state.value.name.copy(value = newName)))
    }

    fun updateGender(newGender: Gender) {
        updateState(_state.value.copy(gender = newGender))
    }

    fun updateAge(newAge: String) {
        updateState(_state.value.copy(age = newAge))
    }

    fun updateHeight(newHeight: String) {
        val error = if (newHeight.toFloatOrNull() != null && newHeight.toFloat() in 50.0..250.0) null else R.string.invalid_height
        updateState(_state.value.copy(height = _state.value.height.copy(value = newHeight, error = error)))
    }

    fun updateWeight(newWeight: String) {
        updateState(_state.value.copy(weight = _state.value.weight.copy(value = newWeight)))
    }

    fun updateActivityLevel(newLevel: ActivityLevel) {
        updateState(_state.value.copy(activityLevel = newLevel))
    }

    fun updateGoal(newGoal: Goal) {
        updateState(_state.value.copy(goal = newGoal))
    }

    fun updateEmail(newEmail: String) {
        updateState(_state.value.copy(email = _state.value.email.copy(value = newEmail)))
    }

    fun updatePassword(newPassword: String) {
        updateState(_state.value.copy(password = _state.value.password.copy(value = newPassword)))
    }

    fun updateNickName(newNickName: String) {
        updateState(_state.value.copy(nickname = _state.value.nickname.copy(value = newNickName)))
    }

    private fun generateNickname() {
        val name = _state.value.name.value.ifBlank { "User" }
        val id = (1000..9999).random()
        updateState(_state.value.copy(
            nickname = _state.value.nickname.copy(value = "$name#$id")
        ))
    }

    private fun updateState(newState: SettingsViewModelState) {
        _state.value = newState
    }

    companion object {
        fun factory(appContainer: AppContainer): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return SettingsViewModel(appContainer) as T
                }
            }
    }
}

fun String.isValidEmail(): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
}