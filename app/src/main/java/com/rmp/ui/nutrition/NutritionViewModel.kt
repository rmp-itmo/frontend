package com.rmp.ui.nutrition

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.rmp.R
import com.rmp.data.AppContainer
import com.rmp.data.ErrorMessage
import com.rmp.data.getCurrentDateAsNumber
import com.rmp.data.repository.forum.ForumRepository
import com.rmp.data.repository.nutrition.GeneratedMenu
import com.rmp.data.repository.nutrition.GeneratedMenuRequest
import com.rmp.data.repository.nutrition.MealRequest
import com.rmp.data.repository.nutrition.Menu
import com.rmp.data.repository.nutrition.NutritionHistory
import com.rmp.data.repository.nutrition.NutritionRepository
import com.rmp.data.repository.nutrition.NutritionStatRequest
import com.rmp.data.repository.nutrition.RemoveMenuItemRequest
import com.rmp.data.repository.nutrition.SaveMenuMeal
import com.rmp.data.repository.nutrition.SaveMenuRequest
import com.rmp.data.repository.nutrition.SwitchDishCheckboxRequest
import com.rmp.ui.forum.feed.FeedViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/*
interface NutritionUiState {
    val caloriesCurrent: Float
    val caloriesTarget: Float
    val meals: List<GetMeal>
    val params: Params
    val idealParams: IdealParams
    val errorMessage: String?
}

sealed class NutritionHistoryState {
    data object Loading : NutritionHistoryState()
    data class Empty(
        val caloriesTarget: Float
    ) : NutritionHistoryState()
    data class Success(
        val date: LocalDate,
        val caloriesCurrent: Float,
        val dishes: Map<String, List<GetDish>>
    ) : NutritionHistoryState()
    data class Error(val message: String) : NutritionHistoryState()
}

private data class NutritionViewModelState(
    override val caloriesCurrent: Float = 0f,
    override val caloriesTarget: Float = 2000f,
    override val meals: List<GetMeal> = emptyList(),
    override val params: Params = Params(),
    override val idealParams: IdealParams = IdealParams(),
    override val errorMessage: String? = null
) : NutritionUiState {
    fun toUiState(): NutritionUiState = this
}
*/
/*
class NutritionViewModel(private val container: AppContainer) : ViewModel() {
    private val _historyState = MutableStateFlow<NutritionHistoryState>(NutritionHistoryState.Loading)
    val historyState = _historyState.asStateFlow()
    private val viewModelState = MutableStateFlow(
        NutritionViewModelState()
    )

    val uiState = viewModelState
        .map(NutritionViewModelState::toUiState)
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value.toUiState()
        )

    init {
        loadDailyStats()
    }

    @SuppressLint("SimpleDateFormat")
    fun loadDailyStats() {
        viewModelScope.launch {
            try {
                val currentDate = SimpleDateFormat("yyyyMMdd").format(Date()).toInt()

                Log.d("loadDailyStats", currentDate.toString())

                val response = container.nutritionRepository.loadDailyStats(
                    NutritionStatRequest(currentDate)
                )

                if (response != null) {
                    updateState(
                        viewModelState.value.copy(
                            caloriesTarget = response.caloriesTarget,
                            caloriesCurrent = response.caloriesCurrent,
                            errorMessage = null
                        )
                    )
                    Log.d("LoadNutritionData",
                        "caloriesTarget: ${response.caloriesTarget}" +
                                "caloriesCurrent: ${response.caloriesCurrent}"
                    )
                } else {
                    updateState(
                        viewModelState.value.copy(
                            errorMessage = "Failed to get caloriesCurrent and caloriesTarget"
                        )
                    )
                }
            } catch (e: Exception) {
                updateState(
                    viewModelState.value.copy(
                        errorMessage = "Failed to load nutrition data: ${e.message}"
                    )
                )
            }
        }
    }

    fun getMenuStats(date: LocalDate) {
        viewModelScope.launch {
            _historyState.value = NutritionHistoryState.Loading
            try {
                val dateInt = date.format(DateTimeFormatter.BASIC_ISO_DATE).toInt()
                val response = container.nutritionRepository.getMenuStats(NutritionStatRequest(dateInt))
                Log.d("ss", response.toString())
                if (response != null && response.dishes.isNotEmpty()) {
                    val totalAmount = response.dishes.values.flatten().sumOf { it.calories }.toFloat()
                    _historyState.value = NutritionHistoryState.Success(
                        date = date,
                        caloriesCurrent = totalAmount,
                        dishes = response.dishes.mapValues { (_, dishes) ->
                            dishes.map {
                                GetDish(
                                    it.id, it.name, it.description, it.imageUrl,
                                    it.portionsCount, it.calories, it.protein,
                                    it.fat, it.carbohydrates, it.timeToCook,
                                    it.typeId, it.menuItemId, it.checked
                                )
                            }
                        }
                    )
                } else {
                    _historyState.value = NutritionHistoryState.Empty(response?.caloriesTarget ?: 2f)
                }
            } catch (e: Exception) {
                _historyState.value = NutritionHistoryState.Error("Ошибка загрузки: ${e.message}")
            }
        }
    }

    fun switchDishCheckbox(mealId: Int, dishId: Int, menuItemId: Int, check: Boolean) {
        viewModelScope.launch {
            try {
                val date = SwitchDishCheckboxRequest(menuItemId, !check)

                Log.d("SwitchDishCheckbox", SwitchDishCheckboxRequest(
                    menuItemId,
                    !check
                ).toString()
                )

                val response = container.nutritionRepository.switchDishCheckbox(date)

                if (response != null) {
                    val updatedMeals = viewModelState.value.meals.map { meal ->
                        meal.copy(dishes = meal.dishes.toMutableList())
                    }.toMutableList()

                    val updatedDishes = updatedMeals[mealId].dishes.toMutableList()

                    val updatedDish = updatedDishes[dishId].copy(checked = !check)
                    updatedDishes[dishId] = updatedDish

                    updatedMeals[mealId] = updatedMeals[mealId].copy(dishes = updatedDishes)

                    updateState(
                        viewModelState.value.copy(
                            meals = updatedMeals,
                            caloriesCurrent = response.calories,
                            errorMessage = null
                        )
                    )
                } else {
                    updateState(
                        viewModelState.value.copy(
                            errorMessage = "Bad menu item switch"
                        )
                    )
                }
            } catch (e: Exception) {
                updateState(
                    viewModelState.value.copy(
                        errorMessage = "Network error"
                    )
                )
            }
        }
    }

    fun removeMenuItem(mealId: Int, dishId: Int, menuItemId: Int) {
        viewModelScope.launch {
            try {
                val date = RemoveMenuItemRequest(menuItemId)

                Log.d("RemoveMenuItem", RemoveMenuItemRequest(
                    menuItemId
                ).toString()
                )

                val response = container.nutritionRepository.removeMenuItem(date)

                if (response != null) {
                    val updatedMeals = viewModelState.value.meals.toMutableList()
                    val meal = updatedMeals[mealId]
                    val updatedMeal = meal.copy(
                        dishes = meal.dishes.toMutableList().apply {
                            removeAt(dishId)
                        }
                    )
                    updatedMeals[mealId] = updatedMeal

                    updateState(
                        viewModelState.value.copy(
                            meals = updatedMeals,
                            caloriesCurrent = response.calories,
                            errorMessage = null
                        )
                    )
                } else {
                    updateState(
                        viewModelState.value.copy(
                            errorMessage = "Вad menu item removal"
                        )
                    )
                }
            } catch (e: Exception) {
                updateState(
                    viewModelState.value.copy(
                        errorMessage = "Network error"
                    )
                )
            }
        }
    }

    fun generateMenu(calories: Int) {
        viewModelScope.launch {
            try {
                val meals: List<MealRequest> = listOf(
                    MealRequest(name = "Завтрак", size = 0.3, type = 1),
                    MealRequest(name = "Обед", size = 0.4, type = 2),
                    MealRequest(name = "Ужин", size = 0.3, type = 3)
                )

                Log.d("GeneratedMenuRequest", GeneratedMenuRequest(
                    calories,
                    meals
                ).toString()
                )

                val response = container.nutritionRepository.getGeneratedMenu(
                    GeneratedMenuRequest(
                        calories,
                        meals
                    )
                )

                if (response != null) {
                    Log.d("GenerateMenu", response.toString())
                    saveGeneratedMenu(response)
                } else {
                    updateState(
                        viewModelState.value.copy(
                            errorMessage = "Failed to generate menu"
                        )
                    )
                }
            } catch (e: Exception) {
                updateState(
                    viewModelState.value.copy(
                        errorMessage = "Network error"
                    )
                )
            }
        }
    }

    private fun saveGeneratedMenu(generatedMenu: GeneratedMenuResponse) {
        viewModelScope.launch {
            try {
                val true_meals: List<SaveMenuMeal> = generatedMenu.meals.map { meal ->
                    SaveMenuMeal(
                        name = meal.name,
                        dishes = meal.dishes.map { dish -> dish.id }
                    )
                }

                val meals: List<SaveMenuMeal> = listOf(
                    SaveMenuMeal(name = "Завтрак", dishes = listOf(12, 25)),
                    SaveMenuMeal(name = "Обед", dishes = listOf(63, 99)),
                    SaveMenuMeal(name = "Ужин", dishes = listOf(119, 152)),
                )

                Log.d("SaveMenuRequest", SaveMenuRequest(
                    true_meals,
                    generatedMenu.params
                ).toString()
                )

                Log.d("SaveMenuRequest", SaveMenuRequest(
                    meals,
                    generatedMenu.params
                ).toString()
                )

                val response = container.nutritionRepository.saveGeneratedMenu(
                    SaveMenuRequest(
                        meals,
                        generatedMenu.params
                    )
                )

                if (response != null) {
                    Log.d("SaveMenu", response.toString())
                    getMenu()
                } else {
                    updateState(
                        viewModelState.value.copy(
                            errorMessage = "Failed to save menu"
                        )
                    )
                }
            } catch (e: Exception) {
                updateState(
                    viewModelState.value.copy(
                        errorMessage = "Network error"
                    )
                )
            }
        }
    }

    private fun getMenu() {
        viewModelScope.launch {
            try {
                val response = container.nutritionRepository.getMenu()

                if (response != null) {
                    Log.d("GetMenu", response.toString())
                    updateState(
                        viewModelState.value.copy(
                            meals = response.meals,
                            params = response.params,
                            errorMessage = null
                        )
                    )
                } else {
                    updateState(
                        viewModelState.value.copy(
                            errorMessage = "Failed to get menu"
                        )
                    )
                }
            } catch (e: Exception) {
                updateState(
                    viewModelState.value.copy(
                        errorMessage = "Network error"
                    )
                )
            }
        }
    }

    private fun updateState(newState: NutritionViewModelState) {
        viewModelState.value = newState
    }

    companion object {
        fun factory(appContainer: AppContainer): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return NutritionViewModel(appContainer) as T
                }
            }
    }
}
*/


