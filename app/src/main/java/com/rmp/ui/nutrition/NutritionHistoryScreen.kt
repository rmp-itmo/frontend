package com.rmp.ui.nutrition

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.rmp.R
import coil.compose.rememberAsyncImagePainter
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.rmp.data.repository.nutrition.GetDish
import com.rmp.data.repository.nutrition.GetMeal
import com.rmp.data.repository.nutrition.Params
import com.rmp.ui.RmpDestinations
import com.rmp.ui.components.AppScreen
import com.rmp.ui.components.NutritionCalendar
import com.rmp.ui.components.RefreshedAppScreen
import com.rmp.ui.components.buttons.BackButton
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun NutritionHistoryScreen(
    uiState: NutritionUiState,
    fetchHistory: (Int) -> Unit,
    dailyGoal: Float,
    onBackClick: () -> Unit
) {
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    val caloriesCurrent = uiState.history?.dishes?.map {
        it.value.sumOf { it.calories }
    }?.sum() ?: 0.0
    val meals = uiState.history?.dishes?.map { (mealName, dishes) ->
        GetMeal(
            mealId = 0,
            name = mealName,
            dishes = dishes,
            params = Params(
                calories = dishes.sumOf { it.calories },
                protein = dishes.sumOf { it.protein },
                fat = dishes.sumOf { it.fat },
                carbohydrates = dishes.sumOf { it.carbohydrates }
            )
        )
    }?.sortedBy { mapTypeNameToId(it.name) } ?: listOf()
    fun LocalDate.getAsInt(): Int {
        fun Int.fixDate(): String = if (this < 10L) "0$this" else "$this"
        val y = year.fixDate()
        val m = monthValue.fixDate()
        val d = dayOfMonth.fixDate()
        return "$y$m$d".toInt()
    }

    val fetchSelected: () -> Unit = {
        fetchHistory(selectedDate.getAsInt())
    }

    LaunchedEffect(selectedDate) {
        fetchSelected()
    }

    RefreshedAppScreen(
        leftComposable = {
            IconButton(
                onClick = onBackClick
            ) {
                Column(
                    modifier = Modifier.align(Alignment.CenterVertically)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.back_to_feed),
                        contentDescription = (stringResource(R.string.menu)),
                        modifier = Modifier
                            .size(35.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                }
            }
        },
        onRefresh = fetchSelected,
        swipeRefreshState = rememberSwipeRefreshState(uiState.isLoading)
    ) {
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
                    text = "%.1f ккал / %.1f ккал".format(caloriesCurrent, dailyGoal),
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
                if (uiState.errors.isNotEmpty()) {
                    ErrorState(
                        message = "Ошибка загрузки",
                        onRetry = { fetchSelected() }
                    )
                } else {
                    NutritionCardsList(
                        meals = meals,
                    )
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
                dishes = meal.dishes
            )
        }
    }
}

@Composable
private fun NutritionCard(
    mealName: String,
    dishes: List<GetDish>
) {
    ElevatedCard(
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardColors(
            containerColor = Color.White,
            contentColor = CardDefaults.cardColors().contentColor,
            disabledContainerColor = CardDefaults.cardColors().disabledContainerColor,
            disabledContentColor = CardDefaults.cardColors().disabledContentColor
        ),
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
                            dish = dish
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
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
private fun NutritionCardItem(
    dish: GetDish,
) {

    var showDescription by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column {
                SubcomposeAsyncImage(
                    model = "${dish.imageUrl}",
                    contentDescription = "Dish image",
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp)),
                    contentScale = ContentScale.Crop,
                    loading = {
                        Box(modifier = Modifier.width(130.dp).height(130.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    },
                    success = { state ->
                        val intrinsicSize = state.painter.intrinsicSize
                        val aspectRatio = intrinsicSize.width / intrinsicSize.height

                        Box(
                            modifier = Modifier
                                .padding(end = 15.dp)
                                .width(130.dp)
                                .aspectRatio(aspectRatio)
                                .clip(RoundedCornerShape(20.dp))
                        ) {
                            Image(
                                painter = state.painter,
                                contentDescription = "Item image",
                            )
                        }
                    },
                    error = {
                        Box(modifier = Modifier.width(130.dp).height(130.dp), contentAlignment = Alignment.Center) {
                            Text("Изображения нет")
                        }
                    }
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = dish.name.capitalize(),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(15.dp))

                Row(
                    modifier = Modifier.width(200.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Белки",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
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
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Жиры",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
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
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Углеводы",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
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
        }
        Row {
            TextButton(onClick = { showDescription = !showDescription }) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Рецепт", modifier = Modifier.padding(end = 5.dp))
                    if (showDescription)
                        Icon(Icons.Filled.KeyboardArrowUp, null)
                    else
                        Icon(Icons.Filled.KeyboardArrowDown, null)
                }
            }
        }
        if (showDescription) {
            Row {
                Text(
                    text = AnnotatedString.fromHtml(
                        htmlString = dish.description
                    )
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
