package com.kaiqkt.authentication.application.web.requests

import com.kaiqkt.authentication.application.exceptions.InvalidRequestException
import com.kaiqkt.authentication.domain.dtos.AuthorizationDto
import com.kaiqkt.authentication.domain.dtos.enums.GrantType
import jakarta.validation.constraints.Pattern

data class AuthorizationRequestV1(
    val clientId: String?,
    val email: String?,
    val password: String?,
    val refreshToken: String?,
    @field:Pattern(regexp = "password|refresh_token", message = "must be refresh_token or password")
    val grantType: String,
)

fun AuthorizationRequestV1.toDto(): AuthorizationDto {
    val grantType = GrantType.valueOf(this.grantType.uppercase())

    return when (grantType) {
        GrantType.REFRESH_TOKEN -> toRefreshDto()
        GrantType.PASSWORD -> toPasswordDto()
    }
}

private fun AuthorizationRequestV1.toRefreshDto(): AuthorizationDto.Refresh {
    if (this.refreshToken.isNullOrBlank() || this.clientId.isNullOrBlank()) {
        val errors = mutableMapOf<String, Any>()

        this.refreshToken.takeUnless { it.isNullOrBlank() } ?: errors.put("refresh_token", "must not be null or blank")
        this.clientId.takeUnless { it.isNullOrBlank() } ?: errors.put("client_id", "must not be null or blank")

        throw InvalidRequestException(errors.toMap())
    }

    return AuthorizationDto.Refresh(
        refreshToken = this.refreshToken,
        clientId = this.clientId,
    )
}

private fun AuthorizationRequestV1.toPasswordDto(): AuthorizationDto.Password {
    if (email.isNullOrBlank() || password.isNullOrBlank() || clientId.isNullOrBlank()) {
        val errors = mutableMapOf<String, Any>()

        this.email.takeUnless { it.isNullOrBlank() } ?: errors.put("email", "must not be null or blank")
        this.clientId.takeUnless { it.isNullOrBlank() } ?: errors.put("client_id", "must not be null or blank")
        this.password.takeUnless { it.isNullOrBlank() } ?: errors.put("password", "must not be null or blank")

        throw InvalidRequestException(errors.toMap())
    }

    return AuthorizationDto.Password(
        email = this.email,
        password = this.password,
        clientId = this.clientId,
    )
}
