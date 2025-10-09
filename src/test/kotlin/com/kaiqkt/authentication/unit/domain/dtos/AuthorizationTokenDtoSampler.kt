package com.kaiqkt.authentication.unit.domain.dtos

import com.kaiqkt.authentication.domain.dtos.AuthorizationTokenDto

object AuthorizationTokenDtoSampler {
    fun sampleRefresh() = AuthorizationTokenDto.Refresh(
        refreshToken = "refresh-token"
    )

    fun samplePassword() = AuthorizationTokenDto.Password(
        email = "email",
        password = "strong-password"
    )
}