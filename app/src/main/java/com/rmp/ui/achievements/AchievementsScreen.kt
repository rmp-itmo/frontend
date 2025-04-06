package com.rmp.ui.achievements

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.rmp.R
import com.rmp.data.repository.achievements.AchievementsDto
import com.rmp.data.repository.achievements.ShareAchievementDto
import com.rmp.ui.components.AppScreen
import com.rmp.ui.components.RefreshedAppScreen
import com.rmp.ui.components.buttons.BackButton


@Composable
fun AchievementCard(
    achievement: AchievementsDto.Achievement,
    type: Int,
    header: String,
    prefix: String,
    icon: Int,
    onShareAchievement: (ShareAchievementDto) -> Unit
) {
    ElevatedCard(
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier.fillMaxWidth().padding(horizontal = 15.dp, vertical = 10.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = colorResource(R.color.white)),
    ) {
        Column(
            modifier = Modifier.padding(vertical = 15.dp, horizontal = 25.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp),
                        text = header
                    )
                }
                Column {
                    IconButton(
                        onClick = {
                            onShareAchievement(ShareAchievementDto(type, achievement.current, achievement.percentage))
                        }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_share),
                            contentDescription = (stringResource(R.string.menu)),
                            modifier = Modifier
                                .size(30.dp)
                                .align(Alignment.CenterHorizontally)
                        )
                    }
                }
            }
            Row {
                Column(
                    modifier = Modifier.padding(end = 40.dp)
                ) {
                    Icon(
                        painter = painterResource(icon),
                        contentDescription = "",
                        modifier = Modifier.size(80.dp),
                        tint = colorResource(R.color.black),
                    )
                }
                Column {
                    Text("Вы соблюдаете норму по ${prefix} уже ${achievement.current} дней \n" +
                            "это лучше чем у ${achievement.percentage}% пользователей!")
                    Text("Ваш рекорд ${achievement.max}")
                }
            }
        }
    }
}


@Composable
fun AchievementsScreen(
    uiState: AchievementsUiState,
    fetchAchievements: () -> Unit,
    onShareAchievement: (Context, ShareAchievementDto) -> Unit
) {
    val ctx = LocalContext.current

    RefreshedAppScreen(
        swipeRefreshState = rememberSwipeRefreshState(isRefreshing = uiState.isLoading),
        onRefresh = fetchAchievements,
        leftComposable = {
            BackButton()
        }
    ) {
        if (uiState.achievements == null) {
            Text("Ошибка загрузки!!")
            return@RefreshedAppScreen
        }
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            AchievementCard(
                achievement = uiState.achievements.calories,
                type = 1,
                header = "Питание",
                prefix = "питанию",
                icon = R.drawable.ic_nutrition_large
            ) { onShareAchievement(ctx, it) }
            AchievementCard(
                achievement = uiState.achievements.water,
                type = 2,
                header = "Вода",
                prefix = "воде",
                icon = R.drawable.ic_water_large
            ) { onShareAchievement(ctx, it) }
            AchievementCard(
                achievement = uiState.achievements.sleep,
                type = 3,
                header = "Сон",
                prefix = "сну",
                icon = R.drawable.ic_sleep_large
            ) { onShareAchievement(ctx, it) }
            AchievementCard(
                achievement = uiState.achievements.steps,
                type = 4,
                header = "Шаги",
                prefix = "шагам",
                icon = R.drawable.ic_workout_large
            ) { onShareAchievement(ctx, it) }
        }
    }
}