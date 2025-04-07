package com.rmp.data.repository.settings

import com.rmp.data.AnyResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserSettingsDto(
    val id: Int,
    val name: String,
    val email: String,
    val height: Double,
    val weight: Double,
    @SerialName("activityType") val activityType: String,
    @SerialName("goalType") val goalType: String,
    @SerialName("isMale") val isMale: Boolean,
    val age: Int,
    @SerialName("nickName") val nickName: String
)

@Serializable
data class UpdateSettingsRequest(
    val name: String? = null,
    val email: String? = null,
    @SerialName("isMale") val isMale: Boolean? = null,
    val age: Int? = null,
    val password: String? = null,
    val height: Double? = null,
    val weight: Double? = null,
    @SerialName("activityType") val activityType: String? = null,
    @SerialName("goalType") val goalType: String? = null,
    @SerialName("nickname") val nickName: String? = null,
    val date: Int
)

@Serializable
data class UpdateSettingsResponse(
    val id: Int? = null,
    val code: Int? = null,
    val status: String? = null,
    val message: String? = null
)

interface SettingsRepository {
    suspend fun getSettings(): UserSettingsDto?
    suspend fun updateSettings(request: UpdateSettingsRequest): UpdateSettingsResponse?
}
