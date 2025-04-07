package com.rmp.ui.settings

import android.widget.Toast
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.rmp.R
import com.rmp.ui.components.AccentImageButton
import com.rmp.ui.components.AccentImageSecondaryButton
import com.rmp.ui.components.Header
import com.rmp.ui.components.LabelledInput
import com.rmp.ui.components.RefreshedAppScreen
import com.rmp.ui.components.buttons.BackButton
import com.rmp.ui.components.buttons.SignOutButton

@Composable
fun SettingsScreen(
    uiState: SettingsUiState,
    onNameChange: (String) -> Unit,
    onGenderChange: (Gender) -> Unit,
    onAgeChange: (String) -> Unit,
    onHeightChange: (String) -> Unit,
    onWeightChange: (String) -> Unit,
    onActivityLevelChange: (ActivityLevel) -> Unit,
    onGoalChange: (Goal) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onNickNameChange: (String) -> Unit,
    onSaveClick: () -> Unit,
    onSignOutClick: () -> Unit,
    onRefresh: () -> Unit,
    clearError: () -> Unit
) {

    val context = LocalContext.current
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = uiState.isLoading)

    RefreshedAppScreen(
        leftComposable = { SignOutButton(onSignOutClick) },
        rightComposable = { BackButton()},
        swipeRefreshState = swipeRefreshState,
        onRefresh = onRefresh
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .imePadding()
                .verticalScroll(rememberScrollState()),
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
                            selected = uiState.activityLevel == ActivityLevel.Low,
                            onClick = { onActivityLevelChange(ActivityLevel.Low) }
                        )
                        Text("Низкий", modifier = Modifier.align(Alignment.CenterVertically))
                        RadioButton(
                            selected = uiState.activityLevel == ActivityLevel.Medium,
                            onClick = { onActivityLevelChange(ActivityLevel.Medium) }
                        )
                        Text("Средний", modifier = Modifier.align(Alignment.CenterVertically))
                        RadioButton(
                            selected = uiState.activityLevel == ActivityLevel.High,
                            onClick = { onActivityLevelChange(ActivityLevel.High) }
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
                            if (uiState.goal == Goal.Lose) {
                                AccentImageButton(
                                    imageRes = R.drawable.weight_lower,
                                    contentDescription = "Снижение",
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    onGoalChange(Goal.Lose)
                                }
                            } else {
                                AccentImageSecondaryButton(
                                    imageRes = R.drawable.lose_weight_svg,
                                    contentDescription = "Снижение",
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    onGoalChange(Goal.Lose)
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
                            if (uiState.goal == Goal.Maintain) {
                                AccentImageButton(
                                    imageRes = R.drawable.weight_same,
                                    contentDescription = "Поддержание",
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                        onGoalChange(Goal.Maintain)
                                }
                            } else {
                                AccentImageSecondaryButton(
                                    imageRes = R.drawable.weight_same_svg,
                                    contentDescription = "Поддержание",
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    onGoalChange(Goal.Maintain)
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
                            if (uiState.goal == Goal.Gain) {
                                AccentImageButton(
                                    imageRes = R.drawable.weight_higher,
                                    contentDescription = "Набор",
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    onGoalChange(Goal.Gain)
                                }
                            } else {
                                AccentImageSecondaryButton(
                                    imageRes = R.drawable.gain_weight_svg,
                                    contentDescription = "Набор",
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    onGoalChange(Goal.Gain)
                                }
                            }
                        }
                    }

                    // Поле для email

                    if (uiState.email.error == null) {
                        LabelledInput(
                            value = uiState.email.value,
                            label = uiState.email.hint,
                            modifier = Modifier.fillMaxWidth(),
                            onInputChange = onEmailChange
                        )
                    } else {
                        LabelledInput(
                            value = uiState.email.value,
                            label = uiState.email.hint,
                            modifier = Modifier.fillMaxWidth(),
                            onInputChange = onEmailChange,
                            isError = true,
                            errorMessage = stringResource(R.string.invalid_email)
                        )
                    }


                    // Поле для пароля
                    LabelledInput(
                        value = uiState.password.value,
                        label = uiState.password.hint,
                        modifier = Modifier.fillMaxWidth(),
                        onInputChange = onPasswordChange
                    )

                    LabelledInput(
                        value = uiState.nickname.value,
                        label = uiState.nickname.hint,
                        modifier = Modifier.fillMaxWidth(),
                        onInputChange = onNickNameChange
                    )
                Button(
                    onClick = onSaveClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    enabled = !checkErrors(uiState)
                ) {
                    Text("Сохранить изменения")
                }
                if (uiState.errorMessage != null) {
                    Toast.makeText(
                        context,
                        uiState.errorMessage,
                        Toast.LENGTH_LONG
                    ).show()

                    clearError.invoke()
                }
            }
        }
    }
}


fun checkErrors(state: SettingsUiState): Boolean {
    return listOf(
        state.email.error, state.height.error, state.name.error,
        state.nickname.error, state.weight.error, state.password.error
    ).any { it != null }
}