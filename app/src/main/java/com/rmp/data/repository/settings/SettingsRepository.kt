package com.rmp.data.repository.settings

import kotlinx.serialization.Serializable

@Serializable
data class UserSettingsDto(
    val name: String,
    val email: String,
    val isMale: Boolean,
    val age: Int,
    val password: String,
    val height: Double,
    val weight: Double,
    val activityType: String,
    val goalType: String,
    val nickName: String
)

@Serializable
data class UpdateSettingsRequest(
    val name: String? = null,
    val email: String? = null,
    val isMale: Boolean? = null,
    val age: Int? = null,
    val password: String? = null,
    val height: Double? = null,
    val weight: Double? = null,
    val activityType: String? = null,
    val goalType: String? = null,
    val nickName: String? = null
)

interface SettingsRepository {
    suspend fun getSettings(): UserSettingsDto?
    suspend fun updateSettings(request: UpdateSettingsRequest): Boolean
}
