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
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.unit.dp
import com.rmp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScreenArct(
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
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(R.drawable.dashboard_icon),
                                contentDescription = "dashboard",
                                contentScale = ContentScale.Fit,
                                modifier = Modifier.size(35.dp).absolutePadding(left=15.dp)
                            )
                            Image(
                                painter = painterResource(R.drawable.healthy_food_icon),
                                contentDescription = "RMP",
                                contentScale = ContentScale.Inside,
                                modifier = Modifier
                                    .width(40.dp)
                            )
                            Image(
                                painter = painterResource(R.drawable.arct_icon),
                                contentDescription = "arct",
                                contentScale = ContentScale.Fit,
                                modifier = Modifier.size(40.dp).absolutePadding(right=15.dp)
                            )
                        }
                    },
                    scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(topAppBarState),
                    // Сдвигаем повыше, иного способа не нашел
                    modifier = Modifier.offset(y = (-30).dp),
                )},
        content = { padding ->
            Box(
                modifier = Modifier
                    .padding(top = 0.dp)
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