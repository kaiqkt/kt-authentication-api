package com.kaiqkt.authentication.unit.domain.dtos

import com.kaiqkt.authentication.domain.dtos.AuthorizationTokenDto
import com.kaiqkt.authentication.domain.dtos.enums.GrantType

object AuthorizationTokenDtoSampler {
    fun sampleCreate(
        code: String? = "code",
        codeVerifier: String? = "code-verifier",
        refreshToken: String? = "refresh-token",
        grantType: GrantType = GrantType.REFRESH_TOKEN
    ) = AuthorizationTokenDto.Create(
        code = code,
        codeVerifier = codeVerifier,
        redirectUri = "http://localhost/token",
        refreshToken = refreshToken,
        grantType = grantType
    )
}