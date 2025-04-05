package com.rmp.ui.nutrition

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rmp.R
import coil.compose.rememberAsyncImagePainter
import com.rmp.data.repository.nutrition.GetDish
import com.rmp.data.repository.nutrition.GetMeal
import com.rmp.data.repository.nutrition.Params
import com.rmp.ui.components.AppScreen
import com.rmp.ui.components.NutritionCalendar
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun NutritionHistoryScreen(
    viewModel: NutritionViewModel,
    dailyGoal: Float,
    onBackClick: () -> Unit
) {
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    val historyState by viewModel.historyState.collectAsState()

    LaunchedEffect(selectedDate) {
        viewModel.getMenuStats(selectedDate)
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
                        is NutritionHistoryState.Success ->
                            "%.1f ккал / %.1f ккал".format(
                                (historyState as NutritionHistoryState.Success).caloriesCurrent,
                                dailyGoal
                            )
                        is NutritionHistoryState.Empty ->
                            "0.0 ккал / %.1f ккал".format(dailyGoal)
                        else -> "0.0 ккал / $dailyGoal ккал"
                    },
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            NutritionCalendar(
                selectedDate = selectedDate,
                onDateSelected = { date ->
                    selectedDate = date
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Box(modifier = Modifier.weight(1f)) {
                when (historyState) {
                    is NutritionHistoryState.Loading -> LoadingState()
                    is NutritionHistoryState.Empty -> EmptyState()
                    is NutritionHistoryState.Error -> ErrorState(
                        message = (historyState as NutritionHistoryState.Error).message,
                        onRetry = { viewModel.getMenuStats(selectedDate) }
                    )
                    is NutritionHistoryState.Success -> {
                        val state = historyState as NutritionHistoryState.Success
                        val meals = state.dishes.map { (mealName, dishes) ->
                            GetMeal(
                                mealId = 0, // Use a proper ID if available
                                name = mealName,
                                dishes = dishes,
                                params = Params(
                                    calories = dishes.sumOf { it.calories },
                                    protein = dishes.sumOf { it.protein },
                                    fat = dishes.sumOf { it.fat },
                                    carbohydrates = dishes.sumOf { it.carbohydrates }
                                )
                            )
                        }
                        NutritionCardsList(
                            meals = meals,
                        )
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
private fun NutritionCardsList(
    meals: List<GetMeal>) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(meals) { meal ->
            NutritionCard(
                mealName = meal.name,
                dishes = meal.dishes,
                mealIndex = meals.indexOf(meal)
            )
        }
    }
}

@Composable
private fun NutritionCard(
    mealName: String,
    dishes: List<GetDish>,
    mealIndex: Int
) {
    var addDishFormState by remember { mutableStateOf(false) }
    var dishName by remember { mutableStateOf("") }
    var dishCalories by remember { mutableStateOf("") }
    var dishDescription by remember { mutableStateOf("") }

    ElevatedCard(
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 12.dp, start = 12.dp, end = 12.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = mealName,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
            ) {
                dishes.forEachIndexed { index, dish ->
                    Column(modifier = Modifier.fillMaxWidth()) {
                        NutritionCardItem(
                            dish = dish,
                            mealIndex = mealIndex,
                            dishIndex = dishes.indexOf(dish),
                        )

                        if (index < dishes.lastIndex) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(2.dp)
                                    .background(Color(0xFF23252A))
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }
            }

            if (!addDishFormState) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(onClick = { addDishFormState = true }) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_add),
                            contentDescription = "Add dish icon",
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Text(text = "Название")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Изображение")
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        TextField(
                            value = dishName,
                            onValueChange = { dishName = it },
                            label = { Text("Введи название") },
                            modifier = Modifier
                                .weight(1f)
                                .height(39.dp)
                                .padding(end = 8.dp)
                                .border(width = 1.dp, color = Color(0xFF23252A), shape = RoundedCornerShape(15.dp))
                        )
                        TextField(
                            value = "",
                            onValueChange = { /* Обработка загрузки изображения */ },
                            label = { Text("Загрузи изображение") },
                            trailingIcon = {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_upload_photo),
                                    contentDescription = "Download icon"
                                )
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(39.dp)
                                .border(width = 1.dp, color = Color(0xFF23252A), shape = RoundedCornerShape(15.dp))
                        )
                    }

                    Text(text = "Описание")
                    TextField(
                        value = dishDescription,
                        onValueChange = { dishDescription = it },
                        label = { Text("Введи описание рецепта") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(70.dp)
                            .padding(top = 8.dp)
                            .border(width = 1.dp, color = Color(0xFF23252A), shape = RoundedCornerShape(15.dp))
                    )

                    Text(text = "Калории")
                    TextField(
                        value = dishCalories,
                        onValueChange = { dishCalories = it },
                        label = { Text("Введи калории") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(39.dp)
                            .padding(top = 8.dp)
                            .border(width = 1.dp, color = Color(0xFF23252A), shape = RoundedCornerShape(15.dp))
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(text = "Белки")
                            TextField(
                                value = "",
                                onValueChange = { /* Обработка белков */ },
                                label = { Text("Белки") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(39.dp)
                                    .padding(top = 8.dp)
                                    .border(width = 1.dp, color = Color(0xFF23252A), shape = RoundedCornerShape(15.dp))
                            )
                        }
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(text = "Жиры")
                            TextField(
                                value = "",
                                onValueChange = { /* Обработка жиров */ },
                                label = { Text("Жиры") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(39.dp)
                                    .padding(top = 8.dp)
                                    .border(width = 1.dp, color = Color(0xFF23252A), shape = RoundedCornerShape(15.dp))
                            )
                        }
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(text = "Углеводы")
                            TextField(
                                value = "",
                                onValueChange = { /* Обработка углеводов */ },
                                label = { Text("Углеводы") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(39.dp)
                                    .padding(top = 8.dp)
                                    .border(width = 1.dp, color = Color(0xFF23252A), shape = RoundedCornerShape(15.dp))
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            // Обработка кнопки
                            addDishFormState = false
                            dishName = ""
                            dishCalories = ""
                            dishDescription = ""
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text(text = "Добавить")
                    }
                }
            }
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
private fun NutritionCardItem(
    dish: GetDish,
    mealIndex: Int,
    dishIndex: Int,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val painter = rememberAsyncImagePainter(
            model = dish.imageUrl ?: R.drawable.ic_eggs,
            error = painterResource(id = R.drawable.ic_eggs)
        )

        Image(
            painter = painter,
            contentDescription = "Item image",
            modifier = Modifier.size(70.dp)
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = dish.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Белки",
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = String.format("%.1f", dish.protein),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Center
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Жиры",
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = String.format("%.1f", dish.fat),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Center
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Углеводы",
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = String.format("%.1f", dish.calories),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        Column(
            modifier = Modifier.width(19.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
//            Image(
//                painter = painterResource(id = R.drawable.ic_unselected_checkbox),
//                contentDescription = "Checkbox icon",
//                modifier = Modifier.size(19.dp)
//            )
            Image(
                painter = painterResource(id = R.drawable.ic_share),
                contentDescription = "Share icon",
                modifier = Modifier.size(19.dp)
            )
//            Image(
//                painter = painterResource(id = R.drawable.ic_trash),
//                contentDescription = "Checkbox icon",
//                modifier = Modifier.size(19.dp)
//            )
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
