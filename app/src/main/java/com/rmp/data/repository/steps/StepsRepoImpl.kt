package com.rmp.data.repository.steps

import com.rmp.data.ApiClient
import com.rmp.data.successOr

class StepsRepoImpl: StepsRepository {
    override suspend fun userStepsLog(userStepsLogDto: UserStepsLogDto): UserStepsLogDto? {
        return ApiClient.authorizedRequest<UserStepsLogDto>(ApiClient.Method.POST, "users/log/steps", userStepsLogDto).successOr(null)
    }
}