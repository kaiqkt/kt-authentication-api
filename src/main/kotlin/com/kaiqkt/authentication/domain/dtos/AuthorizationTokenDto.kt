package com.kaiqkt.authentication.domain.dtos

sealed class AuthorizationTokenDto {
    data class Refresh(
        val clientId: String,
        val refreshToken: String
    ): AuthorizationTokenDto()

    data class Password(
        val clientId: String,
        val email: String,
        val password: String
    ): AuthorizationTokenDto()
}