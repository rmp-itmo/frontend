package com.rmp.data.repository.steps

import kotlinx.serialization.Serializable

@Serializable
data class UserStepsLogDto(
    val count: Int
)

interface StepsRepository {
    suspend fun userStepsLog(userStepsLogDto: UserStepsLogDto): UserStepsLogDto?
}