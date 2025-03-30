package com.rmp.ui.water

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun WaterRoute(
    waterViewModel: WaterViewModel,
    onBackClick: () -> Unit
) {
    val uiState by waterViewModel.uiState.collectAsStateWithLifecycle()
    var showHistory by remember { mutableStateOf(false) }

    if (showHistory) {
        WaterHistoryScreen(
            viewModel = waterViewModel,
            onBackClick = { showHistory = false }
        )
    } else {
        WaterScreen(
            uiState = uiState,
            onBackClick = onBackClick,
            onAddWater = { amount -> waterViewModel.addWaterRecord(amount) },
            onCalendarClick = { showHistory = true }
        )
    }
}