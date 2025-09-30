package com.kaiqkt.authentication.unit.domain.dtos

import com.kaiqkt.authentication.domain.dtos.AuthenticationDto

object AuthenticationDtoSampler {
    fun sample() = AuthenticationDto(
        accessToken = "access-token",
        refreshToken = "refresh-token",
        expiresIn = 300
    )
}