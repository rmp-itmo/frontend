package com.rmp.ui.components

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.rmp.ui.theme.RmpTheme

@Composable
fun AccentButton(
    text: String,
    buttonPressed: () -> Unit
) {
    Button(onClick = buttonPressed) {
        Text(text, fontWeight = FontWeight.Bold)
    }
}

@Preview("Accent button preview")
@Composable
fun AccentButtonPreview() {
    RmpTheme {
        AccentButton("Click me") {
            println(1)
        }
    }
}

@Composable
fun SecondaryButton(
    text: String,
    buttonPressed: () -> Unit
) {
    TextButton(onClick = buttonPressed) {
        Text(text, fontWeight = FontWeight.Bold)
    }
}

@Preview("Secondary button preview")
@Composable
fun SecondaryButtonPreview() {
    RmpTheme {
        SecondaryButton("Click me") {
            println(1)
        }
    }
}