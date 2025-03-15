package com.rmp.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    uiState: HomeUiState,
    openDrawer: () -> Unit,
) {
    Column(modifier = Modifier.padding(30.dp)) {
        Text("Text", color = Color.Blue)   
    }
}