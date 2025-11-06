package com.kaiqkt.authentication.unit.application.web.requests

import com.kaiqkt.authentication.application.web.requests.ResourceServerRequestV1

object ResourceServerRequestV1Sampler {
    fun sample(
        name: String = "resource-server",
        description: String? = null,
    ) = ResourceServerRequestV1(
        name = name,
        description = description,
    )
}
