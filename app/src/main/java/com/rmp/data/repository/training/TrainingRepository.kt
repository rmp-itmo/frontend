package com.rmp.data.repository.training

import com.rmp.data.AnyResponse
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName


@Serializable
data class SelectorItem(
    @SerialName("id")
    val id: Int,
    @SerialName("name")
    val name: String
)

@Serializable
data class IntensitiesDto(
    @SerialName("intensities")
    val intensities: List<SelectorItem>
)

@Serializable
data class TypesDto(
    @SerialName("types")
    val types: List<SelectorItem>
)


@Serializable
data class TrainingLogDto(
    @SerialName("date")
    val date: Int,
    @SerialName("end")
    val end: Int,
    @SerialName("intensity")
    val intensity: Int,
    @SerialName("start")
    val start: Int,
    @SerialName("type")
    val type: Int
)

@Serializable
data class TrainingLogSuccess(val id: Int)

@Serializable
data class TrainingFilterDto(
    @SerialName("date")
    val date: Int
)


@Serializable
data class TrainingListDto(
    @SerialName("stepsCurrent")
    val stepsCurrent: Int,
    @SerialName("stepsTarget")
    val stepsTarget: Int,
    @SerialName("trainings")
    val trainings: Map<String, List<Training>>
) {
    @Serializable
    data class Training(
        @SerialName("calories")
        val calories: Double,
        @SerialName("end")
        val end: String,
        @SerialName("id")
        val id: Int,
        @SerialName("intensity")
        val intensity: String,
        @SerialName("start")
        val start: String,
        @SerialName("type")
        val type: String
    )
}

@Serializable
data class SetStepsTargetDto(
    @SerialName("date")
    val date: Int,
    @SerialName("steps")
    val steps: Int
)

interface TrainingRepository {
    suspend fun fetchTypes(): TypesDto?

    suspend fun fetchIntensities(): IntensitiesDto?

    suspend fun fetchTrainings(trainingFilterDto: TrainingFilterDto): TrainingListDto?

    suspend fun logTraining(trainingLogDto: TrainingLogDto): TrainingLogSuccess?

    suspend fun updateStepTarget(setStepsTargetDto: SetStepsTargetDto): TrainingLogSuccess?
}