package com.kaiqkt.authentication.application.web.requests

import com.kaiqkt.authentication.domain.dtos.AuthorizationCodeDto
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class AuthorizationCodeRequestV1(
    @field:NotBlank(message = "must not be blank")
    val email: String,

    @field:NotBlank(message = "must not be blank")
    val password: String,

    @field:NotBlank(message = "must not be blank")
    val redirectUri: String,
    //val clientId: String,
    @field:NotBlank(message = "must not be blank")
    val codeChallenge: String
)

fun AuthorizationCodeRequestV1.toDto() = AuthorizationCodeDto.Create(
    email = this.email,
    password = this.password,
    redirectUri = this.redirectUri,
    codeChallenge = this.codeChallenge
)