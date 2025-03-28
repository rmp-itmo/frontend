package com.rmp.data.repository.signup

import com.rmp.data.AnyResponse
import com.rmp.data.ApiClient
import com.rmp.data.TokenDto
import com.rmp.data.getCurrentDateAsNumber
import com.rmp.data.isSuccess
import com.rmp.data.successOr

class UserRepoImpl: UserRepository {
    override suspend fun createUser(createUserDto: CreateUserDto): Boolean {
        createUserDto.registrationDate = getCurrentDateAsNumber()
        val response = ApiClient.unauthorizedRequest<AnyResponse>(ApiClient.Method.POST, "users/create", createUserDto)

        return response.isSuccess()
    }

    override suspend fun loginUser(userLoginDto: UserLoginDto): TokenDto? {
        val response = ApiClient.unauthorizedRequest<TokenDto>(ApiClient.Method.POST, "auth", userLoginDto)

        return response.successOr(null)
    }

    override suspend fun getMe(): UserDto? {
        return ApiClient.authorizedRequest<UserDto>(ApiClient.Method.GET, "users").successOr(null)
    }
}