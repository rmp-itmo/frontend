package com.rmp.ui.components

import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.rmp.R
import com.rmp.ui.home.BackButton
import com.rmp.ui.LocalNavController
import com.rmp.ui.heart.HomeButton
import com.rmp.ui.home.FeedButton
import com.rmp.ui.home.SettingButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScreen(
    showTopBar: Boolean = true,
    showBackButton: Boolean = false,
    showButtons: Boolean = false,
    showButtonHome: Boolean = false,
    openDrawer: () -> Unit = {},
    onSignOutClick: () -> Unit = {},
    clearTokens: () -> Unit = {},
    content: @Composable (BoxScope.() -> Unit)
) {
    val topAppBarState = rememberTopAppBarState()
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val navigator = LocalNavController.current

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

                    if (showButtons || showButtonHome) {
                        Row(
                            modifier = Modifier.padding(start = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (showButtonHome) {
                                HomeButton()
                            } else if (showButtons) {
                                FeedButton()
                            }
                        }
                    }
                    if (showBackButton) {
                        Row (
                            modifier = Modifier.padding(start = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            BackButton()
                        }
                    }
                },
                actions = {
                    if (showButtonHome) {
                        SettingButton()
                        IconButton(
                            onClick = {
                                onSignOutClick()
                                clearTokens()
                            },
                            modifier = Modifier.padding(end = 24.dp, top = 16.dp),
                            colors = IconButtonDefaults.iconButtonColors(contentColor = Color.Gray)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ExitToApp,
                                contentDescription = stringResource(R.string.sign_out),
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }
                }
            )
        },
        content = { padding ->
            Box(
                modifier = Modifier
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