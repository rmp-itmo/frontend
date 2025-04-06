package com.rmp.ui.forum.profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun ProfileRoute(
    profileViewModel: ProfileViewModel
) {
    val uiState by profileViewModel.uiState.collectAsStateWithLifecycle()

    ProfileScreen(uiState, profileViewModel::fetchProfile, profileViewModel::subscribe, profileViewModel::upvotePost)
}