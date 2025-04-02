package com.rmp.ui.nutrition

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.rmp.data.AppContainer
import com.rmp.data.repository.nutrition.GeneratedMenuRequest
import com.rmp.data.repository.nutrition.GeneratedMenuResponse
import com.rmp.data.repository.nutrition.GetMeal
import com.rmp.data.repository.nutrition.IdealParams
import com.rmp.data.repository.nutrition.MealRequest
import com.rmp.data.repository.nutrition.NutritionDailyRecord
import com.rmp.data.repository.nutrition.NutritionStatRequest
import com.rmp.data.repository.nutrition.Params
import com.rmp.data.repository.nutrition.SaveMenuMeal
import com.rmp.data.repository.nutrition.SaveMenuRequest
import com.rmp.data.repository.nutrition.SwitchDishCheckboxRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*


interface NutritionUiState {
    val currentAmount: Float
    val dailyGoal: Float
    val nutritionRecords: List<NutritionDailyRecord>
    val meals: List<GetMeal>
    val params: Params
    val idealParams: IdealParams
    val errorMessage: String?
}

sealed class NutritionHistoryState {
    data object Loading : NutritionHistoryState()
    data class Empty(
        val dailyGoal: Float
    ) : NutritionHistoryState()
    data class Success(
        val date: LocalDate,
        val totalAmount: Float,
        val records: List<NutritionDailyRecord>
    ) : NutritionHistoryState()
    data class Error(val message: String) : NutritionHistoryState()
}

private class NutritionViewModelState(
    override val currentAmount: Float = 0f,
    override val dailyGoal: Float = 2000f,
    override val nutritionRecords: List<NutritionDailyRecord> = emptyList(),
    override val meals: List<GetMeal> = emptyList(),
    override val params: Params = Params(),
    override val idealParams: IdealParams = IdealParams(),
    override val errorMessage: String? = null
) : NutritionUiState {
    fun toUiState(): NutritionUiState = this

    fun copy(
        currentAmount: Float = this.currentAmount,
        dailyGoal: Float = this.dailyGoal,
        nutritionRecords: List<NutritionDailyRecord> = this.nutritionRecords,
        meals: List<GetMeal> = this.meals,
        params: Params = this.params,
        idealParams: IdealParams = this.idealParams,
        errorMessage: String? = this.errorMessage
    ) = NutritionViewModelState(currentAmount, dailyGoal, nutritionRecords, meals, params, idealParams, errorMessage)
}

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
        loadNutritionData()
    }

    fun loadDailyStats(date: LocalDate) {
        viewModelScope.launch {
            _historyState.value = NutritionHistoryState.Loading
            try {
                val dateInt = date.format(DateTimeFormatter.BASIC_ISO_DATE).toInt()
                val response = container.nutritionRepository.getDailyNutritionStats(NutritionStatRequest(dateInt))

                if (response != null && response.nutrition.isNotEmpty()) {
                    val totalAmount = response.nutrition.sumOf { it.volume.toDouble() }.toFloat()
                    _historyState.value = NutritionHistoryState.Success(
                        date = date,
                        totalAmount = totalAmount,
                        records = response.nutrition.map {
                            NutritionDailyRecord(it.date, it.time, (it.volume * 1000))
                        }
                    )
                } else {

                    _historyState.value = NutritionHistoryState.Empty(response?.nutritionTarget?.toFloat() ?: 2f)
                }
            } catch (e: Exception) {
                _historyState.value = NutritionHistoryState.Error("Ошибка загрузки: ${e.message}")
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun loadNutritionData() {
        viewModelScope.launch {
            try {
                val currentDate = SimpleDateFormat("yyyyMMdd").format(Date()).toInt()
                val response = container.nutritionRepository.getDailyNutritionStats(
                    NutritionStatRequest(currentDate)
                )

                Log.d("nutrition-target-log", "${response?.nutritionTarget}")

                response?.let { stats ->
                    val totalAmount = stats.nutrition.sumOf { it.volume.toDouble() }.toFloat()
                    val records = stats.nutrition.map { record ->
                        NutritionDailyRecord(
                            date = record.date,
                            time = record.time,
                            volume = (record.volume * 1000)
                        )
                    }

                    updateState(
                        viewModelState.value.copy(
                            dailyGoal = response.nutritionTarget.toFloat(),
                            currentAmount = totalAmount,
                            nutritionRecords = records,
                            errorMessage = null
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

    fun switchDishCheckbox(mealId: Int, dishId: Int, menuItemId: Int, check: Boolean) {
        viewModelScope.launch {
            try {
                val date = SwitchDishCheckboxRequest(menuItemId, check)

                Log.d("SwitchDishCheckbox", SwitchDishCheckboxRequest(
                    menuItemId,
                    check
                ).toString()
                )

                val response = container.nutritionRepository.switchDishCheckbox(date)

                if (response != null) {
                    val updatedMeals = viewModelState.value.meals.toMutableList()
                    val meal = updatedMeals[mealId]
                    val updatedMeal = meal.copy(
                        dishes = meal.dishes.toMutableList().apply {
                            val updatedDish = this[dishId].copy(checked = check)
                            this[dishId] = updatedDish
                        }
                    )
                    updatedMeals[mealId] = updatedMeal
                    updateState(
                        viewModelState.value.copy(
                            meals = updatedMeals,
                            currentAmount = response.calories,
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
                val response = container.nutritionRepository.removeMenuItem(menuItemId)

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
                            currentAmount = response.calories,
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
