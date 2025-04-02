package com.rmp.data.repository.nutrition

import com.rmp.ui.signup.StateDescription
import kotlinx.serialization.Serializable

@Serializable
data class NutritionLogResponse(
    val id: Int,
    val currentAmount: Float? = null,
    val dailyGoal: Int? = null,
    val logs: List<NutritionDailyRecord>? = null
)

@Serializable
data class NutritionStatRequest(
    val date: Int // Формат YYYYMMDD
)

@Serializable
data class NutritionStatResponse(
    val nutritionTarget: Double,
    val nutrition: List<NutritionDailyRecord>
)

@Serializable
data class NutritionDailyRecord(
    val date: Int,
    val time: String,
    val volume: Float
)

@Serializable
data class SwitchDishCheckboxRequest(
    val menuItemId: Int,
    val check: Boolean
)

@Serializable
data class SwitchDishCheckboxResponse(
    val calories: Float
)

@Serializable
data class RemoveMenuItemResponse(
    val calories: Float,
)

@Serializable
data class SaveMenuRequest(
    val meals: List<SaveMenuMeal>,
    val params: Params
)

@Serializable
data class SaveMenuMeal(
    val name: String,
    val dishes: List<Int>
)

@Serializable
data class SaveMenuResponse(
    val success: Boolean,
    val data: String
)

@Serializable
data class GetMenuResponse(
    val meals: List<GetMeal>,
    val params: Params
)

@Serializable
data class GetMeal(
    val mealId: Long,
    val name: String,
    val dishes: List<GetDish>,
    val params: Params
)

@Serializable
data class GetDish(
    val id: Int,
    val name: String,
    val description: String,
    val imageUrl: String?,
    val portionsCount: Int,
    val calories: Double,
    val protein: Double,
    val fat: Double,
    val carbohydrates: Double,
    val timeToCook: Int,
    val typeId: Int,
    val menuItemId: Int,
    val checked: Boolean
)

@Serializable
data class GeneratedMenuRequest(
    val calories: Int,
    val meals: List<MealRequest>
)

@Serializable
data class MealRequest(
    val name: String,
    val size: Double,
    val type: Int
)

@Serializable
data class GeneratedMenuResponse(
    val meals: List<GeneratedMeal>,
    val params: Params,
    val idealParams: IdealParams
)

@Serializable
data class GeneratedMeal(
    val name: String,
    val dishes: List<GeneratedDish>,
    val params: Params,
    val idealParams: Params
)

@Serializable
data class GeneratedDish(
    val id: Int,
    val name: String,
    val logo: String?,
    val calories: Double,
    val protein: Double,
    val fat: Double,
    val carbohydrates: Double,
    val timeToCook: Int,
    val typeId: Int
)

@Serializable
data class Params(
    val calories: Double = 0.0,
    val protein: Double = 0.0,
    val fat: Double = 0.0,
    val carbohydrates: Double = 0.0
)

@Serializable
data class IdealParams(
    val calories: Double = 0.0,
    val minProtein: Double = 0.0,
    val maxProtein: Double = 0.0,
    val minFat: Double = 0.0,
    val maxFat: Double = 0.0,
    val minCarbohydrates: Double = 0.0,
    val maxCarbohydrates: Double = 0.0
)

interface NutritionRepository {
    suspend fun logNutrition(nutritionLog: NutritionDailyRecord): NutritionLogResponse?
    suspend fun getDailyNutritionStats(date: NutritionStatRequest): NutritionStatResponse?
    suspend fun getGeneratedMenu(date: GeneratedMenuRequest): GeneratedMenuResponse?
    suspend fun saveGeneratedMenu(date: SaveMenuRequest): SaveMenuResponse?
    suspend fun getMenu(): GetMenuResponse?
    suspend fun switchDishCheckbox(date: SwitchDishCheckboxRequest): SwitchDishCheckboxResponse?
    suspend fun removeMenuItem(menuItemId: Int): RemoveMenuItemResponse?
}