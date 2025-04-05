package com.rmp.ui.heart

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rmp.ui.hello.HelloScreen
import com.rmp.ui.hello.HelloUiState
import com.rmp.ui.hello.HelloViewModel

@Composable
fun HeartRoute(
    heartViewModel: HeartViewModel,
) {
    HeartScreen(heartViewModel)
}