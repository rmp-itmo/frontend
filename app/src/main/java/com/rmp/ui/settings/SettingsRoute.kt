package com.rmp.ui.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun SettingsRoute(
    settingsViewModel: SettingsViewModel
) {
    val uiState by settingsViewModel.uiState.collectAsStateWithLifecycle()

    SettingsScreen(
        uiState = uiState,
        onNameChange = settingsViewModel::updateName,
        onGenderChange = settingsViewModel::updateGender,
        onBirthDateChange = settingsViewModel::updateBirthDate,
        onHeightChange = settingsViewModel::updateHeight,
        onWeightChange = settingsViewModel::updateWeight,
        onActivityLevelChange = settingsViewModel::updateActivityLevel,
        onGoalChange = settingsViewModel::updateGoal,
        onEmailChange = settingsViewModel::updateEmail,
        onPasswordChange = settingsViewModel::updatePassword
    )
}
