package com.rmp.data.database.auth

import androidx.room.Dao
import androidx.room.Query

@Dao
interface AuthTokenDao {

    @Query("UPDATE auth_token SET accessToken = :accessToken, refreshToken = :refreshToken WHERE id = 1")
    suspend fun saveTokens(accessToken: String, refreshToken: String)

    @Query("SELECT * FROM auth_token LIMIT 1")
    suspend fun getTokens(): AuthToken

    @Query("UPDATE auth_token SET accessToken = '', refreshToken = '' WHERE id = 1")
    suspend fun clearTokens()
}