package com.kaiqkt.authentication.application.web.responses

import com.kaiqkt.authentication.domain.dtos.TokensDto

data class AuthenticationTokenResponseV1(
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String,
    val expiresIn: Long,
)

fun TokensDto.toResponseV1() =
    AuthenticationTokenResponseV1(
        accessToken = this.accessToken,
        refreshToken = this.refreshToken,
        expiresIn = this.expiresIn,
        tokenType = this.tokenType,
    )
