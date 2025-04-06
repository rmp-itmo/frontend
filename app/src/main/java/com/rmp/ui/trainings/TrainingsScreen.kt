package com.rmp.ui.trainings

import android.util.Log
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TimePickerDialog
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.rmp.R
import com.rmp.data.getCurrentDateAsNumber
import com.rmp.data.repository.training.SetStepsTargetDto
import com.rmp.data.repository.training.TrainingListDto
import com.rmp.data.repository.training.TrainingLogDto
import com.rmp.ui.components.DropDown
import com.rmp.ui.components.RefreshedAppScreen
import com.rmp.ui.components.buttons.BackButton
import kotlin.math.roundToInt

@Composable
fun TrainingIcon(trainingType: String) {
    Icon(
        painter = painterResource(R.drawable.mid),
        contentDescription = "Training Type #$trainingType"
    )
}

@Composable
fun TrainingDay(trainingDay: Pair<String, List<TrainingListDto.Training>>) {
    val (day, trainings) = trainingDay
    Column {
        Text("Date: $day")
        TrainingList(trainings)
    }
}

@Composable
fun TrainingList(trainings: List<TrainingListDto.Training>) {
    Column {
        for (training in trainings) {
            Row {
                Column {
                    TrainingIcon(training.type)
                }
                Column {
                    Row {
                        Column {
                            Text(training.type)
                            Text("Since ${training.start} till ${training.end}")
                        }
                    }
                    Row {
                        Column {
                            Text("Калории")
                            Text(training.calories.roundToInt().toString())
                        }
                        Column {
                            Text("Интенсивность")
                            Text(training.intensity)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePicker(
    label: String,
    onTimeSelected: (Pair<Int, Int>) -> Unit
) {
    var show by remember { mutableStateOf(false) }
    val state = rememberTimePickerState()

    state.is24hour = true

    TextButton(onClick = { show = true }) {
        Text(label)
    }

    if (show) {
        TimePickerDialog(
            onDismissRequest = { show = false },
            title = { Text(label) },
            confirmButton = {
                TextButton(onClick = {
                    onTimeSelected(state.hour to state.minute)
                    show = false
                }) {
                    Text("Выбрать")
                }
            }
        ) {
            androidx.compose.material3.TimePicker(state)
        }
    }
}

private fun Pair<String, String>?.describeTime(): String =
    if (this == null) "XX:XX"
    else "$first:$second"

@Composable
fun CreateTrainingDialog(
    uiState: TrainingsUiState,
    getTypeId: (String) -> Int,
    getIntensityId: (String) -> Int,
    getTypeName: (Int) -> String,
    getIntensityName: (Int) -> String,
    onCancel: () -> Unit,
    onCreate: (TrainingLogDto) -> Unit
) {
    var type by remember { mutableIntStateOf(1) }
    var intensity by remember { mutableIntStateOf(1) }
    var timeStart by remember { mutableStateOf<Pair<String, String>?>(null) }
    var timeEnd by remember { mutableStateOf<Pair<String, String>?>(null) }

    AlertDialog(
        onDismissRequest = onCancel,
        text = {
            Column {
                DropDown(
                    options = uiState.types.map { it.name },
                    label = "Тип тренировки",
                    value = getTypeName(type),
                    onItemSelected = {
                        type = getTypeId(it)
                    }
                )
                DropDown(
                    options = uiState.intensities.map { it.name },
                    label = "Интенсивность",
                    value = getIntensityName(intensity),
                    onItemSelected = {
                        intensity = getIntensityId(it)
                    }
                )
                Row {
                    Column {
                        TimePicker("Начало тренировки ${timeStart.describeTime()}") { (h, m) ->
                            val mF = if (m < 10) "0$m" else "$m"
                            val hF = if (h < 10) "0$h" else "$h"
                            Log.d("training", "selected time: $hF:$mF $h, $m")
                            timeStart = hF to mF
                        }
                    }

                    Column {
                        TimePicker("Конец тренировки ${timeEnd.describeTime()}") { (h, m) ->
                            val mF = if (m < 10) "0$m" else "$m"
                            val hF = if (h < 10) "0$h" else "$h"
                            Log.d("training", "selected time: $hF:$mF $h, $m")
                            timeEnd = hF to mF
                        }
                    }
                }
            }
        },
        confirmButton = {
            FilledTonalButton(
                onClick = {
                    if (timeStart != null && timeEnd != null) {
                        val (hS, mS) = timeStart!!
                        val (hE, mE) = timeEnd!!
                        onCreate(
                            TrainingLogDto(
                                date = getCurrentDateAsNumber(),
                                end = "$hE$mE".toInt(),
                                start = "$hS$mS".toInt(),
                                intensity = intensity,
                                type = type
                            )
                        )
                    }
                }
            ) {
                Text("Добавить")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onCancel
            ) {
                Text("Отмена")
            }
        }
    )
}

@Composable
fun EditStepsDialog(
    uiState: TrainingsUiState,
    onCancel: () -> Unit,
    onCreate: (SetStepsTargetDto) -> Unit
) {
    var steps by remember { mutableStateOf("${uiState.stepsTarget}") }

    AlertDialog(
        onDismissRequest = onCancel,
        text = {
            Column {
                Text("Текущая цель ${uiState.stepsTarget}")
                OutlinedTextField(
                    value = steps,
                    onValueChange = { steps = it },
                    label = { Text("Новая цель") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth(),
                    singleLine = true,
                    shape = MaterialTheme.shapes.medium
                )
            }
        },
        confirmButton = {
            FilledTonalButton(onClick = {
                onCreate(SetStepsTargetDto(date = getCurrentDateAsNumber(), steps.toInt()))
            }) {
                Text("Сохранить")
            }

        },
        dismissButton = {
            TextButton(
                onClick = onCancel
            ) {
                Text("Отмена")
            }
        }

    )
}

@Composable
fun TrainingsScreen(
    uiState: TrainingsUiState,
    fetchLog: (Int?, String?) -> Unit,
    onLogTraining: (TrainingLogDto) -> Unit,
    onSetSteps: (SetStepsTargetDto) -> Unit,
    getTypeId: (String) -> Int,
    getIntensityId: (String) -> Int,
    getTypeName: (Int) -> String,
    getIntensityName: (Int) -> String
) {
    val today = getCurrentDateAsNumber().toString()
    var showCreateTraining by remember { mutableStateOf(false) }
    var showEditSteps by remember { mutableStateOf(false) }

    RefreshedAppScreen(
        leftComposable = { BackButton() },
        swipeRefreshState = rememberSwipeRefreshState(uiState.isLoadings),
        onRefresh = {
            fetchLog(null, null)
        },
    ) {
        if (showCreateTraining) CreateTrainingDialog(uiState, getTypeId, getIntensityId, getTypeName, getIntensityName, {
            showCreateTraining = false
        }) {
            onLogTraining(it)
            showCreateTraining = false
        }
        if (showEditSteps) EditStepsDialog(uiState, {
            showEditSteps = false
        }) {
            onSetSteps(it)
            showEditSteps = false
        }

        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState()),
        ) {
            Text("Тренировки")
            ElevatedCard(
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp, vertical = 10.dp),
//                    .verticalScroll(rememberScrollState()),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = colorResource(R.color.white)),
            ) {
                Row {
                    Text("Цель по шагам ${uiState.stepsTarget}")
                    OutlinedButton(onClick = {
                        showEditSteps = true
                    }) {
                        Text("edit")
                    }
                }
                Text("Текущие шаги: ${uiState.stepsCurrent}")

                HorizontalDivider()

                TrainingList(uiState.trainings[today] ?: listOf())

                TextButton(onClick = {
                    showCreateTraining = true
                }) {
                    Text("Добавить тренировку")
                }
            }

            Text("История")

            uiState.trainings.forEach { (day, list) ->
                if (day == today) return@forEach

                TrainingDay(day to list)
            }
        }
    }
}