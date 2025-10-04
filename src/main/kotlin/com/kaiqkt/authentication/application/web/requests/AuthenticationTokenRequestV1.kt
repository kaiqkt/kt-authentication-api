package com.kaiqkt.authentication.application.web.requests

import com.kaiqkt.authentication.domain.dtos.AuthorizationTokenDto
import com.kaiqkt.authentication.domain.dtos.enums.GrantType
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

data class AuthenticationTokenRequestV1(
    val refreshToken: String?,
    @field:NotBlank(message = "must not be blank")
    @field:Pattern(regexp = "refresh_token", message = "must be refresh_token")
    val grantType: String
)

fun AuthenticationTokenRequestV1.toDto() = AuthorizationTokenDto.Create(
    refreshToken = this.refreshToken,
    grantType = GrantType.valueOf(this.grantType.uppercase())
)