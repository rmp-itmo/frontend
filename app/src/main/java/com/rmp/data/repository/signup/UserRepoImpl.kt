package com.rmp.data.repository.signup

import android.media.session.MediaSession.Token
import androidx.annotation.AnyRes
import com.rmp.data.AnyResponse
import com.rmp.data.ApiClient
import com.rmp.data.TokenDto
import com.rmp.data.baseUrl
import com.rmp.data.isSuccess
import com.rmp.data.success
import com.rmp.data.successOr
import io.ktor.client.call.body
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class UserRepoImpl: UserRepository {
    override suspend fun createUser(createUserDto: CreateUserDto): Boolean {
        val response = ApiClient.unauthorizedRequest<AnyResponse>(ApiClient.Method.POST, "users/create", createUserDto)

        return response.isSuccess()
    }

    override suspend fun loginUser(userLoginDto: UserLoginDto): TokenDto? {
        val response = ApiClient.unauthorizedRequest<TokenDto>(ApiClient.Method.POST, "auth", userLoginDto)

        return response.successOr(null)
    }

    override suspend fun getMe(): UserDto {
        return ApiClient.authorizedRequest<UserDto>(ApiClient.Method.GET, "users").success()
    }
}