package com.rmp.data.repository.settings

import com.rmp.data.ApiClient
import com.rmp.data.ApiException
import com.rmp.data.getIfException
import com.rmp.data.successOr

class SettingsRepoImpl : SettingsRepository {
    override suspend fun getSettings(): UserSettingsDto? {
        return ApiClient.authorizedRequest<UserSettingsDto>(
            ApiClient.Method.GET,
            "users"
        ).successOr(null)
    }

    override suspend fun updateSettings(request: UpdateSettingsRequest): Pair<UpdateSettingsSuccess?, ApiException?> {
        val response =ApiClient.authorizedRequest<UpdateSettingsSuccess>(
            ApiClient.Method.POST,
            "users/update",
            request
        )
        return response.successOr(null) to response.getIfException()
    }
}
