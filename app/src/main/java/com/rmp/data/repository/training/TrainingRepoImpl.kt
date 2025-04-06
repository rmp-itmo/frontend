package com.rmp.data.repository.training

import com.rmp.data.ApiClient
import com.rmp.data.successOr

class TrainingRepoImpl: TrainingRepository {
    override suspend fun fetchTypes(): TypesDto? =
        ApiClient.authorizedRequest<TypesDto>(ApiClient.Method.GET,"users/trainings/type").successOr(null)

    override suspend fun fetchIntensities(): IntensitiesDto? =
        ApiClient.authorizedRequest<IntensitiesDto>(ApiClient.Method.GET,"users/trainings/intensity").successOr(null)

    override suspend fun fetchTrainings(trainingFilterDto: TrainingFilterDto): TrainingListDto? =
        ApiClient.authorizedRequest<TrainingListDto>(ApiClient.Method.POST, "users/trainings/month", trainingFilterDto).successOr(null)

    override suspend fun logTraining(trainingLogDto: TrainingLogDto): TrainingLogSuccess? =
        ApiClient.authorizedRequest<TrainingLogSuccess>(ApiClient.Method.POST, "users/trainings/log", trainingLogDto).successOr(null)

    override suspend fun updateStepTarget(setStepsTargetDto: SetStepsTargetDto): TrainingLogSuccess? =
        ApiClient.authorizedRequest<TrainingLogSuccess>(ApiClient.Method.POST, "users/update/steps", setStepsTargetDto).successOr(null)
}