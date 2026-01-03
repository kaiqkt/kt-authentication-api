package com.kaiqkt.authentication.domain.dtos

sealed class AuthorizationDto {
    data class Refresh(
        val refreshToken: String,
    ) : AuthorizationDto()

    data class Password(
        val email: String,
        val password: String,
    ) : AuthorizationDto()
}
