package com.kaiqkt.authentication.domain.dtos

data class TokensDto(
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String = "bearer",
    val expiresIn: Long
)