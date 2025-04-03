package com.rmp.ui.home

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rmp.R
import com.rmp.ui.LocalNavController
import com.rmp.ui.RmpDestinations
import com.rmp.ui.components.AppScreen
import com.rmp.ui.components.SpinningCirclesLoader
import com.rmp.ui.components.buttons.FeedButton
import com.rmp.ui.components.buttons.SettingButton
import kotlin.math.roundToInt

@Composable
fun HomeScreen(
    uiState: HomeUiState,
) {
    val context = LocalContext.current
    val navigator = LocalNavController.current

    AppScreen(
        leftComposable = { SettingButton() },
        rightComposable = { FeedButton() }
    ) {
        if (uiState.isLoading) {
            SpinningCirclesLoader()
        } else {
            if (uiState.errors.isNotEmpty()) {
                Toast.makeText(
                    context,
                    stringResource(R.string.error),
                    Toast.LENGTH_SHORT
                ).show()
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
                            modifier = Modifier.weight(1f),
                            onClick = { navigator.navigate(RmpDestinations.SLEEP_ROUTE) }
                        )
                        ImageCard(
                            title = (stringResource(R.string.heart)),
                            value = if (uiState.healthData.heartRate != "") uiState.healthData.heartRate +  " " + stringResource(R.string.heart_min) else "",
                            imageRes = R.drawable.ic_heart,
                            modifier = Modifier.weight(1f),
                            onClick = { navigator.navigate(RmpDestinations.HEART_ROUTE) }
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
                            modifier = Modifier.weight(1f),
                            onClick = { navigator.navigate(RmpDestinations.NUTRITION_ROUTE) }
                        )
                        WaterCard(
                            progress = if (uiState.healthData.water != null) ((uiState.healthData.water.first / uiState.healthData.water.second) * 8).roundToInt() else 0,
                            modifier = Modifier.weight(1f),
                            onClick = { navigator.navigate(RmpDestinations.WATER_ROUTE) }
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
                            modifier = Modifier.weight(1f),
                            onClick = { navigator.navigate(RmpDestinations.TRAIN_ROUTE) }
                        )
                        ImageCard(
                            title = (stringResource(R.string.achievements)),
                            value = "",
                            imageRes = R.drawable.ic_achievements,
                            modifier = Modifier.weight(1f),
                            onClick = { navigator.navigate(RmpDestinations.ACHIEVEMENT_ROUTE) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ImageCard(
    title: String,
    value: String,
    imageRes: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(20.dp)

    Card(
        modifier = modifier
            .aspectRatio(1f)
            .height(160.dp)
            .clip(shape)
            .clickable { onClick() },
        shape = shape,
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
                    .then(
                        if (imageRes == R.drawable.ic_nutrition) {
                            Modifier.padding(top = 15.dp)
                        } else {
                            Modifier
                        }
                    )
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
    ElevatedCard(
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = colorResource(R.color.white)),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.health),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                HealthMetricItem(
                    name = stringResource(R.string.calories),
                    value = calories,
                    color = colorResource(R.color.coral)
                )
                HealthMetricItem(
                    name = stringResource(R.string.water),
                    value = water,
                    color = colorResource(R.color.marine)
                )
                HealthMetricItem(
                    name = stringResource(R.string.steps),
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
                    progress = { 1f },
                    modifier = Modifier.size(100.dp),
                    color = colorResource(R.color.coral).copy(alpha = 0.2f),
                    strokeWidth = 1.dp,
                    trackColor = Color.Transparent
                )
                CircularProgressIndicator(
                    progress = { 1f },
                    modifier = Modifier.size(80.dp),
                    color = colorResource(R.color.marine).copy(alpha = 0.2f),
                    strokeWidth = 1.dp,
                    trackColor = Color.Transparent
                )
                CircularProgressIndicator(
                    progress = { 1f },
                    modifier = Modifier.size(60.dp),
                    color = colorResource(R.color.grey).copy(alpha = 0.2f),
                    strokeWidth = 1.dp,
                    trackColor = Color.Transparent
                )

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (uiState.healthData.calories?.first == 0) {
                        Box(
                            modifier = Modifier
                                .offset(y = (-45).dp)
                                .size(8.dp)
                                .background(colorResource(R.color.coral), CircleShape)
                        )
                    }

                    if (uiState.healthData.water?.first == 0f) {
                        Box(
                            modifier = Modifier
                                .offset(y = (-35).dp)
                                .size(8.dp)
                                .background(colorResource(R.color.marine), CircleShape)
                        )
                    }

                    if (uiState.healthData.steps?.first == 0) {
                        Box(
                            modifier = Modifier
                                .offset(y = (-25).dp)
                                .size(8.dp)
                                .background(colorResource(R.color.grey), CircleShape)
                        )
                    }
                }

                CircularProgressIndicator(
                    progress = {
                        uiState.healthData.calories?.let {
                            it.first.toFloat() / it.second.toFloat()
                        } ?: 0f
                    },
                    modifier = Modifier.size(100.dp),
                    color = colorResource(R.color.coral),
                    strokeWidth = 8.dp,
                    trackColor = Color.Transparent
                )

                CircularProgressIndicator(
                    progress = {
                        uiState.healthData.water?.let {
                            it.first / it.second
                        } ?: 0f
                    },
                    modifier = Modifier.size(80.dp),
                    color = colorResource(R.color.marine),
                    strokeWidth = 8.dp,
                    trackColor = Color.Transparent
                )

                CircularProgressIndicator(
                    progress = {
                        uiState.healthData.steps?.let {
                            it.first.toFloat() / it.second.toFloat()
                        } ?: 0f
                    },
                    modifier = Modifier.size(60.dp),
                    color = colorResource(R.color.grey),
                    strokeWidth = 8.dp,
                    trackColor = Color.Transparent
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
fun WaterCard(
    progress: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(20.dp)

    Card(
        modifier = modifier
            .aspectRatio(1f)
            .height(160.dp)
            .clip(shape)
            .clickable { onClick() },
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = colorResource(R.color.white)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.water),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(bottom = 4.dp)
                    .fillMaxWidth()
                    .wrapContentWidth(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(40.dp))

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