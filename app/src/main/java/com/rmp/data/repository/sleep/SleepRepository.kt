package com.rmp.data.repository.sleep

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
data class SleepUploadDro(
    val hours: Int,
    val minutes: Int,
    // YYYYMMDD
    val date: Int,
    val quality: Int
)


@Serializable
data class SleepGetHistoryDto(
    val dateFrom: Int,
    val dateTo: Int,
    val hours: Int,
    val minutes: Int
)


@Serializable
data class SleepHistoryResponseDto(
    val sleepTarget: Double,
    val from: Int,
    val to: Int,
    val data: List<SleepResponseDto>
)

interface SleepRepository {
    suspend fun logSleep(sleep: SleepUploadDro): SleepResponseDto?
    suspend fun getSleepHistory(date: SleepGetHistoryDto): SleepHistoryResponseDto?
}