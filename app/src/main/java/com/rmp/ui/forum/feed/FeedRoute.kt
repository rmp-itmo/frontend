package com.rmp.ui.forum.feed

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun FeedRoute(
    feedViewModel: FeedViewModel
) {
    val uiState by feedViewModel.uiState.collectAsStateWithLifecycle()

    FeedScreen(uiState, feedViewModel::fetchFeed, feedViewModel::createPost, feedViewModel::upvotePost)
}