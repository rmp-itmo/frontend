package com.rmp.ui.water

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rmp.R
import com.rmp.data.repository.water.WaterDailyRecord
import com.rmp.ui.components.AppScreen
import com.rmp.ui.components.buttons.BackButton
import com.rmp.ui.components.buttons.CalendarButton
import androidx.compose.material3.Text as Text1

@Composable
fun WaterScreen(
    uiState: WaterUiState,
    onAddWater: (Int) -> Unit,
    onCalendarClick: () -> Unit
) {
    var showVolumeDialog by remember { mutableStateOf(false) }
    AppScreen(
        leftComposable = { BackButton() },
        rightComposable = { CalendarButton(onCalendarClick) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            WaterHeader(
                currentAmount = uiState.currentAmount,
                dailyGoal = uiState.dailyGoal,
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (uiState.waterRecords.isEmpty()) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text1(
                        text = "Сегодня вы ещё не пили воду.",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Normal,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text1(
                        text = "Давайте это исправим!",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }
            } else {
                Box(modifier = Modifier.weight(1f)) {
                    WaterCardsList(records = uiState.waterRecords)
                }
            }

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                AddWaterButton(
                    onClick = { showVolumeDialog = true }
                )
                if (showVolumeDialog) {
                    WaterVolumeDialog(
                        onDismiss = { showVolumeDialog = false },
                        onVolumeSelected = { volume ->
                            onAddWater(volume)
                            showVolumeDialog = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun WaterVolumeDialog(
    onDismiss: () -> Unit,
    onVolumeSelected: (Int) -> Unit
) {
    var volumeText by remember { mutableStateOf("200") }
    var isError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text1("Добавить воду") },
        text = {
            Column {
                Text1("Введите объем воды (мл)", modifier = Modifier.padding(bottom = 8.dp))

                OutlinedTextField(
                    value = volumeText,
                    onValueChange = { newValue ->
                        if (newValue.isEmpty() || newValue.toIntOrNull() != null) {
                            volumeText = newValue
                            isError = false
                        } else {
                            isError = true
                        }
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number
                    ),
                    isError = isError,
                    label = { Text1("Объем в миллилитрах") },
                    modifier = Modifier.fillMaxWidth()
                )

                if (isError) {
                    Text1(
                        text = "Пожалуйста, введите число",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val volume = volumeText.toIntOrNull()
                    if (volume != null && volume > 0) {
                        onVolumeSelected(volume)
                    } else {
                        isError = true
                    }
                },
                enabled = volumeText.isNotEmpty() && !isError
            ) {
                Text1("Добавить")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text1("Отмена")
            }
        }
    )
}

@Composable
private fun WaterHeader(
    currentAmount: Float,
    dailyGoal: Float,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text1(
            text = "Вода",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Text1(
            text = "%.1f л / %.1f л".format(currentAmount, dailyGoal),
            fontSize = 16.sp
        )
    }
}

@Composable
private fun WaterCardsList(records: List<WaterDailyRecord>) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 154.dp),
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(records) { record ->
            WaterCard(time = record.time, amount = "${record.volume} мл")
        }
    }
}

@Composable
private fun AddWaterButton(onClick: () -> Unit) {
    IconButton(
        onClick = onClick,
        modifier = Modifier.size(48.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_add),
            contentDescription = "Add water",
            modifier = Modifier.size(32.dp)
        )
    }
}

@Composable
private fun WaterCard(time: String, amount: String) {
    ElevatedCard(
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = colorResource(R.color.white)),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .width(154.dp)
            .height(123.dp)
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text1(
                text = time,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_glass),
                    contentDescription = "Water glass",
                    modifier = Modifier.size(40.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text1(
                    modifier = Modifier.padding(start = 5.dp),
                    text = amount,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}