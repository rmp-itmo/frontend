package com.rmp.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rmp.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun updateName(newName: String) {
        updateState(_uiState.value.copy(name = _uiState.value.name.copy(value = newName)))
    }

    fun updateGender(newGender: Gender) {
        updateState(_uiState.value.copy(gender = newGender))
    }

    fun updateAge(newAge: String) {
        val error = if (newAge.toIntOrNull() != null && newAge.toInt() in 7..150) null else R.string.invalid_height
        updateState(_uiState.value.copy(age = newAge))
    }

    fun updateHeight(newHeight: String) {
        val error = if (newHeight.toIntOrNull() != null && newHeight.toInt() in 50..250) null else R.string.invalid_height
        updateState(_uiState.value.copy(height = _uiState.value.height.copy(value = newHeight, error = error)))
    }

    fun updateWeight(newWeight: String) {
        updateState(_uiState.value.copy(weight = _uiState.value.weight.copy(value = newWeight)))
    }

    fun updateActivityLevel(newLevel: ActivityLevel) {
        updateState(_uiState.value.copy(activityLevel = newLevel))
    }

    fun updateGoal(newGoal: Goal) {
        updateState(_uiState.value.copy(goal = newGoal))
    }

    fun updateEmail(newEmail: String) {
        updateState(_uiState.value.copy(email = _uiState.value.email.copy(value = newEmail)))
    }

    fun updatePassword(newPassword: String) {
        updateState(_uiState.value.copy(password = _uiState.value.password.copy(value = newPassword)))
    }


    private fun generateNickname() {
        val name = _uiState.value.name.value.ifBlank { "User" }
        val id = (1000..9999).random()
        updateState(_uiState.value.copy(nickname = _uiState.value.nickname.copy(value = "$name#$id")))
    }

    private fun updateState(newState: SettingsUiState) {
        viewModelScope.launch {
            _uiState.emit(newState)
        }
    }
}

fun String.isValidEmail(): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
}
