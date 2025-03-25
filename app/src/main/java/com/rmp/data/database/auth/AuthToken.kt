package com.rmp.data.database.auth

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "auth_token")
data class AuthToken(
    @PrimaryKey val id: Int = 1,
    val accessToken: String,
    val refreshToken: String
)