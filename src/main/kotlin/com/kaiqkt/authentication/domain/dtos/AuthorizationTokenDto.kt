package com.kaiqkt.authentication.domain.dtos

import com.kaiqkt.authentication.domain.dtos.enums.GrantType

sealed class AuthorizationTokenDto {
    data class Create(
        val refreshToken: String?,
        val grantType: GrantType
    )
}