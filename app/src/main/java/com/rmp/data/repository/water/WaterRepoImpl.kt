package com.rmp.data.repository.water

import com.rmp.data.ApiClient
import com.rmp.data.successOr

class WaterRepoImpl : WaterRepository {
    override suspend fun logWater(waterLog: WaterDailyRecord): WaterLogResponse? {
        val response = ApiClient.authorizedRequest<WaterLogResponse>(
            ApiClient.Method.POST,
            "users/log/water",
            waterLog
        )
        return response.successOr(null)
    }

    override suspend fun getDailyWaterStats(date: WaterStatRequest): WaterStatResponse? {
        val response = ApiClient.authorizedRequest<WaterStatResponse>(
            ApiClient.Method.POST,
            "users/stat/water",
                date
        )

        return response.successOr(null)
    }
}