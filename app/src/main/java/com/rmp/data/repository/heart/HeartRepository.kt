package com.rmp.data.repository.heart

import kotlinx.serialization.Serializable

@Serializable
data class GraphConfigurationDto (
    val day: String? = null,
    val month: String? = null,
    val year: Int
)

@Serializable
data class GraphOutputDto (
    val avgValue: Double,
    val highestValue: Double,
    val lowestValue: Double,
    val points: Map<Int, Double>
)

@Serializable
data class HeartRateLogDto(
    val heartRate: Int,
    val date: Int,
    val time: Int
)

@Serializable
data class HeartRateId(
    val id: Int
)


interface HeartRepository {
    suspend fun getGraphHeart(graphConfigurationDto: GraphConfigurationDto): GraphOutputDto?

    suspend fun userHeartLog(heartRateLogDto: HeartRateLogDto): HeartRateId?
}