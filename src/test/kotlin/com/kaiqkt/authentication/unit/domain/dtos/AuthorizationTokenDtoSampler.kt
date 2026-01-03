package com.kaiqkt.authentication.unit.domain.dtos

import com.kaiqkt.authentication.domain.dtos.AuthorizationDto

object AuthorizationTokenDtoSampler {
    fun sampleRefresh() =
        AuthorizationDto.Refresh(
            refreshToken = "refresh-token",
        )

    fun samplePassword() =
        AuthorizationDto.Password(
            email = "email",
            password = "strong-password",
        )
}
