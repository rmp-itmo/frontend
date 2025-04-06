package com.rmp.ui.nutrition

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rmp.R
import com.rmp.data.repository.nutrition.Dish
import com.rmp.data.repository.nutrition.Meal
import com.rmp.ui.components.AccentButton
import com.rmp.ui.components.AppScreen
import com.rmp.ui.components.buttons.BackButton
import com.rmp.ui.components.buttons.CalendarButton

@Composable
fun NutritionScreen(
    uiState: NutritionUiState,
    onSaveMenu: () -> Unit,
    onAddNutrition: (Int) -> Unit,
    onCalendarClick: () -> Unit,
    firstEntrance: Boolean,
    onGenerateMenu: () -> Unit
) {
    AppScreen(
        leftComposable = { BackButton() },
        rightComposable = { CalendarButton(onCalendarClick) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .blur(if (firstEntrance) 8.dp else 0.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            NutritionHeader(
                currentAmount = uiState.currentAmount,
                dailyGoal = uiState.dailyGoal
            )

            Spacer(modifier = Modifier.height(16.dp))
            Box(modifier = Modifier.weight(1f)) {
                NutritionCardsList(meals = uiState.meals)
            }

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                AddNutritionButton(onAddNutrition = onAddNutrition)
            }
        }

        if (firstEntrance) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                AccentButton(stringResource(R.string.generate_menu), onGenerateMenu)
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                AccentButton(stringResource(R.string.save_menu), onSaveMenu)
            }
        }
    }
}

@Composable
private fun NutritionHeader(
    currentAmount: Float,
    dailyGoal: Float
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Питание",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "%.1f ккал / %.1f ккал".format(currentAmount, dailyGoal),
            fontSize = 16.sp
        )
    }
}

@Composable
private fun NutritionCardsList(meals: List<Meal>) {
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
private fun AddNutritionButton(onAddNutrition: (Int) -> Unit) {
    IconButton(
        onClick = { onAddNutrition(200) },
        modifier = Modifier.size(48.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_add),
            contentDescription = "Add menu",
            modifier = Modifier.size(32.dp)
        )
    }
}

@Composable
private fun NutritionCard(
    mealName: String,
    dishes: List<Dish>
) {
    ElevatedCard(
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(232.dp)
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = mealName,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(dishes) { dish ->
                    NutritionCardItem(dish = dish)
                }
            }
        }
    }
}

@Composable
private fun NutritionCardItem(dish: Dish) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_eggs),
            contentDescription = "Item image",
            modifier = Modifier.size(57.dp)
        )

        Column(
            modifier = Modifier.weight(1f).padding(horizontal = 8.dp)
        ) {
            Text(
                text = dish.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Белки\n${dish.protein}",
                    fontSize = 12.sp,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = "Жиры\n${dish.fat}",
                    fontSize = 12.sp,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = "Углеводы\n${dish.calories}",
                    fontSize = 12.sp,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Box(
            modifier = Modifier.size(19.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_trash),
                contentDescription = "Trash icon",
                modifier = Modifier.size(19.dp)
            )
        }
    }
}