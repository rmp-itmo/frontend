package com.rmp.ui.components.buttons

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.rmp.R


@Composable
fun RowScope.CalendarButton(
    onClick: () -> Unit,
) {
    IconButton(
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.align(Alignment.CenterVertically)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_calendar),
                contentDescription = "Calendar",
                modifier = Modifier
                    .size(35.dp)
                    .align(Alignment.CenterHorizontally)
            )
        }
    }
}
