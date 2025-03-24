package com.rmp.data

import kotlinx.serialization.Serializable

@Serializable
data class TokenDto(
    var accessToken: String? = null,
    var refreshToken: String? = null
)