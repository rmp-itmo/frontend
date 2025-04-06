package com.rmp.data.repository.sleep

import com.rmp.data.ApiClient
import com.rmp.data.repository.heart.GraphConfigurationDto
import com.rmp.data.repository.heart.GraphOutputDto
import com.rmp.data.successOr


class SleepRepoImpl : SleepRepository {

    override suspend fun logSleep(sleep: SleepUploadDto): SleepResponseDto? {
        val response = ApiClient.authorizedRequest<SleepResponseDto>(
            ApiClient.Method.POST,
            "sleep",
            sleep
        )
        return response.successOr(null)
    }


    override suspend fun getSleepHistory(date: SleepGetHistoryDto): SleepHistoryResponseDto? {
        val response = ApiClient.authorizedRequest<SleepHistoryResponseDto>(
            ApiClient.Method.POST,
            "sleep/history",
            date
        )
        return response.successOr(null)
    }

    override suspend fun getGraphSleep(graphConfigurationDto: GraphConfigurationDto): GraphOutputDto? {
        return ApiClient.authorizedRequest<GraphOutputDto>(ApiClient.Method.POST, "stat/graph/sleep", graphConfigurationDto).successOr(null)
    }
}