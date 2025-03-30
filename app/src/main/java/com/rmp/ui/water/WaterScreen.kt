package com.rmp.ui.water

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rmp.R
import com.rmp.data.repository.water.WaterDailyRecord
import com.rmp.ui.components.AppScreen

@Composable
fun WaterScreen(
    uiState: WaterUiState,
    onBackClick: () -> Unit,
    onAddWater: (Int) -> Unit,
    onCalendarClick: () -> Unit
) {
    AppScreen {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            WaterHeader(
                currentAmount = uiState.currentAmount,
                dailyGoal = uiState.dailyGoal,
                onBackClick = onBackClick,
                onCalendarClick = onCalendarClick
            )

            Spacer(modifier = Modifier.height(16.dp))
            Box(modifier = Modifier.weight(1f)) {
                WaterCardsList(records = uiState.waterRecords)
            }


            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                AddWaterButton(onAddWater = onAddWater)
            }
        }
    }
}

@Composable
private fun WaterHeader(
    currentAmount: Float,
    dailyGoal: Float,
    onBackClick: () -> Unit,
    onCalendarClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                painter = painterResource(id = R.drawable.ic_grid),
                contentDescription = "Back"
            )
        }

        Text(
            text = "Вода",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "%.1f л / %.1f л".format(currentAmount, dailyGoal),
            fontSize = 16.sp
        )

        IconButton(onClick = onCalendarClick) {
            Icon(
                painter = painterResource(id = R.drawable.ic_calendar),
                contentDescription = "Calendar"
            )
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
private fun AddWaterButton(onAddWater: (Int) -> Unit) {
    IconButton(
        onClick = { onAddWater(200) },
        modifier = Modifier.size(48.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_add),
            contentDescription = "Add water",
            modifier = Modifier.size(32.dp)
        )
    }
}

@Composable
private fun WaterCard(time: String, amount: String) {
    ElevatedCard(
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
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