data class NutritionUiState(
    val isLoading: Boolean = false,
    val errors: List<ErrorMessage> = emptyList(),
    val isMenuGenerated: Boolean = true,
    val menu: Menu? = null,
    val currentCalories: Float = 0f,
    val targetCalories: Float = 0f,
    val history: NutritionHistory? = null
)

class NutritionViewModel(
    private val nutritionRepository: NutritionRepository
): ViewModel() {
    private val _uiState = MutableStateFlow(NutritionUiState())
    val uiState: StateFlow<NutritionUiState> = _uiState.asStateFlow()

    companion object {
        fun factory(forumRepository: NutritionRepository): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return NutritionViewModel(forumRepository) as T
            }
        }
    }

    init {
        viewModelScope.launch {
            fetchMenu()
        }
    }

    private suspend fun getMenu(scope: CoroutineScope) =
        scope.async { nutritionRepository.getMenu() }.await()

    private fun stopWithError() {
        _uiState.update {
            it.copy(
                isLoading = false,
                errors = listOf(ErrorMessage(null, R.string.error_load_data))
            )
        }
    }

    fun fetchMenu() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true)
            }

            val userMenu = getMenu(this)
            val caloriesData = async { nutritionRepository.loadDailyStats(NutritionStatRequest(
                date = getCurrentDateAsNumber()
            )) }.await()

            if (userMenu == null || caloriesData == null) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errors = listOf(ErrorMessage(null, R.string.error_load_data))
                    )
                }
                return@launch
            }

            _uiState.update {
                it.copy(
                    isLoading = false,
                    errors = emptyList(),
                    menu = userMenu,
                    isMenuGenerated = (userMenu.meals != null),
                    currentCalories = caloriesData.caloriesCurrent,
                    targetCalories = caloriesData.caloriesTarget
                )
            }
        }
    }

    fun generateMenu() {
        val meals: List<MealRequest> = listOf(
            MealRequest(name = "Завтрак", size = 0.3, type = 1),
            MealRequest(name = "Обед", size = 0.4, type = 2),
            MealRequest(name = "Ужин", size = 0.3, type = 3)
        )

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                )
            }

            val generated = async { nutritionRepository.getGeneratedMenu(GeneratedMenuRequest(
                calories = _uiState.value.targetCalories,
                meals = meals
            )) }.await() ?: run {
                stopWithError()
                return@launch
            }

            async { nutritionRepository.saveGeneratedMenu(
                SaveMenuRequest(
                    meals = generated.meals.map {
                        SaveMenuMeal(
                            name = it.name,
                            dishes = it.dishes.map { it.id }
                        )
                    },
                    params = generated.params
                )
            ) }.await() ?: run {
                stopWithError()
                return@launch
            }

            val menu = getMenu(this) ?: run {
                stopWithError()
                return@launch
            }

            _uiState.update {
                it.copy(
                    isLoading = false,
                    isMenuGenerated = true,
                    errors = emptyList(),
                    menu = menu
                )
            }
        }
    }

    fun fetchHistory(date: Int) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true)
            }

            val history = async { nutritionRepository.getMenuStats(NutritionStatRequest(date)) }.await() ?: run {
                stopWithError()
                return@launch
            }

            _uiState.update {
                it.copy(
                    isLoading = false,
                    errors = emptyList(),
                    history = history,
                )
            }
        }
    }

    fun removeMenuItem(menuItemId: Long) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                )
            }

            val removeResult = async {
                nutritionRepository.removeMenuItem(RemoveMenuItemRequest(menuItemId))
            }.await() ?: run {
                stopWithError()
                return@launch
            }


            _uiState.update {
                it.copy(
                    isLoading = false,
                    currentCalories = removeResult.calories,
                    menu = it.menu?.let {
                        it.copy(
                            meals = it.meals?.map { meal ->
                                meal.copy(
                                    dishes = meal.dishes.filter { it.menuItemId != menuItemId }
                                )
                            }
                        )
                    }
                )
            }

        }
    }

    fun switchCheckBox(menuItemId: Long, check: Boolean) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                )
            }

            val removeResult = async {
                nutritionRepository.switchDishCheckbox(SwitchDishCheckboxRequest(menuItemId, check))
            }.await() ?: run {
                stopWithError()
                return@launch
            }


            _uiState.update {
                it.copy(
                    isLoading = false,
                    currentCalories = removeResult.calories,
                    menu = it.menu?.let {
                        it.copy(
                            meals = it.meals?.map { meal ->
                                meal.copy(
                                    dishes = meal.dishes.map {
                                        if (it.menuItemId == menuItemId)
                                            it.copy(
                                                checked = check
                                            )
                                        else it
                                    }
                                )
                            }
                        )
                    }
                )
            }

        }
    }
}