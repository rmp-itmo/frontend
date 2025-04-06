package com.rmp.ui.achievements

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import com.rmp.R
import com.rmp.data.repository.achievements.AchievementsDto
import com.rmp.data.repository.achievements.ShareAchievementDto
import com.rmp.ui.components.AppScreen
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
    Column {
        Row {
            Column {
                Text(header)
            }
            Column {
                OutlinedButton(onClick = {
                    onShareAchievement(ShareAchievementDto(type, achievement.current, achievement.percentage))
                }) { "Share" }
            }
        }
        Row {
            Column {
                Image(
                    painter = painterResource(icon),
                    contentDescription = ""
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


@Composable
fun AchievementsScreen(
    uiState: AchievementsUiState,
    fetchAchievements: () -> Unit,
    onShareAchievement: (ShareAchievementDto) -> Unit
) {

    AppScreen(
        leftComposable = {
            BackButton()
        }
    ) {
        if (uiState.achievements == null) return@AppScreen
        Column {
            AchievementCard(
                achievement = uiState.achievements.calories,
                type = 1,
                header = "Питание",
                prefix = "питанию",
                icon = R.drawable.ic_nutrition,
                onShareAchievement
            )
            AchievementCard(
                achievement = uiState.achievements.water,
                type = 2,
                header = "Вода",
                prefix = "воде",
                icon = R.drawable.ic_water,
                onShareAchievement
            )
            AchievementCard(
                achievement = uiState.achievements.sleep,
                type = 3,
                header = "Сон",
                prefix = "сну",
                icon = R.drawable.ic_sleep,
                onShareAchievement
            )
            AchievementCard(
                achievement = uiState.achievements.steps,
                type = 4,
                header = "Шаги",
                prefix = "шагам",
                icon = R.drawable.ic_workout,
                onShareAchievement
            )
        }
    }
}