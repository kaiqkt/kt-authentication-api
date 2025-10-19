package com.kaiqkt.authentication.unit.domain.dtos

import com.kaiqkt.authentication.domain.dtos.AuthorizationTokenDto
import io.azam.ulidj.ULID

object AuthorizationTokenDtoSampler {
    fun sampleRefresh() = AuthorizationTokenDto.Refresh(
        clientId = ULID.random(),
        refreshToken = "refresh-token"
    )

    fun samplePassword() = AuthorizationTokenDto.Password(
        clientId = ULID.random(),
        email = "email",
        password = "strong-password"
    )
}