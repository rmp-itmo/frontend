package com.rmp.data.repository.sleep

import com.rmp.data.repository.heart.GraphConfigurationDto
import com.rmp.data.repository.heart.GraphOutputDto
import kotlinx.serialization.Serializable


@Serializable
data class SleepResponseDto(
    val id: Int,
    val userId: Int,
    val hours: Int,
    val minutes: Int,
    val date: Int,
    val quality: Int
)

@Serializable
data class SleepUploadDto(
    val hours: Int,
    val minutes: Int,
    // YYYYMMDD
    val date: Int,
    val quality: Int
)


@Serializable
data class SleepGetHistoryDto(
    val dateFrom: Int,
    val dateTo: Int
)


@Serializable
data class SleepHistoryResponseDto(
    val sleepTarget: Double,
    val from: Int,
    val to: Int,
    val data: List<SleepResponseDto>
)

interface SleepRepository {
    suspend fun logSleep(sleep: SleepUploadDto): SleepResponseDto?
    suspend fun getSleepHistory(date: SleepGetHistoryDto): SleepHistoryResponseDto?
    suspend fun getGraphSleep(graphConfigurationDto: GraphConfigurationDto): GraphOutputDto?
}