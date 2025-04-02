package com.rmp.ui.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun SettingsRoute(
    settingsViewModel: SettingsViewModel,
    onSignOutClick: () -> Unit
) {
    val uiState by settingsViewModel.uiState.collectAsStateWithLifecycle()
    SettingsRoute(
        uiState = uiState,
        onNameChange = settingsViewModel::updateName,
        onGenderChange = settingsViewModel::updateGender,
        onAgeChange = settingsViewModel::updateAge,
        onHeightChange = settingsViewModel::updateHeight,
        onWeightChange = settingsViewModel::updateWeight,
        onActivityLevelChange = settingsViewModel::updateActivityLevel,
        onGoalChange = settingsViewModel::updateGoal,
        onEmailChange = settingsViewModel::updateEmail,
        onPasswordChange = settingsViewModel::updatePassword,
        onSignOutClick = onSignOutClick
    )
}
@Composable
fun SettingsRoute(
    uiState: SettingsUiState,
    onNameChange: (String) -> Unit,
    onGenderChange: (Gender) -> Unit,
    onAgeChange: (String) -> Unit,
    onHeightChange: (String) ->Unit,
    onWeightChange: (String) -> Unit,
    onActivityLevelChange: (ActivityLevel) ->Unit,
    onGoalChange: (Goal) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onSignOutClick: () -> Unit
) {
    SettingsScreen(
        uiState = uiState,
        onNameChange = onNameChange,
        onGenderChange = onGenderChange,
        onAgeChange = onAgeChange,
        onHeightChange = onHeightChange,
        onWeightChange = onWeightChange,
        onActivityLevelChange = onActivityLevelChange,
        onGoalChange = onGoalChange,
        onEmailChange = onEmailChange,
        onPasswordChange = onPasswordChange,
        onSignOutClick = onSignOutClick
    )
}