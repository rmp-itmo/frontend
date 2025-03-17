package com.rmp.ui.components

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.rmp.ui.theme.RmpTheme

@Composable
fun LabelledInput(
    value: String,
    label: String,
    leadingIcon: ImageVector? = null,
    onInputChange: (String) -> Unit
) {
    if (leadingIcon != null)
        OutlinedTextField(
            leadingIcon = { Icon(leadingIcon, label) },
            label = { Text(label) },
            value = value,
            onValueChange = onInputChange
        )
    else
        OutlinedTextField(
            label = { Text(label) },
            value = value,
            onValueChange = onInputChange
        )
}

@Preview("Preview text input")
@Composable
fun PreviewInput() {
    RmpTheme {
        LabelledInput(
            label = "Your name",
            value = "",
            leadingIcon = Icons.Outlined.Person
        ) { println(it) }
    }
}

@Composable
fun DropDown(
    items: List<String>,
    label: String,
    value: String,
    onItemSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var text by remember { mutableStateOf("") }
    var selectedItem by remember { mutableStateOf("") }

    Box(
        modifier = Modifier.clickable {
            expanded = !expanded
            Log.d("tag", "Clicked $expanded")
        }
    ) {
        OutlinedTextField(
            label = { Text(label) },
            value = value,
            onValueChange = {},
            enabled = false
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            offset = DpOffset(0.dp, 0.dp)
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    onClick = {
                        selectedItem = item
                        text = item
                        expanded = false
                        onItemSelected(item)
                    },
                    text = { Text(item) }
                )
            }
        }
    }
}