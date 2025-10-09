package com.kaiqkt.authentication.unit.application.web.requests

import com.kaiqkt.authentication.application.web.requests.PermissionRequestV1

object PermissionRequestV1Sampler {
    fun sample(
        resource: String = "authentication",
        verb: String = "view",
        description: String? = null
    ) = PermissionRequestV1(
        resource = resource,
        verb = verb,
        description = description
    )
}