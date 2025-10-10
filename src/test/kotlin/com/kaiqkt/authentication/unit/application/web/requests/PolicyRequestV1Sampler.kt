package com.kaiqkt.authentication.unit.application.web.requests

import com.kaiqkt.authentication.application.web.requests.PolicyRequestV1

object PolicyRequestV1Sampler {
    fun sample(
        uri: String = "/users",
        method: String = "POST",
        isPublic: Boolean = false
    ) = PolicyRequestV1(
        uri = uri,
        method = method,
        isPublic = isPublic
    )
}