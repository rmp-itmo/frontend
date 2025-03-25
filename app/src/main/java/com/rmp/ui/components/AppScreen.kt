package com.rmp.ui.components

import android.app.Activity
import android.content.Context
import android.os.Build
import android.view.inputmethod.InputMethodManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.rmp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScreen(
    showTopBar: Boolean = true,
    openDrawer: () -> Unit = {},
    content: @Composable (BoxScope.() -> Unit)
) {
    val topAppBarState = rememberTopAppBarState()
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Image(
                        painter = painterResource(R.drawable.healthy_food_icon),
                        contentDescription = "RMP",
                        contentScale = ContentScale.Inside,
                        modifier = Modifier.width(55.dp).padding(top = 10.dp)
                    )
                },
                scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(topAppBarState),
            )
        },
        content = { padding ->
            Box(
                modifier = Modifier
                    .padding(padding)
                    .pointerInput(Unit) {
                        detectTapGestures {
                            focusManager.clearFocus()
                            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                            imm.hideSoftInputFromWindow((context as Activity).currentFocus?.windowToken, 0)
                        }
                    },
                content = content
            )
        }
    )
}