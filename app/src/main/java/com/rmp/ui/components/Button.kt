package com.rmp.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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

@Composable
fun AccentImageButton(
    imageRes: Int,
    contentDescription: String,
    modifier: Modifier = Modifier,
    buttonPressed: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Button(
            onClick = buttonPressed,
        ) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = contentDescription,
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(40.dp)
            )
        }
        Text(
            text = contentDescription,
            modifier = Modifier.align(Alignment.CenterHorizontally)
                .scale(0.7f, 0.7f),
            fontWeight = FontWeight.Bold
        )
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