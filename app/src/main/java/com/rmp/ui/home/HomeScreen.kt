package com.rmp.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rmp.R
import com.rmp.ui.components.AccentButton
import com.rmp.ui.components.AppScreen
import com.rmp.ui.components.SpinningCirclesLoader

@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onSignOutClick: () -> Unit,
    clearTokens: () -> Unit
) {
    AppScreen {
        if (uiState.isLoading) {
            SpinningCirclesLoader()
        } else {
            if (uiState.errors.isNotEmpty()) {
                Text("Ошибка")
                return@AppScreen
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Здоровье",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                HealthProgressItem(
                    title = "Калории",
                    progressText = "1800 / 2000",
                    progress = 1800f / 2000f,
                    color = Color(0xFFFF6B6B)
                )

                Spacer(modifier = Modifier.height(16.dp))

                HealthProgressItem(
                    title = "Вода",
                    progressText = "1.2л / 2л",
                    progress = 1.2f / 2f,
                    color = Color(0xFF4CC9F0)
                )

                Spacer(modifier = Modifier.height(16.dp))

                HealthProgressItem(
                    title = "Шаги",
                    progressText = "4300 / 8000",
                    progress = 4300f / 8000f,
                    color = Color(0xFF7209B7)
                )

                Divider(
                    modifier = Modifier
                        .padding(vertical = 24.dp)
                        .fillMaxWidth()
                )

                Text(
                    text = "Сон",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "5 - 32 минуты",
                    fontSize = 16.sp
                )

                Divider(
                    modifier = Modifier
                        .padding(vertical = 24.dp)
                        .fillMaxWidth()
                )

                Text(
                    text = "Питание",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "1678 мол",
                    fontSize = 16.sp
                )

                Divider(
                    modifier = Modifier
                        .padding(vertical = 24.dp)
                        .fillMaxWidth()
                )

                Text(
                    text = "Тренировки",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Достижения",
                    fontSize = 16.sp
                )

                Spacer(modifier = Modifier.weight(1f))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentSize(Alignment.Center)
                ) {
                    AccentButton(
                        stringResource(R.string.sign_out),
                        buttonPressed = {
                            onSignOutClick()
                            clearTokens()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun HealthProgressItem(
    title: String,
    progressText: String,
    progress: Float,
    color: Color
) {
    Column {
        Text(
            text = title,
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            color = color,
            trackColor = Color.LightGray
        )

        Text(
            text = progressText,
            fontSize = 14.sp,
            modifier = Modifier.padding(top = 4.dp).align(Alignment.End)
        )
    }
}