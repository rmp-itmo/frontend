package com.rmp.ui.home

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle


/**
 * Displays the Home route.
 *
 * Note: AAC ViewModels don't work with Compose Previews currently.
 *
 * @param homeViewModel ViewModel that handles the business logic of this screen
 */
@Composable
fun HomeRoute(
    homeViewModel: HomeViewModel,
    onSignOutClick: () -> Unit
) {
    val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()
    val clearTokens = homeViewModel::clearTokens

    HomeRoute(
        uiState = uiState,
        signOutClick= onSignOutClick,
        clearTokens= clearTokens
    )
}

@Composable
fun HomeRoute(
    uiState: HomeUiState,
    signOutClick: () -> Unit,
    clearTokens: () -> Unit
) {
    HomeScreen(uiState, signOutClick, clearTokens)
}