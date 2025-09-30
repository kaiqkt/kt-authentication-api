package com.kaiqkt.authentication.unit.domain.models

import com.kaiqkt.authentication.domain.models.AuthorizationCode
import com.kaiqkt.authentication.domain.models.User
import com.kaiqkt.authentication.domain.utils.EncryptHelper
import java.time.LocalDateTime

object AuthorizationCodeSampler {
    fun sample(
        codeChallenge: String = EncryptHelper.encrypt("code-challenge"),
        user: User = UserSampler.sample()
    ) = AuthorizationCode(
        code = "123456",
        redirectUri = "http://localhost:8080",
        codeChallenge = codeChallenge,
        user = user,
        expireAt = LocalDateTime.now().plusSeconds(300)
    )
}