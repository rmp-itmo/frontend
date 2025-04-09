package com.rmp.ui.nutrition

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun NutritionRoute(
    nutritionViewModel: NutritionViewModel,
    onBackClick: () -> Unit
) {
    val uiState by nutritionViewModel.uiState.collectAsStateWithLifecycle()
    var showHistory by remember { mutableStateOf(false) }
    var firstEntrance by remember { mutableStateOf(true) }

    if (showHistory) {
        NutritionHistoryScreen(
            viewModel = nutritionViewModel,
            uiState.caloriesTarget,
            onBackClick = { showHistory = false }
        )
    } else {
        NutritionScreen(
            uiState = uiState,
            onBackClick = onBackClick,
            onSwitchDishCheckbox = nutritionViewModel::switchDishCheckbox,
            onRemoveItem = nutritionViewModel::removeMenuItem,
            onDishAdd = nutritionViewModel::addMenuItem,
            onCalendarClick = { showHistory = true },
            firstEntrance = firstEntrance,
            onGenerateMenu = {
//                nutritionViewModel.generateMenu(uiState.caloriesTarget) //TODO change
                nutritionViewModel.generateMenu(2000f)
                firstEntrance = false
            }
        )
    }
}
