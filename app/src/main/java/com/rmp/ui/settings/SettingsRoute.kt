package com.rmp.ui.settings

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun SettingsRoute(
    settingsViewModel: SettingsViewModel,
    onSignOutClick: () -> Unit
) {
    val uiState by settingsViewModel.uiState.collectAsStateWithLifecycle()
    var showSaveDialog by remember { mutableStateOf(false) }


    SettingsScreen(
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
        onNickNameChange = settingsViewModel::updateNickName,
        onSaveClick = { showSaveDialog = true },
        onSignOutClick = onSignOutClick,
        onRefresh = settingsViewModel::loadSettings,
        clearError = settingsViewModel::clearErrors
    )

    if (showSaveDialog) {
        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            title = { Text("Сохранить изменения?") },
            text = { Text("Вы уверены, что хотите сохранить изменения?") },
            confirmButton = {
                Button(
                    onClick = {
                        settingsViewModel.saveSettings()
                        showSaveDialog = false
                    }
                ) {
                    Text("Сохранить")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showSaveDialog = false }
                ) {
                    Text("Отмена")
                }
            }
        )
    }
}