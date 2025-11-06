package com.kaiqkt.authentication.unit.domain.models

import com.kaiqkt.authentication.domain.models.Policy
import com.kaiqkt.authentication.domain.models.ResourceServer
import com.kaiqkt.authentication.domain.models.enums.Method

object PolicySampler {
    fun sample(resourceServer: ResourceServer = ResourceServerSampler.sample()) =
        Policy(
            uri = "/users",
            method = Method.POST,
            isPublic = false,
            resourceServer = resourceServer,
        )
}
