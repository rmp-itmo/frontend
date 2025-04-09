package com.rmp.ui.nutrition

import android.util.Log
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.webkit.WebView
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.res.ResourcesCompat
import com.rmp.R
import com.rmp.ui.components.AccentButton
import com.rmp.ui.components.AppScreen
import coil.compose.rememberAsyncImagePainter
import com.rmp.data.UploadedImage
import com.rmp.data.repository.nutrition.GetDish
import com.rmp.data.repository.nutrition.GetMeal
import java.io.ByteArrayOutputStream

@Composable
fun NutritionScreen(
    uiState: NutritionUiState,
    onBackClick: () -> Unit,
    onSwitchDishCheckbox: (Int, Int, Long, Boolean) -> Unit,
    onRemoveItem: (Int, Int, Long) -> Unit,
    onCalendarClick: () -> Unit,
    onDishAdd: (
        Int, Long, String, String,
        Uri?, String, Double, Double,
        Double, Double, Long, Long) -> Unit,
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
                    onRemoveItem = onRemoveItem,
                    onDishAdd = onDishAdd
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
    onSwitchDishCheckbox: (Int, Int, Long, Boolean) -> Unit,
    onRemoveItem: (Int, Int, Long) -> Unit,
    onDishAdd: (
        Int, Long, String, String,
        Uri?, String, Double, Double,
        Double, Double, Long, Long) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(meals) { meal ->
            NutritionCard(
                mealId = meal.mealId,
                mealName = meal.name,
                dishes = meal.dishes,
                mealIndex = meals.indexOf(meal),
                onSwitchDishCheckbox = onSwitchDishCheckbox,
                onRemoveItem = onRemoveItem,
                onDishAdd = onDishAdd
            )
        }
    }
}

@Composable
private fun NutritionCard(
    mealId: Long,
    mealName: String,
    dishes: List<GetDish>,
    mealIndex: Int,
    onSwitchDishCheckbox: (Int, Int, Long, Boolean) -> Unit,
    onRemoveItem: (Int, Int, Long) -> Unit,
    onDishAdd: (
        Int, Long, String, String,
        Uri?, String, Double, Double,
        Double, Double, Long, Long) -> Unit
) {
    var addDishFormState by remember { mutableStateOf(false) }
    var dishName by remember { mutableStateOf("") }
    var dishDescription by remember { mutableStateOf("") }
    var dishImage by remember { mutableStateOf<Uri?>(null) }
    var dishTimeToCook by remember { mutableStateOf("") }
    var dishCalories by remember { mutableStateOf("") }
    var dishProtein by remember { mutableStateOf("") }
    var dishFat by remember { mutableStateOf("") }
    var dishCarbohydrates by remember { mutableStateOf("") }
    val typeId: String

    if (mealName == "Завтрак") {
        typeId = "1"
    } else if (mealName == "Обед") {
        typeId = "2"
    } else {
        typeId = "3"
    }

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
                    mealIndex = mealIndex,
                    mealId = mealId,
                    name = dishName,
                    description = dishDescription,
                    image = dishImage,
                    calories = dishCalories,
                    protein = dishProtein,
                    fat = dishFat,
                    carbohydrates = dishCarbohydrates,
                    timeToCook = dishTimeToCook,
                    typeId = typeId,
                    onNameChange = { dishName = it },
                    onCaloriesChange = { dishCalories = it },
                    onProteinChange = { dishProtein = it },
                    onFatChange = { dishFat = it },
                    onCarbohydratesChange = { dishCarbohydrates = it },
                    onDescriptionChange = { dishDescription = it },
                    onImageChange = { dishImage = it },
                    onTimeToCookChange = { dishTimeToCook = it },
                    onDishAdd = {
                                mealIndex, mealId, name, description,
                                image, imageName, calories, protein,
                                fat, carbohydrates, timeToCook, typeId ->
                        onDishAdd(mealIndex, mealId, name, description,
                            image, imageName, calories, protein, fat,
                            carbohydrates, timeToCook, typeId)
                        addDishFormState = false
                        dishName = ""
                        dishDescription = ""
                        dishImage = null
                        dishName = ""
                        dishCarbohydrates = ""
                        dishProtein = ""
                        dishFat = ""
                        dishCalories = ""
                        dishTimeToCook = ""
                    },
                    closeDishForm = {
                        addDishFormState = false
                        dishName = ""
                        dishDescription = ""
                        dishImage = null
                        dishName = ""
                        dishCarbohydrates = ""
                        dishProtein = ""
                        dishFat = ""
                        dishCalories = ""
                        dishTimeToCook = ""
                    }
                )
            }
        }
    }
}

