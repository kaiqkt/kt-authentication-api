package com.kaiqkt.authentication.unit.application.web.requests

import com.kaiqkt.authentication.application.web.requests.UserRequestV1

object UserRequestV1Sampler {
    fun sample(
        email: String = "kt@kt.com",
        password: String = "@Admin1234#"
    ) = UserRequestV1(
        email = email,
        password = password
    )
}