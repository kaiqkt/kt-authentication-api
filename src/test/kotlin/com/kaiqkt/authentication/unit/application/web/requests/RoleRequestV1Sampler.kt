package com.kaiqkt.authentication.unit.application.web.requests

import com.kaiqkt.authentication.application.web.requests.RoleRequestV1

object RoleRequestV1Sampler {
    fun sample(
        name: String = "ROLE_USER",
        description: String? = null,
    ) = RoleRequestV1(
        name = name,
        description = description,
    )
}
