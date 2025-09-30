package com.kaiqkt.authentication.application.web.responses

import com.kaiqkt.authentication.domain.dtos.AuthenticationDto

data class AuthorizationTokenResponse(
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String,
    val expiresIn: Long
)

fun AuthenticationDto.toResponseV1() = AuthorizationTokenResponse(
    accessToken = this.accessToken,
    refreshToken = this.refreshToken,
    expiresIn = this.expiresIn,
    tokenType = this.tokenType
)