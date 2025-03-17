package com.rmp.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.rmp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScreen(
    showTopBar: Boolean = true,
    openDrawer: () -> Unit = {},
    content: @Composable (BoxScope.() -> Unit)
) {
    val topAppBarState = rememberTopAppBarState()
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Image(
                        painter = painterResource(R.drawable.healthy_food_icon),
                        contentDescription = "RMP",
                        contentScale = ContentScale.Inside,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(topAppBarState),
            )
        },
        content = { padding ->
            Box(
                modifier = Modifier.padding(padding),
                content = content
            )
        }
    )
}