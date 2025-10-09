package com.kaiqkt.authentication.domain.dtos

sealed class AuthorizationTokenDto {
    data class Refresh(
        val refreshToken: String
    ): AuthorizationTokenDto()

    data class Password(
        val email: String,
        val password: String
    ): AuthorizationTokenDto()
}