package com.rmp.ui.signup

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun SignupRoute(
    signupViewModel: SignupViewModel,
    prevState: () -> Unit,
    nextState: () -> Unit,
    setWelcome: (String, String, String) -> Unit,
    setParams: (String, String, ActivityLevel) -> Unit,
    setTarget: (WeightTarget) -> Unit,
    setLoginStep: (String, String) -> Unit

) {
    val uiState by signupViewModel.uiState.collectAsStateWithLifecycle()

    SignupRoute(uiState, prevState, nextState, setWelcome, setParams, setTarget, setLoginStep)
}

@Composable
fun SignupRoute(
    uiState: SignupUiState,
    prevState: () -> Unit,
    nextState: () -> Unit,
    setWelcome: (String, String, String) -> Unit,
    setParams: (String, String, ActivityLevel) -> Unit,
    setTarget: (WeightTarget) -> Unit,
    setLoginStep: (String, String) -> Unit
) {
    SignupScreen(uiState, prevState, nextState, setWelcome, setParams, setTarget, setLoginStep)
}