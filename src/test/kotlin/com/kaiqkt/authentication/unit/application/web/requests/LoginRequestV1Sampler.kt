package com.kaiqkt.authentication.unit.application.web.requests

import com.kaiqkt.authentication.application.web.requests.LoginRequestV1

object LoginRequestV1Sampler {
    fun sample(
        email: String = "kt@kt.com",
        password: String = "strong-password"
    ) = LoginRequestV1(email = email, password = password)
}