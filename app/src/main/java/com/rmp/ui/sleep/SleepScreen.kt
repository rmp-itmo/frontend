package com.rmp.ui.sleep

import androidx.compose.ui.Alignment
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.Button
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rmp.R
import com.rmp.ui.components.AppScreen

@Composable
fun SleepScreen(
    uiState: SleepUiState,
    onBackClick: () -> Unit,
    onGoBadChange: (String) -> Unit,
    onWakeUpChange: (String) -> Unit,
    onQualityChange: (Int) -> Unit,
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val blockWidth = screenWidth * 1.0f
    val blockHeight = screenHeight * 0.35f
    val images = listOf(
        R.drawable.smile to 1,
        R.drawable.mid to 2,
        R.drawable.sad to 3
    )

    AppScreen {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            SleepHeader(onBackClick)

            if (uiState.isFirstTime) {
                Card(
                    modifier = Modifier
                        .padding(top= 20.dp)
                        .align(Alignment.CenterHorizontally)
                        .width(blockWidth)
                        .height(blockHeight)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White),
                ) {
                    Column {

                        Row(
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(top = 10.dp)
                        ) {
                            Text1(
                                text = "Как вам спалось сегодня?",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                ),
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier
                                    .padding(bottom = 8.dp)
                                    .align(Alignment.CenterVertically)
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
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
                                        onValueChange = { onGoBadChange(it) },
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
                                        onValueChange = { onWakeUpChange(it) },
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
                            images.forEach { (imageRes, value) ->
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
                                onClick = { /* Логика сохранения */ },
                                modifier = Modifier
                                    .width(blockWidth / 2)
                                    .height(blockHeight / 8),
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
        }
    }
}

@Composable
private fun SleepHeader(
    onBackClick: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column()
        {
            IconButton(onClick = onBackClick) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_grid),
                    contentDescription = "Back"
                )
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth().align(Alignment.CenterVertically)
        ) {
            Text1(
                text = "Сон",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterHorizontally).padding(end = 52.dp)
            )
        }
    }
}
