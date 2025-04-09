package com.rmp.ui.nutrition

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.rmp.R
import com.rmp.data.ErrorMessage
import com.rmp.data.getCurrentDateAsNumber
import com.rmp.data.repository.nutrition.AddMenuItem
import com.rmp.data.repository.nutrition.GeneratedMenuRequest
import com.rmp.data.repository.nutrition.MealRequest
import com.rmp.data.repository.nutrition.Menu
import com.rmp.data.repository.nutrition.NutritionHistory
import com.rmp.data.repository.nutrition.NutritionRepository
import com.rmp.data.repository.nutrition.NutritionStatRequest
import com.rmp.data.repository.nutrition.RemoveMenuItemRequest
import com.rmp.data.repository.nutrition.SaveMenuMeal
import com.rmp.data.repository.nutrition.SaveMenuRequest
import com.rmp.data.repository.nutrition.SearchResultDto
import com.rmp.data.repository.nutrition.SwitchDishCheckboxRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

fun mapTypeNameToId(name: String): Long = when (name) {
    "Завтрак" -> 1
    "Обед" -> 2
    "Ужин" -> 3
    "Перекус" -> 4
    else -> 4
}

data class NutritionUiState(
    val isLoading: Boolean = false,
    val errors: List<ErrorMessage> = emptyList(),
    val isMenuGenerated: Boolean = true,
    val menu: Menu? = null,
    val currentCalories: Float = 0f,
    val targetCalories: Float = 0f,

    val history: NutritionHistory? = null,

    val searchResult: SearchResultDto? = null
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

    fun addMenuItem(addMenuItem: AddMenuItem) {

    }

}