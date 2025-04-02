package com.rmp.ui.nutrition

import android.util.Log //TODO временно, потом убрать
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
            uiState.dailyGoal,
            onBackClick = { showHistory = false }
        )
    } else {
        NutritionScreen(
            uiState = uiState,
            onBackClick = onBackClick,
            onSaveMenu = { Log.d("SaveMenu", "Сохранить меню!!!!!!") },
            onAddNutrition = { amount -> nutritionViewModel.addNutritionRecord(amount) },
            onCalendarClick = { showHistory = true },
            firstEntrance = firstEntrance,
            onGenerateMenu = {
                nutritionViewModel.generateMenu(2000)
                firstEntrance = false
            }
        )
    }
}
