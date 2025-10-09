package com.kaiqkt.authentication.unit.application.web.requests

import com.kaiqkt.authentication.application.web.requests.AuthenticationTokenRequestV1

object AuthenticationTokenRequestV1Sampler {
    fun sample(
        refreshToken: String? = "refresh-token",
        email: String? = "kt@kt.com",
        password: String? = "strong-password",
        grantType: String = "refresh_token"
    ) = AuthenticationTokenRequestV1(
        refreshToken = refreshToken,
        email = email,
        password = password,
        grantType = grantType
    )
}