package com.rmp.data.repository.nutrition

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
    val meals: List<Meal>,
    val params: Params,
    val idealParams: IdealParams
)

@Serializable
data class Meal(
    val name: String,
    val dishes: List<Dish>,
    val params: Params,
    val idealParams: Params
)

@Serializable
data class Dish(
    val id: Int,
    val name: String,
    val logo: String,
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

data class NutritionItem(
    val mainText: String,
    val value1: String,
    val value2: String,
    val value3: String
)

interface NutritionRepository {
    suspend fun logNutrition(nutritionLog: NutritionDailyRecord): NutritionLogResponse?
    suspend fun getDailyNutritionStats(date: NutritionStatRequest): NutritionStatResponse?
    suspend fun getGeneratedMenu(date: GeneratedMenuRequest): GeneratedMenuResponse?
}