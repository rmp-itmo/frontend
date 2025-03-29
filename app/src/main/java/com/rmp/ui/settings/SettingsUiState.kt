package com.rmp.ui.settings

data class SettingsField(
    val value: String = "",
    val error: Int? = null,
    val hint: String = ""
)

enum class Gender {
    MALE, FEMALE
}

enum class ActivityLevel {
    LOW, MODERATE, HIGH
}

enum class Goal {
    LOSE_WEIGHT, MAINTAIN_WEIGHT, GAIN_WEIGHT
}

data class SettingsUiState(
    val name: SettingsField = SettingsField(hint = "Введите имя"),
    val gender: Gender = Gender.MALE,  // Используем enum Gender
    val birthDate: String = "",        // Формат: "дд.мм.гггг"
    val height: SettingsField = SettingsField(hint = "Введите рост (см)"),
    val weight: SettingsField = SettingsField(hint = "Введите вес (кг)"),
    val activityLevel: ActivityLevel = ActivityLevel.MODERATE,  // Используем enum ActivityLevel
    val goal: Goal = Goal.MAINTAIN_WEIGHT,  // Используем enum Goal
    val email: SettingsField = SettingsField(hint = "Введите email"),
    val password: SettingsField = SettingsField(hint = "Введите пароль"),
    val nickname: String = ""  // Никнейм по умолчанию: Имя#id (генерируется)
)
