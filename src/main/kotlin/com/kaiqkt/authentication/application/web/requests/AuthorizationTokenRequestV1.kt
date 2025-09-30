package com.kaiqkt.authentication.application.web.requests

import com.kaiqkt.authentication.domain.dtos.AuthorizationTokenDto
import com.kaiqkt.authentication.domain.dtos.enums.GrantType
import jakarta.validation.constraints.NotBlank

data class AuthorizationTokenRequestV1(
    val code: String?,
    val codeVerifier: String?,
    val refreshToken: String?,
    @field:NotBlank(message = "must no be blank")
    val redirectUri: String,
    @field:NotBlank(message = "must no be blank")
    val grantType: String
)

fun AuthorizationTokenRequestV1.toDto() = AuthorizationTokenDto.Create(
    code = this.code,
    codeVerifier = this.codeVerifier,
    redirectUri = this.redirectUri,
    refreshToken = this.refreshToken,
    grantType = GrantType.valueOf(this.grantType.uppercase())
)