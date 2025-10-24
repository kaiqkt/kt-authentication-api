package com.kaiqkt.authentication.unit.domain.dtos

import com.kaiqkt.authentication.domain.dtos.TokensDto

object AuthenticationDtoSampler {
    fun sample() = TokensDto(
        accessToken = "access-token",
        refreshToken = "refresh-token",
        expiresIn = 300
    )
}