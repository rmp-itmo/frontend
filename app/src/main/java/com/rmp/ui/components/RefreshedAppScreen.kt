package com.rmp.ui.components

import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.rmp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RefreshedAppScreen(
    leftComposable: @Composable (RowScope.() -> Unit) = {},
    rightComposable: @Composable (RowScope.() -> Unit) = {},
    swipeRefreshState: SwipeRefreshState,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable (BoxScope.() -> Unit) = {},
) {
    val topAppBarState = rememberTopAppBarState()
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    SwipeRefresh(
        state = swipeRefreshState,
        onRefresh = onRefresh,
        modifier = Modifier.fillMaxSize().padding(top = 10.dp)
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Image(
                            painter = painterResource(R.drawable.healthy_food_icon),
                            contentDescription = stringResource(R.string.app_name),
                            contentScale = ContentScale.Inside,
                            modifier = Modifier
                                .width(55.dp)
                                .padding(top = 10.dp)
                        )
                    },
                    scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(topAppBarState),
                    navigationIcon = {
                        Row (
                            modifier = Modifier
                                .padding(top = 12.dp, start = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            leftComposable()
                        }
                    },
                    actions = {
                        Row (
                            modifier = Modifier
                                .padding(top = 12.dp, end = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            rightComposable()
                        }
                    }
                )
            },
            content = { padding ->
                Box(
                    modifier = modifier
                        .padding(padding)
                        .pointerInput(Unit) {
                            detectTapGestures {
                                focusManager.clearFocus()
                                val imm =
                                    context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                                imm.hideSoftInputFromWindow(
                                    (context as Activity).currentFocus?.windowToken,
                                    0
                                )
                            }
                        },
                    content = content
                )
            }
        )
    }
}