package com.kaiqkt.authentication.unit.domain.models

import com.kaiqkt.authentication.domain.models.Client
import com.kaiqkt.authentication.domain.models.ResourceServer

object ClientSampler {
    fun sample(
        name: String = "client-name",
        description: String? = null,
        secret: String = "secret",
        resourceServers: MutableSet<ResourceServer> = mutableSetOf()
    ) = Client(
        name = name,
        description = description,
        secret = secret,
        resourceServers = resourceServers
    )
}