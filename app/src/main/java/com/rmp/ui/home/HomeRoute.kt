package com.rmp.ui.home

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
) {
    val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()

    HomeRoute(
        uiState = uiState,
        onRefresh = homeViewModel::fetchUserStatSummary
    )
}

@Composable
fun HomeRoute(
    uiState: HomeUiState,
    onRefresh: () -> Unit,
) {
    HomeScreen(uiState, onRefresh)
}