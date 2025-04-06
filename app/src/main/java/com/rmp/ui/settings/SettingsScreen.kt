package com.rmp.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rmp.R
import com.rmp.ui.components.AccentImageButton
import com.rmp.ui.components.AccentImageSecondaryButton
import com.rmp.ui.components.AppScreenSettings
import com.rmp.ui.components.Header
import com.rmp.ui.components.LabelledInput
import com.rmp.ui.theme.RmpTheme

@Composable
fun SettingsScreen(
    uiState: SettingsUiState,
    onSignOutClick: () -> Unit,
    onNameChange: (String) -> Unit,
    onGenderChange: (Gender) -> Unit,
    onAgeChange: (String) -> Unit,
    onHeightChange: (String) -> Unit,
    onWeightChange: (String) -> Unit,
    onActivityLevelChange: (ActivityLevel) -> Unit,
    onGoalChange: (Goal) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit
) {
    RmpTheme {
        AppScreenSettings(
            showTopBar = true,
            onSignOutClick = onSignOutClick
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
                    .imePadding(),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxHeight(0.8f)
                ) {
                    Header(
                        stringResource(R.string.settings_header),
                        null,
                        Modifier.align(Alignment.Start),
                        textAlign = TextAlign.Start,
                    )
                    Spacer(modifier = Modifier.height(30.dp))

                    // Поле для имени
                    LabelledInput(
                        value = uiState.name.value,
                        label = uiState.name.hint,
                        modifier = Modifier.fillMaxWidth(),
                        onInputChange = onNameChange
                    )

                    // Поле для выбора пола
                    Text(
                        text = "Пол:",
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Row {
                        RadioButton(
                            selected = uiState.gender == Gender.MALE,
                            onClick = { onGenderChange(Gender.MALE) }
                        )
                        Text("Мужской", modifier = Modifier.align(Alignment.CenterVertically))
                        RadioButton(
                            selected = uiState.gender == Gender.FEMALE,
                            onClick = { onGenderChange(Gender.FEMALE)}
                        )
                        Text("Женский", modifier = Modifier.align(Alignment.CenterVertically))
                    }

                    // Поле для возраста
                    LabelledInput(
                        value = uiState.age,
                        label = "Ваш возраст",
                        modifier = Modifier.fillMaxWidth(),
                        onInputChange = onAgeChange
                    )

                    // Поле для роста
                    LabelledInput(
                        value = uiState.height.value,
                        label = uiState.height.hint,
                        isError = uiState.height.error != null,
                        errorMessage = uiState.height.error?.let { stringResource(it) },
                        modifier = Modifier.fillMaxWidth(),
                        onInputChange = onHeightChange
                    )
                    // Поле для веса
                    LabelledInput(
                        value = uiState.weight.value,
                        label = "Вес",
                        modifier = Modifier.fillMaxWidth(),
                        onInputChange = onWeightChange
                    )
                    // Поле для выбора уровня активности
                    Text(
                        text = "Уровень активности:",
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Row {
                        RadioButton(
                            selected = uiState.activityLevel == ActivityLevel.LOW,
                            onClick = { onActivityLevelChange(ActivityLevel.LOW) }
                        )
                        Text("Низкий", modifier = Modifier.align(Alignment.CenterVertically))
                        RadioButton(
                            selected = uiState.activityLevel == ActivityLevel.MODERATE,
                            onClick = { onActivityLevelChange(ActivityLevel.MODERATE) }
                        )
                        Text("Средний", modifier = Modifier.align(Alignment.CenterVertically))
                        RadioButton(
                            selected = uiState.activityLevel == ActivityLevel.HIGH,
                            onClick = { onActivityLevelChange(ActivityLevel.HIGH) }
                        )
                        Text("Высокий", modifier = Modifier.align(Alignment.CenterVertically))
                    }
                    // Поле для выбора цели
                    Text(
                        text = "Цель:",
                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 8.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Кнопка "Похудение"
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 1.dp)
                                .padding(1.dp)
                        ) {
                            if (uiState.goal == Goal.LOSE_WEIGHT) {
                                AccentImageButton(
                                    imageRes = R.drawable.weight_lower,
                                    contentDescription = "Снижение",
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    onGoalChange(Goal.LOSE_WEIGHT)
                                }
                            } else {
                                AccentImageSecondaryButton(
                                    imageRes = R.drawable.lose_weight_svg,
                                    contentDescription = "Снижение",
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    onGoalChange(Goal.LOSE_WEIGHT)
                                }
                            }
                        }

                        // Кнопка "Поддержание"
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 1.dp)
                                .padding(1.dp)
                        ) {
                            if (uiState.goal == Goal.MAINTAIN_WEIGHT) {
                                AccentImageButton(
                                    imageRes = R.drawable.weight_same,
                                    contentDescription = "Поддержание",
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    onGoalChange(Goal.MAINTAIN_WEIGHT)
                                }
                            } else {
                                AccentImageSecondaryButton(
                                    imageRes = R.drawable.weight_same_svg,
                                    contentDescription = "Поддержание",
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    onGoalChange(Goal.MAINTAIN_WEIGHT)
                                }
                            }
                        }

                        // Кнопка "Набор"
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 1.dp)
                                .padding(1.dp)
                        ) {
                            if (uiState.goal == Goal.GAIN_WEIGHT) {
                                AccentImageButton(
                                    imageRes = R.drawable.weight_higher,
                                    contentDescription = "Набор",
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    onGoalChange(Goal.GAIN_WEIGHT)
                                }
                            } else {
                                AccentImageSecondaryButton(
                                    imageRes = R.drawable.gain_weight_svg,
                                    contentDescription = "Набор",
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    onGoalChange(Goal.GAIN_WEIGHT)
                                }
                            }
                        }
                    }

                    // Поле для email
                    LabelledInput(
                        value = uiState.email.value,
                        label = uiState.email.hint,
                        modifier = Modifier.fillMaxWidth(),
                        onInputChange = onEmailChange
                    )

                    // Поле для пароля
                    LabelledInput(
                        value = uiState.password.value,
                        label = uiState.password.hint,
                        modifier = Modifier.fillMaxWidth(),
                        onInputChange = onPasswordChange
                    )

                    // Поле для никнейма
                    LabelledInput(
                        value = uiState.nickname.value,
                        label = uiState.nickname.hint,
                        modifier = Modifier.fillMaxWidth(),
                        onInputChange = onPasswordChange
                    )
                }
            }
        }
    }
}
@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    val dummyState = SettingsUiState(
        name = SettingsField(value = "Иван", hint = "Введите имя"),
        gender = Gender.MALE, // Значение по умолчанию "Мужской"
        age = "32",
        height = SettingsField(value = "180", hint = "Введите рост (см)"),
        weight = SettingsField(value = "75", hint = "Введите вес (кг)"),
        activityLevel = ActivityLevel.MODERATE, // Например, "Средний"
        goal = Goal.MAINTAIN_WEIGHT, // Например, "Поддержание веса"
        email = SettingsField(value = "example@mail.com", hint = "Введите email"),
        password = SettingsField(value = "password123", hint = "Введите пароль"),
        nickname = SettingsField(value = "Иван#123", hint = "Придумайте себе никнейм")// Никнейм по умолчанию
    )

    SettingsScreen(
        uiState = dummyState,
        onNameChange = {},
        onGenderChange = {},
        onAgeChange = {},
        onHeightChange = {},
        onWeightChange = {},
        onActivityLevelChange = {},
        onGoalChange = {},
        onEmailChange = {},
        onPasswordChange = {},
        onSignOutClick = {}
    )
}