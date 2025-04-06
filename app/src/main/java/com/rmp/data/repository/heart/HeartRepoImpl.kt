package com.rmp.data.repository.heart

import com.rmp.data.ApiClient
import com.rmp.data.successOr

class HeartRepoImpl: HeartRepository {
    override suspend fun getGraphHeart(graphConfigurationDto: GraphConfigurationDto): GraphOutputDto? {
        return ApiClient.authorizedRequest<GraphOutputDto>(ApiClient.Method.POST, "stat/graph/heart", graphConfigurationDto).successOr(null)
    }

    override suspend fun userHeartLog(heartRateLogDto: HeartRateLogDto): HeartRateId? {
        return ApiClient.authorizedRequest<HeartRateId>(ApiClient.Method.POST, "users/log/heart", heartRateLogDto).successOr(null)
    }
}