package com.rmp.data.repository.water

import kotlinx.serialization.SerialName
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
    @SerialName("water")
    val records: List<WaterDailyRecord>
)

@Serializable
data class WaterDailyRecord(
    @SerialName("date")
    val date: Int,

    @SerialName("time")
    val time: String,

    @SerialName("volume")
    val volume: Float
)

interface WaterRepository {
    suspend fun logWater(waterLog: WaterDailyRecord): WaterLogResponse?
    suspend fun getDailyWaterStats(date: WaterStatRequest): WaterStatResponse?
}