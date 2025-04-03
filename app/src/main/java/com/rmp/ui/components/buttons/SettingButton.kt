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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.rmp.R
import com.rmp.ui.LocalNavController
import com.rmp.ui.RmpDestinations


@Composable
fun RowScope.SettingButton() {
    val navigator = LocalNavController.current

    IconButton(
        onClick = { navigator.navigate(RmpDestinations.SETTINGS_ROUTE) }
    ) {
        Column(
            modifier = Modifier.align(Alignment.CenterVertically)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.settings),
                contentDescription = (stringResource(R.string.settings)),
                modifier = Modifier
                    .size(40.dp)
                    .align(Alignment.CenterHorizontally)
            )
        }
    }
}