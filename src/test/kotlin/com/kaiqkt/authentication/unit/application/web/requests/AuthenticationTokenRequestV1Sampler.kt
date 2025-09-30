package com.kaiqkt.authentication.unit.application.web.requests

import com.kaiqkt.authentication.application.web.requests.AuthorizationTokenRequestV1

object AuthenticationTokenRequestV1Sampler {
    fun sample(
        code: String? = "code",
        codeVerifier: String? = "code-challenge",
        refreshToken: String? = "refresh-token",
        redirectUri: String = "http://localhost:8080",
        grantType: String = "authorization_code"
    ) = AuthorizationTokenRequestV1(
        code = code,
        codeVerifier = codeVerifier,
        refreshToken = refreshToken,
        redirectUri = redirectUri,
        grantType = grantType
    )
}