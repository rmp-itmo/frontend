package com.rmp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.*

@Composable
fun WaterCalendar(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    val dates = remember {
        List(30) { index ->
            LocalDate.now().minusDays(7L - index)
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        dates.forEach { date ->
            DayItem(
                date = date,
                isSelected = date == selectedDate,
                onClick = { onDateSelected(date) }
            )
        }
    }
}

@Composable
private fun DayItem(
    date: LocalDate,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(40.dp)
            .clickable(onClick = onClick)
    ) {
        Text(
            text = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
            style = MaterialTheme.typography.bodySmall
        )
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(40.dp)
                .background(
                    color = if (isSelected) MaterialTheme.colorScheme.primary
                    else Color.Transparent,
                    shape = CircleShape
                )
        ) {
            Text(
                text = date.dayOfMonth.toString(),
                color = if (isSelected) MaterialTheme.colorScheme.onPrimary
                else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}