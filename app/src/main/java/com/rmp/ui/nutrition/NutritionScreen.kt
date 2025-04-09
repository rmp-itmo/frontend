package com.rmp.ui.nutrition

import android.annotation.SuppressLint
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.webkit.WebView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.viewinterop.AndroidView
import com.rmp.R
import com.rmp.ui.components.AccentButton
import coil.compose.rememberAsyncImagePainter
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.rmp.data.UploadedImage
import com.rmp.data.repository.nutrition.AddMenuDish
import com.rmp.data.repository.nutrition.AddMenuItem
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
    onDishAdd: (AddMenuItem) -> Unit,
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
                    uiState, {},
                    meals = uiState.menu?.meals ?: listOf(),
                    onSwitchDishCheckbox = onSwitchDishCheckbox,
                    onRemoveItem = onRemoveItem,
                    onDishAdd = onDishAdd
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
    onRemoveItem: (Long) -> Unit,
    onDishAdd: (AddMenuItem) -> Unit) {
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
                onRemoveItem = onRemoveItem,
                onDishAdd = {
                    onDishAdd(
                        AddMenuItem(
                            mealId = meal.mealId,
                            newDish = it.copy(
                                typeId = mapTypeNameToId(meal.name)
                            )
                        )
                    )
                }
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
    onRemoveItem: (Long) -> Unit,
    onDishAdd: (AddMenuDish) -> Unit
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
                    onNewDishCreated = onDishAdd,
                    onDishSelected = {}
                )
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(
                        onClick = { addDishFormState = false },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_back),
                            contentDescription = "Назад",
                            modifier = Modifier.size(50.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NewDishForm(
    onAddDish: (AddMenuDish) -> Unit
) {
    val ctx = LocalContext.current

    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var photo by remember { mutableStateOf<UploadedImage?>(null) }
    var calories by remember { mutableFloatStateOf(0f) }
    var fats by remember { mutableFloatStateOf(0f) }
    var protein by remember { mutableFloatStateOf(0f) }
    var carbohydrates by remember { mutableFloatStateOf(0f) }
    var timeToCook by remember { mutableIntStateOf(0) }
    var isNameEmpty by remember { mutableStateOf(false) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val uploaded = UploadedImage.buildFromUri(ctx, it)
            photo = uploaded
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
            Text(
                text = "Название",
                fontSize = 14.sp
            )
            OutlinedTextField(
                value = name,
                onValueChange = {
                    name = it
                    isNameEmpty = name.isEmpty()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                shape = RoundedCornerShape(15.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (isNameEmpty) Color.Red else Color(0xFFDFE2E5),
                    unfocusedBorderColor = if (isNameEmpty) Color.Red else Color(0xFFDFE2E5),
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    focusedPlaceholderColor = Color(0xFFDFE2E5)
                ),
                placeholder = {
                    Text(
                        text = "Введите название",
                        color = Color(0xFFDFE2E5),
                        fontSize = 14.sp
                    )
                },
                singleLine = true,
                isError = isNameEmpty
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Изображение",
                fontSize = 14.sp
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp)
                    .border(
                        width = 1.dp,
                        color = Color(0xFFDFE2E5),
                        shape = RoundedCornerShape(15.dp)
                    )
                    .clickable { imagePickerLauncher.launch("image/*") }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(55.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = photo?.imageName ?: "Загрузи изображение",
                        color = if (photo?.imageName != null) Color.Black
                        else Color(0xFFDFE2E5),
                        fontSize = 14.sp,
                        modifier = Modifier.weight(1f)
                    )
                    if (photo?.imageName == null) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_upload_photo),
                            contentDescription = "Upload dish photo icon",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }

    Text(
        text = "Описание",
        fontSize = 14.sp
    )
    OutlinedTextField(
        value = description,
        onValueChange = { description = it },
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp),
        shape = RoundedCornerShape(15.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFFDFE2E5),
            unfocusedBorderColor = Color(0xFFDFE2E5),
            unfocusedContainerColor = Color.Transparent,
            focusedContainerColor = Color.Transparent,
            focusedPlaceholderColor = Color(0xFFDFE2E5)
        ),
        placeholder = {
            Text(
                text = "Введите описание рецепта",
                color = Color(0xFFDFE2E5),
                fontSize = 14.sp
            )
        },
        singleLine = false
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Время приготовления",
                fontSize = 14.sp
            )
            OutlinedTextField(
                value = timeToCook.toString(),
                onValueChange = { input ->
                    val filteredText = input.replace(Regex("[^0-9.,]"), "")
                    val formattedText = filteredText.replace(',', '.')

                    val parts = formattedText.split('.')
                    if (parts.size <= 2) {
                        timeToCook = try {
                            input.toInt()
                        } catch (_: Exception) {
                            if (input == "") 0 else timeToCook
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                shape = RoundedCornerShape(15.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFDFE2E5),
                    unfocusedBorderColor = Color(0xFFDFE2E5),
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    focusedPlaceholderColor = Color(0xFFDFE2E5)
                ),
                placeholder = {
                    Text(
                        text = "Количество минут",
                        color = Color(0xFFDFE2E5),
                        fontSize = 14.sp
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Калории",
                fontSize = 14.sp
            )
            OutlinedTextField(
                value = calories.toString(),
                onValueChange = { input ->
                    val filteredText = input.replace(Regex("[^0-9.,]"), "")
                    var formattedText = filteredText.replace(',', '.')

                    if (calories == 0f) {
                        formattedText = formattedText.substring(3)
                    }

                    val parts = formattedText.split('.')

                    if (parts.size <= 2) {
                        calories = try {
                            input.toFloat()
                        } catch (_: Exception) {
                            if (input == "") 0f else calories
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                shape = RoundedCornerShape(15.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFDFE2E5),
                    unfocusedBorderColor = Color(0xFFDFE2E5),
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    focusedPlaceholderColor = Color(0xFFDFE2E5)
                ),
                placeholder = {
                    Text(
                        text = "Калории",
                        color = Color(0xFFDFE2E5),
                        fontSize = 14.sp
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )
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
            Text(
                text = "Белки",
                fontSize = 14.sp
            )
            OutlinedTextField(
                value = protein.toString(),
                onValueChange = { input ->
                    val filteredText = input.replace(Regex("[^0-9.,]"), "")
                    val formattedText = filteredText.replace(',', '.')

                    val parts = formattedText.split('.')
                    if (parts.size <= 2) {
                        protein = try {
                            input.toFloat()
                        } catch (_: Exception) {
                            if (input == "") 0f else protein
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                shape = RoundedCornerShape(15.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFDFE2E5),
                    unfocusedBorderColor = Color(0xFFDFE2E5),
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    focusedPlaceholderColor = Color(0xFFDFE2E5)
                ),
                placeholder = {
                    Text(
                        text = "Белки",
                        color = Color(0xFFDFE2E5),
                        fontSize = 14.sp
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Жиры",
                fontSize = 14.sp
            )
            OutlinedTextField(
                value = fats.toString(),
                onValueChange = { input ->
                    val filteredText = input.replace(Regex("[^0-9.,]"), "")
                    val formattedText = filteredText.replace(',', '.')

                    val parts = formattedText.split('.')
                    if (parts.size <= 2) {
                        fats = try {
                            input.toFloat()
                        } catch (_: Exception) {
                            if (input == "") 0f else fats
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                shape = RoundedCornerShape(15.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFDFE2E5),
                    unfocusedBorderColor = Color(0xFFDFE2E5),
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    focusedPlaceholderColor = Color(0xFFDFE2E5)
                ),
                placeholder = {
                    Text(
                        text = "Жиры",
                        color = Color(0xFFDFE2E5),
                        fontSize = 14.sp
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Углеводы",
                fontSize = 14.sp
            )
            OutlinedTextField(
                value = carbohydrates.toString(),
                onValueChange = { input ->
                    val filteredText = input.replace(Regex("[^0-9.,]"), "")
                    val formattedText = filteredText.replace(',', '.')

                    val parts = formattedText.split('.')
                    if (parts.size <= 2) {
                        carbohydrates = try {
                            input.toFloat()
                        } catch (_: Exception) {
                            if (input == "") 0f else carbohydrates
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                shape = RoundedCornerShape(15.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFDFE2E5),
                    unfocusedBorderColor = Color(0xFFDFE2E5),
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    focusedPlaceholderColor = Color(0xFFDFE2E5)
                ),
                placeholder = {
                    Text(
                        text = "Углеводы",
                        color = Color(0xFFDFE2E5),
                        fontSize = 14.sp
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, start = 16.dp, end = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = {
                onAddDish(
                    AddMenuDish(
                        name = name,
                        description = description,
                        image = photo?.image,
                        imageName = photo?.imageName,
                        portionsCount = 1,
                        calories = calories.toDouble(),
                        protein = protein.toDouble(),
                        fat = fats.toDouble(),
                        carbohydrates = carbohydrates.toDouble(),
                        timeToCook = timeToCook.toLong(),
                    )
                )
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
    onNewDishCreated: (AddMenuDish) -> Unit,
    onDishSelected: (Long) -> Unit
) {
    var formSelector by remember { mutableStateOf(false) }
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
    var showDescription by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    onClick = { showDescription = !showDescription },
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ),

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

        if (showDescription) {
            AndroidView(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                factory = { context ->
                    WebView(context).apply {
                        loadDataWithBaseURL(null, dish.description, "text/html", "UTF-8", null)
                    }
                }
            )
        }
    }
    }
}
