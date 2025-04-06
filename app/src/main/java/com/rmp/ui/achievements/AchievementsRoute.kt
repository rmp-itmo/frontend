package com.rmp.ui.achievements

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun AchievementsRoute(
    achievementsViewModel: AchievementsViewModel
) {
    val uiState  by achievementsViewModel.uiState.collectAsStateWithLifecycle()

    AchievementsScreen(uiState, achievementsViewModel::fetchAchievements, achievementsViewModel::shareAchievement)
}