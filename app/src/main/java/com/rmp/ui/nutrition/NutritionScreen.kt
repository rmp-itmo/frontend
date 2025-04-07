package com.rmp.ui.nutrition

import android.annotation.SuppressLint
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rmp.R
import com.rmp.ui.components.AccentButton
import coil.compose.rememberAsyncImagePainter
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.rmp.data.UploadedImage
import com.rmp.data.repository.nutrition.GetDish
import com.rmp.data.repository.nutrition.GetMeal
import com.rmp.ui.components.RefreshedAppScreen
import com.rmp.ui.components.buttons.BackButton

@Composable
fun NutritionScreen(
    uiState: NutritionUiState,
    onSwitchDishCheckbox: (Long, Boolean) -> Unit,
    onRemoveItem: (Long) -> Unit,
    onCalendarClick: () -> Unit,
    onGenerateMenu: () -> Unit
) {
    val state = rememberSwipeRefreshState(false)

    RefreshedAppScreen(
        leftComposable = { BackButton() },
        onRefresh = {},
        swipeRefreshState = state,
        modifier = Modifier.blur(if (!uiState.isMenuGenerated) 8.dp else 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            NutritionHeader(
                currentAmount = uiState.currentCalories,
                dailyGoal = uiState.targetCalories,
                onCalendarClick = onCalendarClick
            )

            Spacer(modifier = Modifier.height(16.dp))
            Box(modifier = Modifier.weight(1f)) {
                NutritionCardsList(
                    //TODO: Provide findDish method
                    uiState, {},
                    meals = uiState.menu?.meals ?: listOf(),
                    onSwitchDishCheckbox = onSwitchDishCheckbox,
                    onRemoveItem = onRemoveItem
                )
            }
        }
    }

    if (!uiState.isMenuGenerated) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            AccentButton(stringResource(R.string.generate_menu), onGenerateMenu)
        }
    }
}

@Composable
private fun NutritionHeader(
    currentAmount: Float,
    dailyGoal: Float,
    onCalendarClick: () -> Unit
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

        Row {
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
}

@Composable
private fun NutritionCardsList(
    uiState: NutritionUiState,
    findDish: () -> Unit,
    meals: List<GetMeal>,
    onSwitchDishCheckbox: (Long, Boolean) -> Unit,
    onRemoveItem: (Long) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(meals) { meal ->
            NutritionCard(
                uiState, findDish,
                mealName = meal.name,
                dishes = meal.dishes,
                onSwitchDishCheckbox = onSwitchDishCheckbox,
                onRemoveItem
            )
        }
    }
}

@Composable
private fun NutritionCard(
    uiState: NutritionUiState,
    findDish: () -> Unit,
    mealName: String,
    dishes: List<GetDish>,
    onSwitchDishCheckbox: (Long, Boolean) -> Unit,
    onRemoveItem: (Long) -> Unit
) {
    var addDishFormState by remember { mutableStateOf(false) }

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
                    uiState,
                    findDish,
                    onNewDishCreated = {

                    },
                    onDishSelected = {

                    }
                )
            }
        }
    }
}

@Composable
fun NewDishForm(
    onAddDish: () -> Unit
) {
    val ctx = LocalContext.current

    var dishName by remember { mutableStateOf("") }
    var dishDescription by remember { mutableStateOf("") }
    var dishPhoto by remember { mutableStateOf<UploadedImage?>(null) }
    var dishCalories by remember { mutableFloatStateOf(0f) }
    var dishFats by remember { mutableFloatStateOf(0f) }
    var dishProtein by remember { mutableFloatStateOf(0f) }
    var dishCarbohydrates by remember { mutableFloatStateOf(0f) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val uploaded = UploadedImage.buildFromUri(ctx, it)
            dishPhoto = uploaded
        }
    }

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
                onValueChange = { dishName = it },
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
            Button (
                modifier = Modifier.padding(top = 5.dp),
                onClick = { imagePickerLauncher.launch("image/*") }
            ) {
                Text("Загрузить изображение")
            }
        }
    }

    Text(text = "Описание")
    OutlinedTextField(
        value = dishDescription,
        onValueChange = { dishDescription = it },
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp),
        singleLine = false
    )

    Text(text = "Калории")
    OutlinedTextField(
        value = dishCalories.toString(),
        onValueChange = { dishCalories = it.toFloat() },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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
                value = "$dishProtein",
                onValueChange = { dishProtein = it.toFloat() },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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
                value = "$dishFats",
                onValueChange = { dishFats = it.toFloat() },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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
                value = "$dishCarbohydrates",
                onValueChange = { dishCarbohydrates = it.toFloat() },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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
                // TODO: Собираем дто и пихаем ее в колбэк
//                val createDishDto =
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

@Composable
fun FindDishForm(
    uiState: NutritionUiState,
    findDish: () -> Unit,
    onDishSelected: (Long) -> Unit
) {
    val searchInput by remember { mutableStateOf("") }

    OutlinedTextField(
        value = searchInput,
        onValueChange = {  },
        shape = RoundedCornerShape(size = 50.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.Black,
            unfocusedBorderColor = Color.Red,
            unfocusedContainerColor = Color.Magenta,
            focusedContainerColor = Color.Green,
            focusedLabelColor = Color.Magenta
        ),
    )
}

@Composable
fun DishForm(
    uiState: NutritionUiState,
    findDish: () -> Unit,
    onNewDishCreated: () -> Unit,
    onDishSelected: (Long) -> Unit
) {
    var formSelector by remember { mutableStateOf(true) }
    Spacer(modifier = Modifier.height(16.dp))


    if (formSelector) {
        FindDishForm(
            uiState,
            findDish,
            onDishSelected
        )
    } else {
        NewDishForm(onNewDishCreated)
    }
}


@Composable
private fun ApproveRemove(
    name: String,
    onApprove: () -> Unit,
    onCancel: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCancel,
        text = {
            Column {
                Text(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    text = "Удаление $name",
                    modifier = Modifier.padding(bottom = 20.dp)
                )
                Text(
                    fontSize = 16.sp,
                    text = "Вы уверены что хотите удалить $name из вашего меню?"
                )
            }
        },
        confirmButton = {
            FilledTonalButton(onClick = {
                onApprove()
            }) {
                Text("Удалить")
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

@SuppressLint("DefaultLocale")
@Composable
private fun NutritionCardItem(
    dish: GetDish,
    onSwitchDishCheckbox: (Long, Boolean) -> Unit,
    onRemoveItem: (Long) -> Unit
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
            modifier = Modifier
                .size(130.dp)
                .padding(horizontal = 15.dp)
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

        var showRemoveDialog by remember { mutableStateOf(false) }

        if (showRemoveDialog)
            ApproveRemove(dish.name, {
                onRemoveItem(dish.menuItemId)
                showRemoveDialog = false
            }) {
                showRemoveDialog = false
            }

        Column(
            modifier = Modifier.width(19.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(
                onClick = {
                    onSwitchDishCheckbox(
                        dish.menuItemId,
                        !dish.checked
                    )
                },
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
//            Image(
//                painter = painterResource(id = R.drawable.ic_share),
//                contentDescription = "Share icon",
//                modifier = Modifier.size(19.dp)
//            )
            IconButton(
                onClick = {
                    showRemoveDialog = true
                },
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
