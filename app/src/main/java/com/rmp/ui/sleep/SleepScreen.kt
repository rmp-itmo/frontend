package com.rmp.ui.sleep

import androidx.compose.ui.Alignment
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.Button
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text as Text1
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rmp.R
import com.rmp.data.repository.sleep.SleepResponseDto
import com.rmp.ui.components.AppScreen

@Composable
fun SleepScreen(
    uiState: SleepUiState,
    onGoBadChange: (String) -> Unit,
    onWakeUpChange: (String) -> Unit,
    onQualityChange: (Int) -> Unit,
    onGraphicClick: () -> Unit,
    onSaveSleepButton: () -> Unit
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val blockWidth = screenWidth * 1.0f
    val blockHeight = screenHeight * 0.38f

    val images = mapOf(
        1 to R.drawable.smile,
        2 to R.drawable.mid,
        3 to R.drawable.sad
    )

    AppScreen(
        showBackButton = true
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            SleepHeader()

            var isErrorGoBad by remember { mutableStateOf(false) }
            var isErrorWakeUp by remember { mutableStateOf(false) }
            val errorMessage = "Указана некорректная дата"

            if (uiState.isFirstTime) {
                Card(
                    modifier = Modifier
                        .padding(top = 20.dp)
                        .align(Alignment.CenterHorizontally)
                        .width(blockWidth)
                        .height(blockHeight)
                        .shadow(8.dp, RoundedCornerShape(20.dp))
                        .clip(RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = colorResource(R.color.white)),
                ) {
                    Column {

                        Row(
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(top = 15.dp)
                        ) {
                            Text1(
                                text = "Как вам спалось сегодня?",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                ),
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier
                                    .align(Alignment.CenterVertically)
                            )
                        }

                        Row(
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            if (isErrorGoBad || isErrorWakeUp) {
                                Text1(
                                    text = errorMessage,
                                    color = Color.Red,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier
                                        .padding(top = 4.dp)
                                        .align(Alignment.CenterVertically)
                                )
                            } else {
                                Spacer(modifier = Modifier.height(20.dp))
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 5.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(
                                modifier = Modifier.weight(1f).align(Alignment.CenterVertically)
                            ) {
                                Row(
                                    modifier = Modifier.align(Alignment.CenterHorizontally),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text1(
                                        text = "Время отхода ко сну",
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.Normal
                                        ),
                                        modifier = Modifier.align(Alignment.CenterVertically)
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.align(Alignment.CenterHorizontally),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    TextField(
                                        value = uiState.goBadTime,
                                        onValueChange = { newText ->
                                            val formatted = formatTimeInput(newText)

                                            isErrorGoBad = !isTimeValid(formatted)
                                            onGoBadChange(formatted)
                                        },
                                        modifier = Modifier
                                            .align(Alignment.CenterVertically)
                                            .width(screenHeight * 0.1f)
                                            .height(screenWidth * 0.115f)
                                            .clip(RoundedCornerShape(20.dp))
                                            .border(
                                                1.dp,
                                                MaterialTheme.colorScheme.primary,
                                                RoundedCornerShape(20.dp)
                                            ),
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        textStyle = TextStyle(
                                            textAlign = TextAlign.Center,
                                            fontWeight = FontWeight.Medium
                                        ),
                                        colors = TextFieldDefaults.colors(
                                            focusedContainerColor = Color.White,
                                            unfocusedContainerColor = Color.White,
                                            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                                            unfocusedIndicatorColor = Color.White,
                                            disabledIndicatorColor = Color.Transparent
                                        )
                                    )
                                }
                            }

                            Column(
                                modifier = Modifier.weight(1f).align(Alignment.CenterVertically)
                            ) {

                                Row(
                                    modifier = Modifier.align(Alignment.CenterHorizontally),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text1(
                                        text = "Время пробуждения",
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.Normal
                                        ),
                                        modifier = Modifier.align(Alignment.CenterVertically)
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.align(Alignment.CenterHorizontally),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    TextField(
                                        value = uiState.wakeUpTime,
                                        onValueChange = { newText ->
                                            val formatted = formatTimeInput(newText)

                                            isErrorWakeUp = !isTimeValid(formatted)
                                            onWakeUpChange(formatted)
                                        },
                                        modifier = Modifier
                                            .align(Alignment.CenterVertically)
                                            .width(screenHeight * 0.1f)
                                            .height(screenWidth * 0.115f)
                                            .clip(RoundedCornerShape(20.dp))
                                            .border(
                                                1.dp,
                                                MaterialTheme.colorScheme.primary,
                                                RoundedCornerShape(20.dp)
                                            ),
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        textStyle = TextStyle(
                                            textAlign = TextAlign.Center,
                                            fontWeight = FontWeight.Medium
                                        ),
                                        colors = TextFieldDefaults.colors(
                                            focusedContainerColor = Color.White,
                                            unfocusedContainerColor = Color.White,
                                            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                                            unfocusedIndicatorColor = Color.White,
                                            disabledIndicatorColor = Color.Transparent
                                        )
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.CenterHorizontally)
                                .padding(vertical = 10.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text1(
                                text = "Оцените качество сна",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(2.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            images.forEach { (value, imageRes) ->
                                val isSelected = uiState.quality == value
                                Image(
                                    painter = painterResource(imageRes),
                                    contentDescription = "",
                                    modifier = Modifier
                                        .size(50.dp)
                                        .clip(CircleShape)
                                        .graphicsLayer(
                                            alpha = if (isSelected) 1f else 0.5f,
                                            scaleX = 1f,
                                            scaleY = 1f
                                        )
                                        .clickable {
                                            onQualityChange(value)
                                        }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(23.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Button(
                                onClick = {
                                    onSaveSleepButton()
                                },
                                modifier = Modifier
                                    .width(blockWidth / 2)
                                    .height(blockHeight / 8)
                                    .shadow(8.dp, RoundedCornerShape(20.dp)),
                                shape = RoundedCornerShape(20.dp),
                                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary)
                            ) {
                                Text1(
                                    "Сохранить",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            HistoryHeader(onGraphicClick = onGraphicClick)

            SleepCardsList(uiState.sleepRecords, images)
        }
    }
}

@Composable
private fun SleepHeader(){
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().align(Alignment.CenterVertically)
        ) {
            Text1(
                text = "Сон",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
private fun SleepCardsList(records: List<SleepResponseDto>, images: Map<Int, Int>) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 154.dp),
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(records) { record ->
            SleepCard(record, images)
        }
    }
}


@Composable
private fun SleepCard(sleep: SleepResponseDto, images: Map<Int, Int>) {
    ElevatedCard(
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = colorResource(R.color.white)),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .width(150.dp)
            .height(123.dp)
            .padding(vertical = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text1(
                text = "${sleep.date.toString().slice(6..7)}." +
                        "${sleep.date.toString().slice(4..5)}." +
                        sleep.date.toString().slice(0..3),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(id = images[sleep.quality]!!),
                    contentDescription = "Sleep quality",
                    modifier = Modifier.size(40.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text1(
                    modifier = Modifier.padding(start = 3.dp),
                    text = "${sleep.hours} ч ${
                        if (sleep.minutes < 10) "0" + sleep.minutes else sleep.minutes
                    } мин",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun ColumnScope.HistoryHeader(
    onGraphicClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.CenterHorizontally)
            .padding(vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween

    ) {
        Column {
            Row {
                Text1(
                    text = "История",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                )

                Image(
                    painter = painterResource(id = R.drawable.ic_heart),
                    contentDescription = "Sleep quality",
                    modifier = Modifier.size(45.dp)
                        .padding(start = 10.dp)
                        .clickable {
                            onGraphicClick()
                        }
                )
            }
        }

        Column(
            modifier = Modifier.align(Alignment.CenterVertically)
        ) {
            Text1(
                text = "Цель: 8 ч",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            )
        }
    }
}

private fun isTimeValid(time: String): Boolean {
    if (time.isEmpty()) return true

    val timePattern = Regex("^([01][0-9]|2[0-3]):([0-5][0-9])$")
    return timePattern.matches(time)
}

fun formatTimeInput(input: String): String {
    val filtered = input.filter { it.isDigit() }
    return when {
        filtered.isEmpty() -> ""
        filtered.length == 1 -> "0${filtered}:"
        filtered.length == 2 -> "${filtered}:"
        filtered.length == 3 -> "${filtered.substring(0, 2)}:${filtered[2]}" // "123" → "12:3"
        filtered.length >= 4 -> "${filtered.substring(0, 2)}:${filtered.substring(2, 4)}" // "1234" → "12:34"
        else -> input
    }.take(5)
}