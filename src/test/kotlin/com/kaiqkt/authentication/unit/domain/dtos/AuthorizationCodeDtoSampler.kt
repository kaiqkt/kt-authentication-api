package com.kaiqkt.authentication.unit.domain.dtos

import com.kaiqkt.authentication.domain.dtos.AuthorizationCodeDto

object AuthorizationCodeDtoSampler {
    fun sampleCreate() = AuthorizationCodeDto.Create(
        email = "kt@kt.com",
        password = "12345678",
        redirectUri = "http://localhost/oauth/token",
        codeChallenge = "code-challenge"
    )
}