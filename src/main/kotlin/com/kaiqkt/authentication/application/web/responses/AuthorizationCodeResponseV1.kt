package com.kaiqkt.authentication.application.web.responses

import com.kaiqkt.authentication.domain.models.AuthorizationCode

data class AuthorizationCodeResponseV1(
    val code: String,
    val redirectUri: String,
    val codeChallenge: String
)

fun AuthorizationCode.toResponseV1() = AuthorizationCodeResponseV1(
    code = this.code,
    redirectUri = this.redirectUri,
    codeChallenge = this.codeChallenge
)