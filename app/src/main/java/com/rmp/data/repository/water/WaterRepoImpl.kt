package com.rmp.data.repository.water

import android.util.Log
import com.rmp.data.ApiClient
import com.rmp.data.isSuccess
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
        return try {
            val response = ApiClient.authorizedRequest<WaterStatResponse>(
                ApiClient.Method.POST,
                "users/stat/water",
                date
            )

            if (!response.isSuccess()) {
                Log.e("WaterRepo", "Server error: $response")
                return null
            }

            response.successOr(null)?.also {
                Log.d("WaterRepo", "Received ${it.records.size} water records")
            }
        } catch (e: Exception) {
            Log.e("WaterRepo", "Network error", e)
            null
        }
    }
}