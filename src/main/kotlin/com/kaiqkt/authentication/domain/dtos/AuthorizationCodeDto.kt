package com.kaiqkt.authentication.domain.dtos

sealed class AuthorizationCodeDto {
    data class Create(
        val email: String,
        val password: String,
        val redirectUri: String,
        val codeChallenge: String,
        //val clientId: String
    )
}