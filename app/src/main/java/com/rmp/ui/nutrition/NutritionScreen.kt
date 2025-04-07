package com.rmp.ui.nutrition

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rmp.R
import com.rmp.ui.components.AccentButton
import com.rmp.ui.components.AppScreen
import coil.compose.rememberAsyncImagePainter
import com.rmp.data.repository.nutrition.GetDish
import com.rmp.data.repository.nutrition.GetMeal
import com.rmp.ui.components.SecondaryButton

@Composable
fun NutritionScreen(
    uiState: NutritionUiState,
    onBackClick: () -> Unit,
    onSwitchDishCheckbox: (Int, Int, Int, Boolean) -> Unit,
    onRemoveItem: (Int, Int, Int) -> Unit,
    onCalendarClick: () -> Unit,
    firstEntrance: Boolean,
    onGenerateMenu: () -> Unit
) {
    AppScreen {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .blur(if (firstEntrance) 8.dp else 0.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            NutritionHeader(
                currentAmount = uiState.caloriesCurrent,
                dailyGoal = uiState.caloriesTarget,
                onBackClick = onBackClick,
                onCalendarClick = onCalendarClick
            )

            Spacer(modifier = Modifier.height(16.dp))
            Box(modifier = Modifier.weight(1f)) {
                NutritionCardsList(
                    meals = uiState.meals,
                    onSwitchDishCheckbox = onSwitchDishCheckbox,
                    onRemoveItem = onRemoveItem
                )
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
        }
    }
}

@Composable
private fun NutritionHeader(
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
            text = "Питание",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "%.1f ккал / %.1f ккал".format(currentAmount, dailyGoal),
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
private fun NutritionCardsList(
    meals: List<GetMeal>,
    onSwitchDishCheckbox: (Int, Int, Int, Boolean) -> Unit,
    onRemoveItem: (Int, Int, Int) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(meals) { meal ->
            NutritionCard(
                mealName = meal.name,
                dishes = meal.dishes,
                mealIndex = meals.indexOf(meal),
                onSwitchDishCheckbox = onSwitchDishCheckbox,
                onRemoveItem
            )
        }
    }
}

@Composable
private fun NutritionCard(
    mealName: String,
    dishes: List<GetDish>,
    mealIndex: Int,
    onSwitchDishCheckbox: (Int, Int, Int, Boolean) -> Unit,
    onRemoveItem: (Int, Int, Int) -> Unit
) {
    var addDishFormState by remember { mutableStateOf(false) }
    var dishName by remember { mutableStateOf("") }
    var dishPhoto by remember { mutableStateOf("") }
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
                .background(Color.White)
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
                            onSwitchDishCheckbox = onSwitchDishCheckbox,
                            onRemoveItem = onRemoveItem
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
                DishForm(
                    dishName = dishName,
                    dishPhoto = dishPhoto,
                    dishCalories = dishCalories,
                    dishDescription = dishDescription,
                    onDishNameChange = { dishName = it },
                    onDishCaloriesChange = { dishCalories = it },
                    onDishDescriptionChange = { dishDescription = it },
                    onAddDish = {
                        addDishFormState = false
                        dishName = ""
                        dishCalories = ""
                        dishDescription = ""
                    }
                )
            }
        }
    }
}

@Composable
fun DishForm(
    dishName: String,
    dishPhoto: String, //TODO change to photo
    dishCalories: String,
    dishDescription: String,
    onDishNameChange: (String) -> Unit,
    onDishCaloriesChange: (String) -> Unit,
    onDishDescriptionChange: (String) -> Unit,
    onAddDish: () -> Unit
) {
    Spacer(modifier = Modifier.height(16.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.Start
        ) {
            Text(text = "Название")
            OutlinedTextField(
                value = dishName,
                onValueChange = { onDishNameChange(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                singleLine = true
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.Start
        ) {
            Text(text = "Изображение")
            OutlinedTextField(
                value = dishPhoto,
                onValueChange = { /* Обработка загрузки изображения */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                singleLine = true
            )
        }
    }

    Text(text = "Описание")
    OutlinedTextField(
        value = dishDescription,
        onValueChange = { onDishDescriptionChange(it) },
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp),
        singleLine = false
    )

    Text(text = "Калории")
    OutlinedTextField(
        value = dishCalories,
        onValueChange = { onDishCaloriesChange(it) },
        modifier = Modifier
            .fillMaxWidth()
            .height(55.dp),
        singleLine = true
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
            OutlinedTextField(
                value = "",
                onValueChange = { /* Обработка белков */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                singleLine = true
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.Start
        ) {
            Text(text = "Жиры")
            OutlinedTextField(
                value = "",
                onValueChange = { /* Обработка жиров */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                singleLine = true
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.Start
        ) {
            Text(text = "Углеводы")
            OutlinedTextField(
                value = "",
                onValueChange = { /* Обработка углеводов */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                singleLine = true
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = {
                onAddDish()
            },
            modifier = Modifier
                .width(156.dp)
                .height(48.dp)
                .clip(RoundedCornerShape(20.dp))
        ) {
            Text(
                text = stringResource(R.string.add),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}


@SuppressLint("DefaultLocale")
@Composable
private fun NutritionCardItem(
    dish: GetDish,
    mealIndex: Int,
    dishIndex: Int,
    onSwitchDishCheckbox: (Int, Int, Int, Boolean) -> Unit,
    onRemoveItem: (Int, Int, Int) -> Unit
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
            IconButton(
                onClick = {
                    onSwitchDishCheckbox(
                        mealIndex,
                        dishIndex,
                        dish.menuItemId,
                        dish.checked
                    ) },
                modifier = Modifier.size(20.dp)
            ) {
                Icon(
                    painter = painterResource(
                        id = if (dish.checked) R.drawable.ic_selected_checkbox
                        else R.drawable.ic_unselected_checkbox
                    ),
                    contentDescription = "Checkbox icon",
                    modifier = Modifier.size(19.dp)
                )
            }
            Image(
                painter = painterResource(id = R.drawable.ic_share),
                contentDescription = "Share icon",
                modifier = Modifier.size(19.dp)
            )
            IconButton(
                onClick = { onRemoveItem(mealIndex, dishIndex, dish.menuItemId) },
                modifier = Modifier.size(20.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_trash),
                    contentDescription = "Trash icon",
                    modifier = Modifier.size(19.dp)
                )
            }
        }
    }
}
