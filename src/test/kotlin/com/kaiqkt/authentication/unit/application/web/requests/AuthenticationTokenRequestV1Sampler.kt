package com.kaiqkt.authentication.unit.application.web.requests

import com.kaiqkt.authentication.application.web.requests.AuthorizationRequestV1
import io.azam.ulidj.ULID

object AuthenticationTokenRequestV1Sampler {
    fun sample(
        refreshToken: String? = "refresh-token",
        clientId: String? = ULID.random(),
        email: String? = "kt@kt.com",
        password: String? = "strong-password",
        grantType: String = "refresh_token",
    ) = AuthorizationRequestV1(
        refreshToken = refreshToken,
        clientId = clientId,
        email = email,
        password = password,
        grantType = grantType,
    )
}
