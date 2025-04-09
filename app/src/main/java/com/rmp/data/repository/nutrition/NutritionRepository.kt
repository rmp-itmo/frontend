package com.rmp.data.repository.nutrition

import android.net.Uri
import kotlinx.serialization.Serializable

@Serializable
data class NutritionStatRequest(
    val date: Int // Формат YYYYMMDD
)

@Serializable
data class NutritionStatResponse(
    val caloriesTarget: Float,
    val caloriesCurrent: Float,
    val waterTarget: Float,
    val waterCurrent: Float,
    val stepsTarget: Float,
    val stepsCurrent: Float,
    val sleepHours: Float,
    val sleepMinutes: Float,
    val heartRate: Float?,
    val glassesOfWater: Float
)

@Serializable
data class SwitchDishCheckboxRequest(
    val menuItemId: Long,
    val check: Boolean
)

@Serializable
data class AddRemoveSelectResponse(
    val calories: Float
)

@Serializable
data class RemoveMenuItemRequest(
    val menuItemId: Long,
)

@Serializable
data class SaveMenuRequest(
    val meals: List<SaveMenuMeal>,
    val params: Params
)

@Serializable
data class SaveMenuMeal(
    val name: String,
    val dishes: List<Long>
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
    val id: Long,
    val name: String,
    val description: String,
    val imageUrl: String?,
    val portionsCount: Int,
    val calories: Double,
    val protein: Double,
    val fat: Double,
    val carbohydrates: Double,
    val timeToCook: Long,
    val typeId: Long,
    val menuItemId: Long,
    val checked: Boolean
)

@Serializable
data class GeneratedMenuRequest(
    val calories: Float,
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
    val id: Long,
    val name: String,
    val logo: String?,
    val calories: Double,
    val protein: Double,
    val fat: Double,
    val carbohydrates: Double,
    val timeToCook: Long,
    val typeId: Long
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

@Serializable
data class NutritionHistoryStatResponse(
    val caloriesTarget: Float,
    val date: Int,
    val dishes: Map<String, List<GetDish>>
)

@Serializable
data class AddMenuItemRequest(
    val mealId: Long,
    val newDish: AddMenuDish
)

@Serializable
data class AddMenuDish(
    val name: String,
    val description: String,
    val image: String?,
    val imageName: String?,
    val portionsCount: Int,
    val calories: Double,
    val protein: Double,
    val fat: Double,
    val carbohydrates: Double,
    val timeToCook: Long,
    val typeId: Long
)

interface NutritionRepository {
    suspend fun loadDailyStats(date: NutritionStatRequest): NutritionStatResponse?
    suspend fun getGeneratedMenu(date: GeneratedMenuRequest): GeneratedMenuResponse?
    suspend fun saveGeneratedMenu(date: SaveMenuRequest): SaveMenuResponse?
    suspend fun getMenu(): GetMenuResponse?
    suspend fun switchDishCheckbox(date: SwitchDishCheckboxRequest): AddRemoveSelectResponse?
    suspend fun removeMenuItem(menuItemId: RemoveMenuItemRequest): AddRemoveSelectResponse?
    suspend fun getMenuStats(date: NutritionStatRequest): NutritionHistoryStatResponse?
    suspend fun addMenuItem(date: AddMenuItemRequest): AddRemoveSelectResponse?
}
