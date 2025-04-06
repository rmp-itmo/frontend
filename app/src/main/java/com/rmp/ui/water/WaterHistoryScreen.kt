package com.rmp.ui.water

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rmp.R
import com.rmp.data.repository.water.WaterDailyRecord
import com.rmp.ui.components.AppScreen
import com.rmp.ui.components.WaterCalendar
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun WaterHistoryScreen(
    viewModel: WaterViewModel,
    dailyGoal: Float,
    onBackClick: () -> Unit
) {
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    val historyState by viewModel.historyState.collectAsState()

    LaunchedEffect(selectedDate) {
        viewModel.loadDailyStats(selectedDate)
    }

    AppScreen {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = selectedDate.format(DateTimeFormatter.ofPattern("yyyy.MM.dd")),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = when (historyState) {
                        is WaterHistoryState.Success ->
                            "%.1f л / %.1f л".format((historyState as WaterHistoryState.Success).totalAmount, dailyGoal)
                        is WaterHistoryState.Empty ->
                            "0.0 л / %.1f л".format(dailyGoal)
                        else -> "0.0 л / 2 л"
                    },
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            WaterCalendar(
                selectedDate = selectedDate,
                onDateSelected = { date ->
                    selectedDate = date
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Box(modifier = Modifier.weight(1f)) {
                when (historyState) {
                    is WaterHistoryState.Loading -> LoadingState()
                    is WaterHistoryState.Empty -> EmptyState()
                    is WaterHistoryState.Error -> ErrorState(
                        message = (historyState as WaterHistoryState.Error).message,
                        onRetry = { viewModel.loadDailyStats(selectedDate) }
                    )
                    is WaterHistoryState.Success -> {
                        val state = historyState as WaterHistoryState.Success
                        WaterCardsList(records = state.records)
                    }
                }
            }

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_back),
                        contentDescription = "Назад",
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun WaterCardsList(records: List<WaterDailyRecord>) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 154.dp),
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(records) { record ->
            WaterCard(time = record.time, amount = "${record.volume} мл")
        }
    }
}

@Composable
private fun WaterCard(time: String, amount: String) {
    ElevatedCard(
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = colorResource(R.color.white)),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .width(154.dp)
            .height(123.dp)
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = time,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_glass),
                    contentDescription = "Water glass",
                    modifier = Modifier.size(40.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = amount,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun EmptyState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Нет данных за выбранный день")
    }
}

@Composable
private fun ErrorState(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(message)
        Button(
            onClick = onRetry,
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Повторить")
        }
    }
}