package com.rmp.ui.sleep

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun SleepRoute(
    sleepViewModel: SleepViewModel,
    onGraphicClick: () -> Unit
) {
    val uiState by sleepViewModel.uiState.collectAsStateWithLifecycle()

    SleepRoute(uiState, sleepViewModel::onQualityChange,
        sleepViewModel::saveSleep, onGraphicClick)
}

@Composable
fun SleepRoute(
    uiState: SleepUiState,
    onQualityChange: (Int) -> Unit,
    onSaveSleepButton: (SleepLogDto) -> Unit,
    onGraphicClick: () -> Unit
) {
    SleepScreen(uiState, onQualityChange, onGraphicClick, onSaveSleepButton)
}