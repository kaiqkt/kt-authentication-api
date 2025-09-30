package com.kaiqkt.authentication.unit.application.web.requests

import com.kaiqkt.authentication.application.web.requests.AuthorizationCodeRequestV1
import com.kaiqkt.authentication.domain.utils.EncryptHelper

object AuthorizationCodeRequestV1Sampler {
    fun sample(
        email: String = "kt@kt.com",
        password: String = "@Admin12345#",
        redirectUri: String = "http://localhost:8080",
        codeChallenge: String = EncryptHelper.encrypt("code-challenge")
    ) = AuthorizationCodeRequestV1(
        email = email,
        password = password,
        redirectUri = redirectUri,
        codeChallenge = codeChallenge
    )
}