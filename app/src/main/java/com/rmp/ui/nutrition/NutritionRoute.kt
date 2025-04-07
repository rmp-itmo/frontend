package com.rmp.ui.nutrition

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun NutritionRoute(
    nutritionViewModel: NutritionViewModel
) {
    val uiState by nutritionViewModel.uiState.collectAsStateWithLifecycle()
    var showHistory by remember { mutableStateOf(false) }

    if (showHistory) {
        NutritionHistoryScreen(
            uiState = uiState,
            fetchHistory = nutritionViewModel::fetchHistory,
            uiState.history?.caloriesTarget ?: 0f,
            onBackClick = { showHistory = false }
        )
    } else {
        NutritionScreen(
            uiState = uiState,
            onSwitchDishCheckbox = nutritionViewModel::switchCheckBox,
            onRemoveItem = nutritionViewModel::removeMenuItem,
            onCalendarClick = { showHistory = true },
            onGenerateMenu = nutritionViewModel::generateMenu
        )
    }
}
