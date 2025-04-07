package com.rmp.ui.settings

import android.util.Log
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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

private data class SettingsViewModelState(
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
) : SettingsUiState

class SettingsViewModel(private val container: AppContainer) : ViewModel() {
    private val _state = MutableStateFlow<SettingsViewModelState>(SettingsViewModelState())
    private var _originalSettings: UserSettingsDto? = null
    val uiState: StateFlow<SettingsUiState> = _state.asStateFlow()

    fun clearErrors() {
        _state.update {
            it.copy(
                errorMessage = null
            )
        }
    }

    fun signOutClick() {
        viewModelScope.launch {
            container.database.authTokenDao().clearTokens()
        }
    }

    fun loadSettings() {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true
                )
            }

            val settings = container.settingsRepository.getSettings() ?: run {
                _state.update {
                    it.copy(
                        errorMessage = "Ошибка загрузки"
                    )
                }
                return@launch
            }

            _originalSettings = settings

            _state.update {
                it.copy (
                    name = it.name.copy(value = settings.name),
                    gender = if (settings.isMale) Gender.MALE else Gender.FEMALE,
                    age = settings.age.toString(),
                    height = it.height.copy(value = "${(settings.height * 10).roundToInt() / 10.0}"),
                    weight = it.weight.copy(value = "${(settings.weight * 10).roundToInt() / 10.0}"),
                    activityLevel = ActivityLevel.valueOf(settings.activityType),
                    goal = Goal.valueOf(settings.goalType),
                    email = it.email.copy(value = settings.email),
                    nickname = it.nickname.copy(value = settings.nickName),
                    isLoading = false
                )
            }
        }

    }
    init {
        viewModelScope.launch {
            loadSettings()
        }
    }

    fun saveSettings() {
        viewModelScope.launch {
            _state.update {
                it.copy(isLoading = true)
            }
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

                Log.d("response", response.toString())

                if (response.second != null) {
                    if (response.second!!.code != 200) {
                        _state.update {
                            it.copy(
                                errorMessage = response.second!!.message
                            )
                        }
                    }
                    _state.update {
                        it.copy(
                            isLoading = false
                        )
                    }
                } else {
                    if (response.first != null) {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = "Settings successfully changed"
                            )
                        }
                    } else {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = "Error on saving messages"
                            )
                        }
                    }

                }
            } catch (e: Exception) {
                Log.d("exception", e.message ?: "")
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Error on saving settings: ${e.message}"
                    )
                }
            }
        }
    }

    fun updateName(newName: String) {
        _state.update {
            it.copy(
                name = it.name.copy(value = newName)
            )
        }
    }

    fun updateGender(newGender: Gender) {
        _state.update {
            it.copy(
                gender = newGender
            )
        }
    }

    fun updateAge(newAge: String) {
        _state.update {
            it.copy(
                age = newAge
            )
        }
    }

    fun updateHeight(newHeight: String) {
        val error = if (newHeight.toFloatOrNull() != null && newHeight.toFloat() in 50.0..250.0) null else R.string.invalid_height
        _state.update {
            it.copy(
                height = it.height.copy(value = newHeight, error = error)
            )
        }
    }

    fun updateWeight(newWeight: String) {
        _state.update {
            it.copy(
                weight = it.weight.copy(value = newWeight)
            )
        }
    }

    fun updateActivityLevel(activityLevel: ActivityLevel) {
        _state.update {
            it.copy(
                activityLevel = activityLevel
            )
        }
    }

    fun updateGoal(newGoal: Goal) {
        _state.update {
            it.copy(
                goal = newGoal
            )
        }
    }


    fun updateEmail(newEmail: String) {
        _state.update {
            it.copy(
                email = it.email.copy(
                    value = newEmail,
                    error = if (newEmail.isValidEmail()) null else R.string.invalid_email)
            )
        }
    }

    fun updatePassword(newPassword: String) {
        _state.update {
            it.copy(
                password = it.password.copy(value = newPassword)
            )
        }
    }

    fun updateNickName(newNickName: String) {
        _state.update {
            it.copy(
                nickname = it.nickname.copy(value = newNickName)
            )
        }
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