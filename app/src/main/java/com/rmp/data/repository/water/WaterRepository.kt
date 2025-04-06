package com.rmp.data.repository.water

import kotlinx.serialization.Serializable

@Serializable
data class WaterLogResponse(
    val id: Int,
    val currentAmount: Float? = null,
    val dailyGoal: Int? = null,
    val logs: List<WaterDailyRecord>? = null
)

@Serializable
data class WaterStatRequest(
    val date: Int // Формат YYYYMMDD
)

@Serializable
data class WaterStatResponse(
    val waterTarget: Double,
    val water: List<WaterDailyRecord>
)

@Serializable
data class WaterDailyRecord(
    val date: Int,
    val time: String,
    val volume: Float
)

interface WaterRepository {
    suspend fun logWater(waterLog: WaterDailyRecord): WaterLogResponse?
    suspend fun getDailyWaterStats(date: WaterStatRequest): WaterStatResponse?
}