package com.rmp.data.repository.achievements

import com.rmp.data.repository.forum.PostDto
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName


@Serializable
data class ShareAchievementDto(
    @SerialName("achievementType")
    val achievementType: Int,
    @SerialName("current")
    val current: Int,
    @SerialName("percentage")
    val percentage: Int
)

@Serializable
data class AchievementsDto(
    @SerialName("calories")
    val calories: Achievement,
    @SerialName("sleep")
    val sleep: Achievement,
    @SerialName("steps")
    val steps: Achievement,
    @SerialName("water")
    val water: Achievement
) {
    @Serializable
    data class Achievement(
        @SerialName("current")
        val current: Int,
        @SerialName("max")
        val max: Int,
        @SerialName("percentage")
        val percentage: Int
    )
}


interface AchievementsRepository {
    suspend fun fetchAchievements(): AchievementsDto?

    suspend fun shareAchievement(shareAchievementDto: ShareAchievementDto): PostDto?
}