package com.rmp.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.rmp.R
import com.rmp.ui.components.AccentButton
import com.rmp.ui.components.AppScreen
import com.rmp.ui.components.SpinningCirclesLoader

@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onSignOutClick: () -> Unit,
    clearTokens: () -> Unit
) {
    AppScreen {
        if (uiState.isLoading)
            SpinningCirclesLoader()
        else {

            if (uiState.errors.isNotEmpty()) {
                Text("Ошибка")
                return@AppScreen
            }

            Column {
                Text("Привет! ${uiState.userName}")
                AccentButton(
                    stringResource(R.string.sign_out),
                ) {
                    onSignOutClick()
                    clearTokens()
                }
            }
        }
    }
}