@Composable
fun DishForm(
    mealIndex: Int, //array index
    mealId: Long, //index in database
    name: String,
    description: String,
    image: Uri?,
    calories: String,
    protein: String,
    fat: String,
    carbohydrates: String,
    timeToCook: String,
    typeId: String,
    onNameChange: (String) -> Unit,
    onCaloriesChange: (String) -> Unit,
    onProteinChange: (String) -> Unit,
    onFatChange: (String) -> Unit,
    onCarbohydratesChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onImageChange: (Uri?) -> Unit,
    onTimeToCookChange: (String) -> Unit,
    onDishAdd: (Int, Long, String, String,
                Uri?, String, Double, Double,
                Double, Double, Long, Long) -> Unit,
    closeDishForm: () -> Unit
) {
    var isNameEmpty by remember { mutableStateOf(false) }
    var isTimeEmpty by remember { mutableStateOf(false) }
    var isCaloriesEmpty by remember { mutableStateOf(false) }
    var isProteinEmpty by remember { mutableStateOf(false) }
    var isFatEmpty by remember { mutableStateOf(false) }
    var isCarbohydratesEmpty by remember { mutableStateOf(false) }

    val context = LocalContext.current
    var uploadedImage by remember { mutableStateOf<UploadedImage?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let {
                val uploaded = UploadedImage.buildFromUri(context, it)
                uploadedImage = uploaded
                onImageChange(uri)
            }
        }
    )

    val eggDrawable = ResourcesCompat.getDrawable(context.resources, R.drawable.ic_eggs, null)
    val eggBitmap = (eggDrawable as BitmapDrawable).bitmap

    val stream = ByteArrayOutputStream()
    eggBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)

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
                value = name,
                onValueChange = {
                    onNameChange(it)
                    isNameEmpty = it.isEmpty()
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
            Text(text = "Изображение")
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp)
                    .border(
                        width = 1.dp,
                        color = Color(0xFFDFE2E5),
                        shape = RoundedCornerShape(15.dp)
                    )
                    .clickable { launcher.launch("image/*") }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(55.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = uploadedImage?.imageName ?: "Загрузи изображение",
                        color = if (uploadedImage?.imageName != null) Color.Black
                        else Color(0xFFDFE2E5),
                        fontSize = 14.sp,
                        modifier = Modifier.weight(1f)
                    )
                    if (uploadedImage?.imageName == null) {
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

    Text(text = "Описание")
    OutlinedTextField(
        value = description,
        onValueChange = { onDescriptionChange(it) },
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
                fontSize = 14.sp,
            )
            OutlinedTextField(
                value = timeToCook,
                onValueChange = { input ->
                    val filteredText = input.replace(Regex("[^0-9.,]"), "")
                    val formattedText = filteredText.replace(',', '.')

                    val parts = formattedText.split('.')
                    if (parts.size <= 2) {
                        onTimeToCookChange(formattedText)
                    }
                    isTimeEmpty = input.isEmpty()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                shape = RoundedCornerShape(15.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (isTimeEmpty) Color.Red else Color(0xFFDFE2E5),
                    unfocusedBorderColor = if (isTimeEmpty) Color.Red else Color(0xFFDFE2E5),
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
                isError = isTimeEmpty,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.Start
        ) {
            Text(text = "Калории")
            OutlinedTextField(
                value = calories,
                onValueChange = { input ->
                    val filteredText = input.replace(Regex("[^0-9.,]"), "")
                    val formattedText = filteredText.replace(',', '.')

                    val parts = formattedText.split('.')
                    if (parts.size <= 2) {
                        onCaloriesChange(formattedText)
                    }
                    isCaloriesEmpty = input.isEmpty()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                shape = RoundedCornerShape(15.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (isCaloriesEmpty) Color.Red else Color(0xFFDFE2E5),
                    unfocusedBorderColor = if (isCaloriesEmpty) Color.Red else Color(0xFFDFE2E5),
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
                isError = isCaloriesEmpty,
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
            Text(text = "Белки")
            OutlinedTextField(
                value = protein,
                onValueChange = { input ->
                    val filteredText = input.replace(Regex("[^0-9.,]"), "")
                    val formattedText = filteredText.replace(',', '.')

                    val parts = formattedText.split('.')
                    if (parts.size <= 2) {
                        onProteinChange(formattedText)
                    }
                    isProteinEmpty = input.isEmpty()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                shape = RoundedCornerShape(15.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (isProteinEmpty) Color.Red else Color(0xFFDFE2E5),
                    unfocusedBorderColor = if (isProteinEmpty) Color.Red else Color(0xFFDFE2E5),
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
                isError = isProteinEmpty,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.Start
        ) {
            Text(text = "Жиры")
            OutlinedTextField(
                value = fat,
                onValueChange = { input ->
                    val filteredText = input.replace(Regex("[^0-9.,]"), "")
                    val formattedText = filteredText.replace(',', '.')

                    val parts = formattedText.split('.')
                    if (parts.size <= 2) {
                        onFatChange(formattedText)
                    }
                    isFatEmpty = input.isEmpty()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                shape = RoundedCornerShape(15.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (isFatEmpty) Color.Red else Color(0xFFDFE2E5),
                    unfocusedBorderColor = if (isFatEmpty) Color.Red else Color(0xFFDFE2E5),
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
                isError = isFatEmpty,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.Start
        ) {
            Text(text = "Углеводы")
            OutlinedTextField(
                value = carbohydrates,
                onValueChange = { input ->
                    val filteredText = input.replace(Regex("[^0-9.,]"), "")
                    val formattedText = filteredText.replace(',', '.')

                    val parts = formattedText.split('.')
                    if (parts.size <= 2) {
                        onCarbohydratesChange(formattedText)
                    }
                    isCarbohydratesEmpty = input.isEmpty()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                shape = RoundedCornerShape(15.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (isCarbohydratesEmpty) Color.Red else Color(0xFFDFE2E5),
                    unfocusedBorderColor = if (isCarbohydratesEmpty) Color.Red else Color(0xFFDFE2E5),
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
                isError = isCarbohydratesEmpty,
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
                if (name == "" || timeToCook == "" || calories == "" ||
                    protein == "" || fat == "" || carbohydrates == "") {
                    closeDishForm()
                    Log.d("", "!!!!!!")
                } else {
                    if (image == null) {
                        val defaultImage = UploadedImage(
                            image = "default_image_base64",
                            imageName = "default_image.jpg"
                        )
                        onDishAdd(
                            mealIndex,
                            mealId,
                            name,
                            description,
                            null,
                            defaultImage.imageName,
                            calories.toDouble(),
                            protein.toDouble(),
                            fat.toDouble(),
                            carbohydrates.toDouble(),
                            timeToCook.toLong(),
                            typeId.toLong()
                        )
                    } else {
                        uploadedImage?.let { uploaded ->
                            onDishAdd(
                                mealIndex,
                                mealId,
                                name,
                                description,
                                image,
                                uploaded.imageName,
                                calories.toDouble(),
                                protein.toDouble(),
                                fat.toDouble(),
                                carbohydrates.toDouble(),
                                timeToCook.toLong(),
                                typeId.toLong()
                            )
                        }
                    }
                }
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

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        IconButton(
            onClick = closeDishForm,
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


@SuppressLint("DefaultLocale")
@Composable
private fun NutritionCardItem(
    dish: GetDish,
    mealIndex: Int,
    dishIndex: Int,
    onSwitchDishCheckbox: (Int, Int, Long, Boolean) -> Unit,
    onRemoveItem: (Int, Int, Long) -> Unit
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
                    modifier = Modifier.size(19.dp)
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
