package com.rmp.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.rmp.ui.theme.RmpTheme

@Composable
fun Header(
    text: String,
    subtitle: String? = null,
    modifier: Modifier = Modifier,
    textAlign: TextAlign = TextAlign.Center
) {
    if (subtitle == null) {
        Text(
            fontSize = 25.sp,
            text = text,
            textAlign = textAlign,
            modifier = modifier
        )
        return
    }
    Column(modifier) {
        Text(
            fontSize = 25.sp,
            text = text,
            textAlign = textAlign,
        )
        Text(
            fontSize = 15.sp,
            text = subtitle
        )
    }
}

@Preview
@Composable
fun headerPreview() {
    RmpTheme {
        Header("Text", subtitle = "subtitle")
    }
}