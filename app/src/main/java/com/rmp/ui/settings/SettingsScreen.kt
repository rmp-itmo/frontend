package com.rmp.ui.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rmp.R
import com.rmp.ui.components.AppScreen
import com.rmp.ui.components.AppScreenArct
import com.rmp.ui.components.Header

@Composable
fun SettingsScreen(
    uiState: SettingsUiState,
    onNameChange: (String) -> Unit,
    onGenderChange: (Gender) -> Unit,
    onBirthDateChange: (String) -> Unit,
    onHeightChange: (String) -> Unit,
    onWeightChange: (String) -> Unit,
    onActivityLevelChange: (ActivityLevel) -> Unit,
    onGoalChange: (Goal) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit
) {
    AppScreenArct {
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
                OutlinedTextField(
                    value = uiState.name.value,
                    onValueChange = onNameChange,
                    label = { Text(uiState.name.hint) },
                    modifier = Modifier.fillMaxWidth()
                )

                // Поле для выбора пола
                Row {
                    RadioButton(
                        selected = uiState.gender == Gender.MALE,
                        onClick = { onGenderChange(Gender.MALE) }
                    )
                    Text("Мужской")
                    RadioButton(
                        selected = uiState.gender == Gender.FEMALE,
                        onClick = { onGenderChange(Gender.FEMALE) }
                    )
                    Text("Женский")
                }

                // Поле для выбора даты рождения
                OutlinedTextField(
                    value = uiState.birthDate,
                    onValueChange = onBirthDateChange,
                    label = { Text("Дата рождения") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Поле для роста
                OutlinedTextField(
                    value = uiState.height.value,
                    onValueChange = onHeightChange,
                    label = { Text(uiState.height.hint) },
                    isError = uiState.height.error != null,
                    supportingText = {
                        uiState.height.error?.let {
                            Text(
                                stringResource(it),
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                // Поле для выбора цели
                Text("Цель:")
                Row {
                    Button(onClick = { onGoalChange(Goal.LOSE_WEIGHT) }) { Text("Похудение") }
                    Button(onClick = { onGoalChange(Goal.GAIN_WEIGHT) }) { Text("Набор массы") }
                    Button(onClick = { onGoalChange(Goal.MAINTAIN_WEIGHT) }) { Text("Поддержание веса") }
                }

                // Поле для веса
                OutlinedTextField(
                    value = uiState.weight.value,
                    onValueChange = onWeightChange,
                    label = { Text("Вес") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Поле для email
                OutlinedTextField(
                    value = uiState.email.value,
                    onValueChange = onEmailChange,
                    label = { Text(uiState.email.hint) },
                    modifier = Modifier.fillMaxWidth()
                )

                // Поле для пароля
                OutlinedTextField(
                    value = uiState.password.value,
                    onValueChange = onPasswordChange,
                    label = { Text(uiState.password.hint) },
                    modifier = Modifier.fillMaxWidth()
                )
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
        birthDate = "01.01.1990",
        height = SettingsField(value = "180", hint = "Введите рост (см)"),
        weight = SettingsField(value = "75", hint = "Введите вес (кг)"),
        activityLevel = ActivityLevel.MODERATE, // Например, "Средний"
        goal = Goal.MAINTAIN_WEIGHT, // Например, "Поддержание веса"
        email = SettingsField(value = "example@mail.com", hint = "Введите email"),
        password = SettingsField(value = "password123", hint = "Введите пароль"),
        nickname = "Иван#123" // Никнейм по умолчанию
    )

    SettingsScreen(
        uiState = dummyState,
        onNameChange = {},
        onGenderChange = {},
        onBirthDateChange = {},
        onHeightChange = {},
        onWeightChange = {},
        onActivityLevelChange = {},
        onGoalChange = {},
        onEmailChange = {},
        onPasswordChange = {}
    )
}