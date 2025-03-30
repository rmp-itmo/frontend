package com.rmp.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.util.Log
import com.rmp.R
import com.rmp.ui.components.AccentButton
import com.rmp.ui.components.AppScreen
import com.rmp.ui.components.SpinningCirclesLoader
import kotlin.math.roundToInt

@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onSignOutClick: () -> Unit,
    clearTokens: () -> Unit
) {
    AppScreen(showButtons = true) {
        if (uiState.isLoading) {
            SpinningCirclesLoader()
        } else {
            if (uiState.errors.isNotEmpty()) {
                AccentButton(
                    text = stringResource(R.string.sign_out),
                    buttonPressed = {
                        onSignOutClick()
                        clearTokens()
                    },
                )
                return@AppScreen
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                HealthCard(
                    uiState = uiState,
                    calories = if (uiState.healthData.calories != null) "${uiState.healthData.calories.first} / ${uiState.healthData.calories.second}" else "",
                    water = if (uiState.healthData.water != null) "${uiState.healthData.water.first}л / ${uiState.healthData.water.second}л" else "",
                    steps = if (uiState.healthData.steps != null) "${uiState.healthData.steps.first} / ${uiState.healthData.steps.second}" else ""
                )

                Spacer(modifier = Modifier.height(16.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        ImageCard(
                            title = (stringResource(R.string.sleep)),
                            value = uiState.healthData.sleep ?: stringResource(R.string.no_data),
                            imageRes = R.drawable.ic_sleep,
                            modifier = Modifier.weight(1f)
                        )
                        ImageCard(
                            title = (stringResource(R.string.heart)),
                            value = (if (uiState.healthData.heartRate != null) uiState.healthData.heartRate + " " + stringResource(R.string.heart_min) else stringResource(R.string.no_data)),
                            imageRes = R.drawable.ic_heart,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        ImageCard(
                            title = (stringResource(R.string.nutrition)),
                            value = (if (uiState.healthData.nutrition != null) uiState.healthData.nutrition + " " + stringResource(R.string.nutrition_unit) else stringResource(R.string.no_data)),
                            imageRes = R.drawable.ic_nutrition,
                            modifier = Modifier.weight(1f)
                        )
                        WaterCard(
                            progress = if (uiState.healthData.water != null) ((uiState.healthData.water.first / uiState.healthData.water.second) * 8).roundToInt() else 0,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        ImageCard(
                            title = (stringResource(R.string.workout)),
                            value = "",
                            imageRes = R.drawable.ic_workout,
                            modifier = Modifier.weight(1f)
                        )
                        ImageCard(
                            title = (stringResource(R.string.achievements)),
                            value = "",
                            imageRes = R.drawable.ic_achievements,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                AccentButton(
                    text = stringResource(R.string.sign_out),
                    buttonPressed = {
                        onSignOutClick()
                        clearTokens()
                    }
                )
            }
        }
    }
}

@Composable
fun ImageCard(
    title: String,
    value: String,
    imageRes: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .aspectRatio(1f)
            .height(160.dp)
            .clickable { },
        colors = CardDefaults.cardColors(containerColor = colorResource(R.color.white)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth()
            )

            when {
                title == stringResource(R.string.sleep) -> {
                    BoldNumbersText(value)
                }
                title == stringResource(R.string.heart) -> {
                    BoldFirstNumberText(value)
                }
                title == stringResource(R.string.nutrition) -> {
                    BoldFirstNumberText(value)
                }
                else -> {
                    Text(
                        text = value,
                        fontSize = 16.sp,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Image(
                painter = painterResource(id = imageRes),
                contentDescription = title,
                modifier = Modifier
                    .size(80.dp)
                    .align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
private fun BoldNumbersText(text: String) {
    Text(
        modifier = Modifier.fillMaxWidth(),
        text = buildAnnotatedString {
            val parts = text.split(" ")
            parts.forEachIndexed { index, part ->
                if (part.matches(Regex("\\d+"))) {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(part)
                    }
                } else {
                    append(part)
                }
                if (index < parts.size - 1) append(" ")
            }
        },
        fontSize = 20.sp
    )
}

@Composable
private fun BoldFirstNumberText(text: String) {
    Text(
        modifier = Modifier.fillMaxWidth(),
        text = buildAnnotatedString {
            val numberPart = text.substringBefore(" ")
            val textPart = text.substringAfter(" ", "")

            if (numberPart.matches(Regex("\\d+"))) {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(numberPart)
                }
                if (textPart.isNotEmpty()) {
                    append(" $textPart")
                }
            } else {
                append(text)
            }
        },
        fontSize = 20.sp
    )
}

@Composable
fun HealthCard(
    calories: String,
    water: String,
    steps: String,
    modifier: Modifier = Modifier,
    uiState: HomeUiState
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = colorResource(R.color.white)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = (stringResource(R.string.health)),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                HealthMetricItem(
                    name = (stringResource(R.string.calories)),
                    value = calories,
                    color = colorResource(R.color.coral)
                )
                HealthMetricItem(
                    name = (stringResource(R.string.water)),
                    value = water,
                    color = colorResource(R.color.marine)
                )
                HealthMetricItem(
                    name = (stringResource(R.string.steps)),
                    value = steps,
                    color = colorResource(R.color.grey)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Box(
                modifier = Modifier.size(100.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    progress = { if (uiState.healthData.calories != null) uiState.healthData.calories.first.toFloat() / uiState.healthData.calories.second.toFloat() else 0f},
                    modifier = Modifier.size(100.dp),
                    color = colorResource(R.color.coral),
                    strokeWidth = 8.dp,
                    trackColor = ProgressIndicatorDefaults.circularIndeterminateTrackColor,
                )
                CircularProgressIndicator(
                    progress = { if (uiState.healthData.water != null) uiState.healthData.water.first / uiState.healthData.water.second else 0f },
                    modifier = Modifier.size(80.dp),
                    color = colorResource(R.color.marine),
                    strokeWidth = 8.dp,
                    trackColor = ProgressIndicatorDefaults.circularIndeterminateTrackColor,
                )
                CircularProgressIndicator(
                    progress = { if (uiState.healthData.steps != null) uiState.healthData.steps.first.toFloat() / uiState.healthData.steps.second.toFloat() else 0f },
                    modifier = Modifier.size(60.dp),
                    color = colorResource(R.color.grey),
                    strokeWidth = 8.dp,
                    trackColor = ProgressIndicatorDefaults.circularIndeterminateTrackColor,
                )
            }
        }
    }
}

@Composable
fun HealthMetricItem(
    name: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .background(color, CircleShape)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = name,
            fontSize = 16.sp,
            modifier = Modifier.width(70.dp)
        )

        Text(
            text = value,
            fontSize = 16.sp,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@Composable
fun WaterCard(progress: Int, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .aspectRatio(1f)
            .height(160.dp),
        colors = CardDefaults.cardColors(containerColor = colorResource(R.color.white)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .clickable {  }
        ) {
            Text(
                text = (stringResource(R.string.water)),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(bottom = 4.dp)
                    .fillMaxWidth()
                    .wrapContentWidth(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    repeat(4) { index ->
                        WaterGlass(isFilled = index < progress.coerceAtMost(4))
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    repeat(4) { index ->
                        WaterGlass(isFilled = index < (progress - 4).coerceAtLeast(0))
                    }
                }
            }
        }
    }
}

@Composable
fun WaterGlass(isFilled: Boolean) {
    Box(
        modifier = Modifier.size(30.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_water),
            contentDescription = (stringResource(R.string.glass_water)),
            tint = if (isFilled) colorResource(R.color.blue) else colorResource(R.color.black),
            modifier = Modifier.size(30.dp)
        )
    }
}

@Composable
fun SettingButton() {
    IconButton(
        onClick = { /* Настройки */ },
        modifier = Modifier
            .wrapContentSize()
            .padding(start = 24.dp, top = 16.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_settings),
            contentDescription = (stringResource(R.string.settings)),
            modifier = Modifier.size(28.dp)
        )
    }
}

@Composable
fun FeedButton() {
    IconButton(
        onClick = { /* Лента */ },
        modifier = Modifier
            .wrapContentSize()
            .padding(end = 24.dp, top = 16.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_feed),
            contentDescription = (stringResource(R.string.feed)),
            modifier = Modifier.size(32.dp)
        )
    }
}
