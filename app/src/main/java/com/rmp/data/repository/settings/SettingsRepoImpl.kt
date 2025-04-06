package com.rmp.data.repository.settings;

import com.rmp.data.AnyResponse
import com.rmp.data.ApiClient
import com.rmp.data.isSuccess
import com.rmp.data.successOr

class SettingsRepositoryImpl : SettingsRepository {
    override suspend fun getSettings(): UserSettingsDto? {
        return ApiClient.authorizedRequest<UserSettingsDto>(
            ApiClient.Method.GET,
            "users"
        ).successOr(null)
    }

    override suspend fun updateSettings(request: UpdateSettingsRequest): Boolean {
        val response = ApiClient.authorizedRequest<AnyResponse>(
            ApiClient.Method.POST,
            "users/update",
            request
        )
        return response.isSuccess()
    }
}
