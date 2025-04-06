package com.rmp.data.repository.signup

import com.rmp.data.TokenDto
import kotlinx.serialization.Serializable

@Serializable
data class CreateUserDto(
    val name: String,
    val email: String,
    val password: String,
    val isMale: Boolean,
    val age: Int,
    val height: Double,
    val weight: Double,
    val activityType: Int,
    val goalType: Int,
    var registrationDate: Int = 0
)

@Serializable
data class UserLoginDto(
    val login: String,
    val password: String
)

@Serializable
data class UserDto(
    val name: String,
    val email: String,
    val isMale: Boolean,
    val age: Int,
    val height: Double,
    val weight: Double,
    val activityType: String,
    val goalType: String,
)

@Serializable
data class UserStatSummaryDto(
    val caloriesTarget: Double,
    val caloriesCurrent: Double,
    val waterTarget: Float,
    val waterCurrent: Float,
    val stepsTarget: Int,
    val stepsCurrent: Int,
    val sleepHours: Int,
    val sleepMinutes: Int,
    val heartRate: Int?,
    val glassesOfWater: Double
)

@Serializable
data class DateDto(
    val date: Int
)

interface UserRepository {
    suspend fun createUser(createUserDto: CreateUserDto): Boolean
    suspend fun loginUser(userLoginDto: UserLoginDto): TokenDto?
    suspend fun getMe(): UserDto?
    suspend fun getMeStatSummary(date: DateDto): UserStatSummaryDto?
}