package com.rmp.ui.components.buttons

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.rmp.R
import com.rmp.ui.LocalNavController
import com.rmp.ui.RmpDestinations


@Composable
fun RowScope.SignOutButton(
    onSignOutClick: () -> Unit,
) {
    val navigator = LocalNavController.current
    IconButton(
        onClick = {
            onSignOutClick()
            navigator.navigate(RmpDestinations.HELLO_ROUTE)
        }
    ) {
        Column(
            modifier = Modifier.align(Alignment.CenterVertically)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                contentDescription = stringResource(R.string.sign_out),
                modifier = Modifier
                    .size(35.dp)
                    .align(Alignment.CenterHorizontally)
            )
        }
    }
}