package com.kaiqkt.authentication.unit.domain.dtos

import com.kaiqkt.authentication.domain.dtos.AuthorizationDto
import io.azam.ulidj.ULID

object AuthorizationTokenDtoSampler {
    fun sampleRefresh() =
        AuthorizationDto.Refresh(
            clientId = ULID.random(),
            refreshToken = "refresh-token",
        )

    fun samplePassword() =
        AuthorizationDto.Password(
            clientId = ULID.random(),
            email = "email",
            password = "strong-password",
        )
}
