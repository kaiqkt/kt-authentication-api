package com.kaiqkt.authentication.domain.dtos

sealed class AuthorizationDto {
    data class Refresh(
        val clientId: String,
        val refreshToken: String
    ): AuthorizationDto()

    data class Password(
        val clientId: String,
        val email: String,
        val password: String
    ): AuthorizationDto()
}