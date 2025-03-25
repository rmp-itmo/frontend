package com.rmp.data

import kotlinx.serialization.Serializable

@Serializable
data class TokenDto(
    var accessToken: String,
    var refreshToken: String
)