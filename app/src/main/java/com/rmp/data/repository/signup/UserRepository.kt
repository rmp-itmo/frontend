package com.rmp.data.repository.signup

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
)

@Serializable
data class UserLoginDto(
    val login: String,
    val password: String
)

interface UserRepository {
    suspend fun createUser(createUserDto: CreateUserDto): Boolean
    suspend fun loginUser(userLoginDto: UserLoginDto): Boolean
}