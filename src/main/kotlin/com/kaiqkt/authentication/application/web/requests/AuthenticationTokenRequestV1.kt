package com.kaiqkt.authentication.application.web.requests

import com.kaiqkt.authentication.application.exceptions.InvalidRequestException
import com.kaiqkt.authentication.domain.dtos.AuthorizationTokenDto
import com.kaiqkt.authentication.domain.dtos.enums.GrantType
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

data class AuthenticationTokenRequestV1(
    val email: String?,
    val password: String?,
    val refreshToken: String?,
    @field:NotBlank(message = "must not be blank")
    @field:Pattern(regexp = "password|refresh_token", message = "must be refresh_token or password")
    val grantType: String
)

fun AuthenticationTokenRequestV1.toDto(): AuthorizationTokenDto {
    val grantType = GrantType.valueOf(this.grantType.uppercase())

    return when (grantType) {
        GrantType.REFRESH_TOKEN -> toRefreshDto()
        GrantType.PASSWORD -> toPasswordDto()
    }
}

private fun AuthenticationTokenRequestV1.toRefreshDto() = AuthorizationTokenDto.Refresh(
    refreshToken = this.refreshToken ?: throw InvalidRequestException(mapOf("refresh_token" to "must not be null"))
)

private fun AuthenticationTokenRequestV1.toPasswordDto() = AuthorizationTokenDto.Password(
    email = email.takeUnless { it.isNullOrBlank() } ?: throw InvalidRequestException(mapOf("email" to "must not be null")),
    password = password.takeUnless { it.isNullOrBlank() } ?: throw InvalidRequestException(mapOf("password" to "must not be null"))
)