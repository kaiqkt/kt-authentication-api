package com.kaiqkt.authentication.unit.domain.models

import com.kaiqkt.authentication.domain.models.Client
import com.kaiqkt.authentication.domain.models.Policy
import com.kaiqkt.authentication.domain.models.ResourceServer

object ClientSampler {
    fun sample(
        name: String = "client-name",
        description: String? = null,
        secret: String = "secret",
        policies: MutableSet<Policy> = mutableSetOf()
    ) = Client(
        name = name,
        description = description,
        secret = secret,
        policies = policies
    )
}