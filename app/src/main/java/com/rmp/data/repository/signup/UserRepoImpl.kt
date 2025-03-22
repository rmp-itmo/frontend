package com.rmp.data.repository.signup

import com.rmp.data.ApiClient
import com.rmp.data.baseUrl
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class UserRepoImpl: UserRepository {
    override suspend fun createUser(createUserDto: CreateUserDto): Boolean {
        val response = ApiClient.client.post("$baseUrl/users/create") {
            setBody(createUserDto)
            headers {
                set("Content-Type", "application/json")
            }
        }

        return (response.status.value == 200)
    }
}