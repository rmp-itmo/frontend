package com.rmp.ui.sleep

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun SleepRoute(
    sleepViewModel: SleepViewModel,
    onBackClick: () -> Unit
) {
    val uiState by sleepViewModel.uiState.collectAsStateWithLifecycle()

    SleepRoute(uiState, onBackClick, sleepViewModel::onGoBadTimeChange,
        sleepViewModel::onWakeUpChange, sleepViewModel::onQualityChange)
}

@Composable
fun SleepRoute(
    uiState: SleepUiState,
    onBackClick: () -> Unit,
    onGoBadChange: (String) -> Unit,
    onWakeUpChange: (String) -> Unit,
    onQualityChange: (Int) -> Unit,
) {
    SleepScreen(uiState, onBackClick, onGoBadChange, onWakeUpChange, onQualityChange)
}