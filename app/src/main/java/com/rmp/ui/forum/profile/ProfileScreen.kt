package com.rmp.ui.forum.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.rmp.ui.LocalNavController
import com.rmp.ui.ProfileNavigator
import com.rmp.ui.RmpDestinations
import com.rmp.ui.components.AppScreen
import com.rmp.ui.components.PostList
//import com.rmp.ui.components.RefreshedAppScreen

@Composable
fun ProfileScreen(
    uiState: ProfileUiState,
    fetchProfile: () -> Unit,
    onSubscribe: () -> Unit,
    onUpvote: (Long, Boolean) -> Unit
) {
    val navigator = LocalNavController.current

    AppScreen(
        leftComposable = {
            OutlinedButton(onClick = {
                navigator.popBackStack()
            }) { Text("Prev") }
        },
//        swipeRefreshState = rememberSwipeRefreshState(isRefreshing = uiState.isLoading),
//        onRefresh = fetchProfile,
    ) {
        if (uiState.profile == null) {
            return@AppScreen
        }
        Column {
            Text("Profile of ${uiState.profile.nickName}")
            Text("Subscribers count: ${uiState.profile.subsNum}")
            if (!uiState.isMe)
                FilledTonalButton(onClick = {
                    onSubscribe()
                }) {
                    Text(if (uiState.profile.isSubscribed) "Unsubscribe" else "Subscribe")
                }

            PostList(
                posts = uiState.profile.posts,
                upvoted = onUpvote,
                authorClicked = {
                    if (!uiState.isMe && uiState.profile.id != it)
                        navigator.navigate(ProfileNavigator(userId = it))
                }
            )
        }
    }
}