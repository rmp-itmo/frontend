package com.rmp.ui.trainings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun TrainingsRoute(
    trainingsViewModel: TrainingsViewModel
) {
    val uiState by trainingsViewModel.uiState.collectAsStateWithLifecycle()

    TrainingsScreen(
        uiState,
        trainingsViewModel::fetchLog,
        trainingsViewModel::logTraining,
        trainingsViewModel::updateStepsTarget,
        trainingsViewModel::typeIdByName,
        trainingsViewModel::intensityIdByName,
        trainingsViewModel::typeNameById,
        trainingsViewModel::intensityNameById
    )
}