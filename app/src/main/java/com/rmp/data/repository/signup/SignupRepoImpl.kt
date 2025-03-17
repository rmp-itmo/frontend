package com.rmp.data.repository.signup

import com.rmp.data.ApiClient
import com.rmp.data.baseUrl
import io.ktor.client.request.post
import io.ktor.util.InternalAPI


@OptIn(InternalAPI::class)
class SignupRepoImpl: SignupRepo {
    override suspend fun createUser(createUserDto: CreateUserDto): Boolean {
        val response = ApiClient.client.post("$baseUrl/users/signup") {
            body = createUserDto
        }

        return (response.status.value == 200)
    }
}