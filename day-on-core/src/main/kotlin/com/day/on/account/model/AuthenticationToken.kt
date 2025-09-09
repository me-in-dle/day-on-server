package com.day.on.account.model

data class AuthenticationToken(
    val accessToken: String,
    val refreshToken: String,
)
