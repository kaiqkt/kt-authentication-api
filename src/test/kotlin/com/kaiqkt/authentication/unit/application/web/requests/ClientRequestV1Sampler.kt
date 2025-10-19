package com.kaiqkt.authentication.unit.application.web.requests

import com.kaiqkt.authentication.application.web.requests.ClientRequestV1
import com.kaiqkt.authentication.domain.models.enums.AuthenticationType

object ClientRequestV1Sampler {
    fun sample(
        name: String = "name",
        description: String? = null,
        resourceServers: Set<String> = setOf()
    ) = ClientRequestV1(
        name = name,
        description = description,
        resourceServers = resourceServers
    )
}