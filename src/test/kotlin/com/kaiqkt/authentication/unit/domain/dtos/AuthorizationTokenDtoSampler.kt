package com.kaiqkt.authentication.unit.domain.dtos

import com.kaiqkt.authentication.domain.dtos.AuthorizationTokenDto
import com.kaiqkt.authentication.domain.dtos.enums.GrantType

object AuthorizationTokenDtoSampler {
    fun sampleCreate(
        refreshToken: String? = "refresh-token",
        grantType: GrantType = GrantType.REFRESH_TOKEN
    ) = AuthorizationTokenDto.Create(
        refreshToken = refreshToken,
        grantType = grantType
    )
}