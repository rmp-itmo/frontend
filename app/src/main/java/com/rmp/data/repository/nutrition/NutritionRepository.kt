package com.rmp.data.repository.nutrition

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName



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
data class SwitchDishCheckboxResponse(
    val calories: Float
)

@Serializable
data class RemoveMenuItemRequest(
    val menuItemId: Long,
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
data class Menu(
    val meals: List<GetMeal>? = null,
    val targetCalories: Double,
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
    //Default values for history fetch request
    val menuItemId: Long = 0L,
    val checked: Boolean = false
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
data class GeneratedMenu(
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

@Serializable
data class NutritionHistory(
    val caloriesTarget: Float,
    val date: Int,
    val dishes: Map<String, List<GetDish>>
)

@Serializable
data class FilterDto(
    @SerialName("nameFilter")
    val nameFilter: String,
    @SerialName("typeId")
    val typeId: Int
)


@Serializable
data class SearchResultDto(
    @SerialName("dishes")
    val dishes: List<Dish>,
    @SerialName("filter")
    val filter: FilterDto
) {
    @Serializable
    data class Dish(
        @SerialName("calories")
        val calories: Double,
        @SerialName("carbohydrates")
        val carbohydrates: Double,
        @SerialName("description")
        val description: String,
        @SerialName("fat")
        val fat: Double,
        @SerialName("id")
        val id: Long,
        @SerialName("imageUrl")
        val imageUrl: String,
        @SerialName("name")
        val name: String,
        @SerialName("portionsCount")
        val portionsCount: Int,
        @SerialName("protein")
        val protein: Double,
        @SerialName("timeToCook")
        val timeToCook: Int,
        @SerialName("typeId")
        val typeId: Int
    )
}

interface NutritionRepository {
    suspend fun loadDailyStats(date: NutritionStatRequest): NutritionStatResponse?
    suspend fun getGeneratedMenu(date: GeneratedMenuRequest): GeneratedMenu?
    suspend fun saveGeneratedMenu(date: SaveMenuRequest): SaveMenuResponse?
    suspend fun getMenu(): Menu?
    suspend fun switchDishCheckbox(date: SwitchDishCheckboxRequest): SwitchDishCheckboxResponse?
    suspend fun removeMenuItem(menuItemId: RemoveMenuItemRequest): RemoveMenuItemResponse?
    suspend fun getMenuStats(date: NutritionStatRequest): NutritionHistory?
    suspend fun getDish(filter: FilterDto): SearchResultDto?
}
