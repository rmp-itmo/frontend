package com.rmp.ui.sleep

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun SleepRoute(
    sleepViewModel: SleepViewModel
) {
    val uiState by sleepViewModel.uiState.collectAsStateWithLifecycle()

    SleepRoute(uiState, sleepViewModel::onGoBadTimeChange,
        sleepViewModel::onWakeUpChange, sleepViewModel::onQualityChange,
        sleepViewModel::saveSleep)
}

@Composable
fun SleepRoute(
    uiState: SleepUiState,
    onGoBadChange: (String) -> Unit,
    onWakeUpChange: (String) -> Unit,
    onQualityChange: (Int) -> Unit,
    onSaveSleepButton: () -> Unit
) {
    SleepScreen(uiState, onGoBadChange, onWakeUpChange, onQualityChange, {}, onSaveSleepButton)
}