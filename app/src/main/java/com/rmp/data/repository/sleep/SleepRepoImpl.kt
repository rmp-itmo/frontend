package com.rmp.data.repository.sleep

import com.rmp.data.ApiClient
import com.rmp.data.successOr


class SleepRepoImpl : SleepRepository {

    override suspend fun logSleep(sleep: SleepUploadDro): SleepResponseDto? {
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
}