package com.rmp.ui.sleep.history

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rmp.ui.sleep.SleepScreen

@Composable
fun SleepHistoryRoute(
    sleepHistoryViewModel: SleepHistoryViewModel
) {
    SleepHistoryScreen(sleepHistoryViewModel)
}
