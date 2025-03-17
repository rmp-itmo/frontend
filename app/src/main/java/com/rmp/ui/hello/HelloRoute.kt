package com.rmp.ui.hello

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun HelloRoute(
    helloViewModel: HelloViewModel,
    goToSignUp: () -> Unit,
    goToLogin: () -> Unit

) {
    val uiState by helloViewModel.uiState.collectAsStateWithLifecycle()

    HelloRoute(uiState, goToSignUp, goToLogin)
}

@Composable
fun HelloRoute(
    uiState: HelloUiState,
    goToSignUp: () -> Unit,
    goToLogin: () -> Unit
) {
    HelloScreen(uiState, goToSignUp, goToLogin)
}