package com.rmp.data.repository.achievements

import com.rmp.data.ApiClient
import com.rmp.data.repository.forum.PostDto
import com.rmp.data.successOr

class AchievementsRepoImpl: AchievementsRepository {
    override suspend fun fetchAchievements(): AchievementsDto? =
        ApiClient.authorizedRequest<AchievementsDto>(ApiClient.Method.GET, "user/stat").successOr(null)

    override suspend fun shareAchievement(shareAchievementDto: ShareAchievementDto): PostDto? =
        ApiClient.authorizedRequest<PostDto>(ApiClient.Method.POST, "social/share/achievement").successOr(null)
}