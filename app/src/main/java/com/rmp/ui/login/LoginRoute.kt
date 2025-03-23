package com.rmp.ui.login


import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun LoginRoute(
    loginViewModel: LoginViewModel,
    onLoginSuccess: () -> Unit,
    onBackClick: () -> Unit
) {
    val uiState by loginViewModel.uiState.collectAsStateWithLifecycle()

    loginViewModel.onLoginSuccess = onLoginSuccess

    LoginRoute(uiState, loginViewModel::onEmailChange, loginViewModel::onPasswordChange, loginViewModel::onLoginClick, onBackClick)
}

@Composable
fun LoginRoute(
    uiState: LoginUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    onBackClick: () -> Unit,
) {
    LoginScreen(uiState, onEmailChange, onPasswordChange, onLoginClick, onBackClick)
}