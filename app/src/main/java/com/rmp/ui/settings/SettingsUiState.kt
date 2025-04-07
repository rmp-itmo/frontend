package com.rmp.ui.settings

// Общие модели данных
data class SettingsField(
    val value: String = "",
    val error: Int? = null,
    val hint: String = ""
)

enum class Gender {
    MALE, FEMALE
}

enum class ActivityLevel {
    Low, Medium, High
}

enum class Goal {
    Lose, Maintain, Gain
}

// Интерфейс состояния (контракт для UI)
interface SettingsUiState {
    val name: SettingsField
    val gender: Gender
    val age: String
    val height: SettingsField
    val weight: SettingsField
    val activityLevel: ActivityLevel
    val goal: Goal
    val email: SettingsField
    val password: SettingsField
    val nickname: SettingsField
    val isLoading: Boolean
    val errorMessage: String?
}