package com.rmp.ui.trainings

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.rmp.R
import com.rmp.data.getCurrentDateAsNumber
import com.rmp.data.getAsDate
import com.rmp.data.repository.training.SetStepsTargetDto
import com.rmp.data.repository.training.TrainingListDto
import com.rmp.data.repository.training.TrainingLogDto
import com.rmp.ui.components.DropDown
import com.rmp.ui.components.TimePicker
import com.rmp.ui.components.RefreshedAppScreen
import com.rmp.ui.components.buttons.BackButton
import kotlin.math.roundToInt

@Composable
fun TrainingIcon(trainingType: String) {
    val icon = when(trainingType) {
        "Бег" -> R.drawable.ic_run
        "Плавание" -> R.drawable.ic_swim
        "Велосипед" -> R.drawable.ic_cycling
        else -> R.drawable.ic_run
    }
    Icon(
        painter = painterResource(icon),
        contentDescription = "Training Type #$trainingType",
        modifier = Modifier.size(170.dp)
    )
}

@Composable
fun TrainingDay(trainingDay: Pair<String, List<TrainingListDto.Training>>) {
    val (day, trainings) = trainingDay
    ElevatedCard(
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp, vertical = 10.dp),
//                    .verticalScroll(rememberScrollState()),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = colorResource(R.color.white)),
    ) {
        Column(
            Modifier
                .padding(horizontal = 15.dp, vertical = 10.dp)
        ) {
            Text(
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                text = "$day"
            )

            if (trainings.size > 0) {
                TrainingList(trainings)
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_no_trains),
                        contentDescription = "",
                        modifier = Modifier.size(170.dp)
                    )
                    Text("В этот день у вас не было тренировок")
                }
            }
        }
    }
}

@Composable
fun TrainingList(trainings: List<TrainingListDto.Training>) {
    var idx = 0
    Column {
        for (training in trainings) {
            var last = idx == trainings.lastIndex
            idx++
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    Modifier.padding(horizontal = 10.dp)
                ) {
                    TrainingIcon(training.type)
                }
                Column {
                    Row(
                        Modifier.padding(bottom = 10.dp)
                    ) {
                        Column {
                            Text(
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                text = training.type,
                                modifier = Modifier.padding(bottom = 20.dp))
                            Text("${training.start} - ${training.end}")
                        }
                    }
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.width(200.dp),
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                fontWeight = FontWeight.Bold,
                                text = "Калории"
                            )
                            Text(training.calories.roundToInt().toString())
                        }
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                fontWeight = FontWeight.Bold,
                                text = "Интенсивность"
                            )
                            Text(training.intensity)
                        }
                    }
                }
            }

            if (!last)
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 15.dp, vertical = 5.dp),
                    thickness = 1.dp, color = Color.Black
                )
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
                Text(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    text = "Добавление тренировки",
                    modifier = Modifier.padding(bottom = 20.dp)
                )
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
                            timeStart = hF to mF
                        }
                    }

                    Column {
                        TimePicker("Конец тренировки ${timeEnd.describeTime()}") { (h, m) ->
                            val mF = if (m < 10) "0$m" else "$m"
                            val hF = if (h < 10) "0$h" else "$h"
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
            Column{
                Text(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    text = "Изменить цель по шагам",
                    modifier = Modifier.padding(bottom = 20.dp)
                )
                Text(
                    "Текущая цель ${uiState.stepsTarget}",
                    modifier = Modifier.padding(bottom = 10.dp)
                )
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
fun ProgressBar(
    current: Int,
    total: Int,
    modifier: Modifier = Modifier
) {
    val progress = current.toFloat() / total
    val text = "$current / $total"

    Box(
        modifier = modifier
            .height(40.dp)
            .clip(RoundedCornerShape(35.dp))
            .background(Color.White)
            .padding(4.dp)
    ) {
        // Background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .shadow(
                    elevation = 5.dp,
                    shape = RoundedCornerShape(35.dp),
                    clip = true
                )
                .background(Color.White)
        )

        // Filled progress
        Box(
            modifier = Modifier
                .fillMaxWidth(progress)
                .fillMaxHeight()
                .clip(RoundedCornerShape(35.dp))
                .background(Color(0xFFFFA500))
        )

        // Progress text
        Text(
            text = text,
            color = Color.Black,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 8.dp),
            textAlign = TextAlign.Center
        )
    }
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
                .padding(horizontal = 15.dp, vertical = 20.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            Text(
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                text = "Тренировки"
            )
            ElevatedCard(
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp, vertical = 10.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = colorResource(R.color.white)),
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 15.dp, vertical = 5.dp)
                ) {
                    Text(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        text = "Цель по шагам ${uiState.stepsTarget}"
                    )
                    IconButton(onClick = {
                        showEditSteps = true
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_edit),
                            contentDescription = "",
                            modifier = Modifier
                                .size(25.dp)
                        )
                    }
                }

                ProgressBar(
                    uiState.stepsCurrent, uiState.stepsTarget,
                    Modifier.padding(horizontal = 15.dp, vertical = 5.dp)
                )

                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 15.dp, vertical = 15.dp),
                    thickness = 1.dp, color = Color.Black
                )

                TrainingList(uiState.trainings[today] ?: listOf())

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    IconButton(onClick = {
                        showCreateTraining = true
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_add),
                            contentDescription = "",
                            modifier = Modifier
                                .size(35.dp)
                        )
                    }
                }
            }

            Text(
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 16.dp),
                text = "История"
            )
            fun Int.fixdate(): String = if (this < 10) "0$this" else "$this"

            val localDate = getAsDate(getCurrentDateAsNumber())
            val maxDay = localDate.dayOfMonth
            var day = maxDay
            while (day >= 1) {
                if (day == maxDay) {
                    day -= 1
                    continue
                }
                val key = "${localDate.year}${localDate.monthValue.fixdate()}${day.fixdate()}"
                val formatted = "${day.fixdate()}.${localDate.monthValue.fixdate()}.${localDate.year}"
                if (key !in uiState.trainings) {
                    TrainingDay(formatted to listOf())
                } else {
                    TrainingDay(formatted to uiState.trainings[key]!!)
                }
                day -= 1
            }
        }
    }
}