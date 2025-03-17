package com.rmp.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ScreenStepVisualizer(
    steps: Int,
    curStep: Int,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier.width((steps * 2 * 10).dp)
    ) {
        for (i in 0..<steps) {
            if (i == curStep)
                Icon(
                    Icons.Filled.Circle,
                    "Active screen",
                    modifier = Modifier.width(10.dp)
                )
            else
                Icon(
                    Icons.Outlined.Circle,
                    "Not active screen",
                    modifier = Modifier.width(10.dp)
                )
        }
    }
}