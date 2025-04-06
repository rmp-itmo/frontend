package com.rmp.ui.forum.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.rmp.R
import com.rmp.ui.LocalNavController
import com.rmp.ui.ProfileNavigator
import com.rmp.ui.components.PostList
import com.rmp.ui.components.RefreshedAppScreen
import com.rmp.ui.components.buttons.BackToFeedButton
import java.util.Locale

@Composable
fun ProfileScreen(
    uiState: ProfileUiState,
    fetchProfile: () -> Unit,
    onSubscribe: () -> Unit,
    onUpvote: (Long, Boolean) -> Unit
) {
    val navigator = LocalNavController.current
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = uiState.isLoading)

    RefreshedAppScreen(
        leftComposable = { BackToFeedButton() },
        swipeRefreshState = swipeRefreshState,
        onRefresh = fetchProfile
    ) {
        if (uiState.profile == null) {
            return@RefreshedAppScreen
        }
        Column(
            modifier = Modifier.align(Alignment.Center)
        ) {
            Row (
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 10.dp)
            ) {
                Text("@${uiState.profile.nickName}",
                    fontWeight = FontWeight.Bold)
            }

            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp),
                colors = CardDefaults.cardColors(containerColor = colorResource(R.color.white)),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = RoundedCornerShape(16.dp),

            ) {
                Row {
                    Column {
                        Icon(
                            painter = painterResource(
                                if (uiState.profile.isMale) {
                                    R.drawable.man
                                } else {
                                    R.drawable.women
                                }
                            ),
                            contentDescription = (stringResource(R.string.menu)),
                            modifier = Modifier
                                .size(150.dp)
                                .align(Alignment.CenterHorizontally)
                                .padding(start = 15.dp, end = 15.dp, top = 25.dp, bottom = 25.dp)
                        )
                    }
                    Column(
                        modifier = Modifier.align(Alignment.CenterVertically)
                    ) {
                        Row {
                            Text(
                                uiState.profile.name
                            )
                        }
                        Row {
                            Text(
                                "На платформе с ${uiState.profile.registrationDate / 10000} года"
                            )
                        }

                        Row(
                            modifier = Modifier.padding(top = 7.dp)
                                .align(Alignment.CenterHorizontally)
                        ) {
                            Text (
                                "${formatFollowers(uiState.profile.subsNum)} подписчиков",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )

                        }
                        if (!uiState.isMe) {
                            Row (
                                modifier = Modifier.padding(top = 2.dp)
                                    .align(Alignment.CenterHorizontally)
                            ) {
                                FilledTonalButton(onClick = {
                                    onSubscribe()
                                }) {
                                    Text(if (uiState.profile.isSubscribed) "Отписаться" else "Подписаться")
                                }
                            }
                        }
                    }
                }
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

private fun formatFollowers(count: Int): String {
    return when {
        count >= 1_000_000 -> String.format(Locale.US, "%.2f млн", count / 1_000_000.0)
        count >= 1_000 -> String.format(Locale.US, "%.2f тыc", count / 1_000.0)
        else -> count.toString()
    }
}