package com.kaiqkt.authentication.unit.application.web.requests

import com.kaiqkt.authentication.application.web.requests.ClientRequestV1

object ClientRequestV1Sampler {
    fun sample(
        name: String = "name",
        description: String? = null,
        policies: Set<String> = setOf(),
    ) = ClientRequestV1(
        name = name,
        description = description,
        policies = policies,
    )
}
