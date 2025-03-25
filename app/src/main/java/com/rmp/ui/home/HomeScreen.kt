package com.rmp.ui.home

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.rmp.R
import com.rmp.ui.components.AccentButton

@Composable
fun HomeScreen(
    uiState: HomeUiState,
    openDrawer: () -> Unit,
    onSignOutClick: () -> Unit,
    clearTokens: () -> Unit
) {
    AccentButton(
        stringResource(R.string.sign_out),
    ) {
        onSignOutClick()
        clearTokens()
    }
}