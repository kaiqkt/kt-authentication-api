package com.kaiqkt.authentication.unit.domain.models

import com.kaiqkt.authentication.domain.models.ResourceServer

object ResourceServerSampler {
    fun sample() =
        ResourceServer(
            name = "service-name",
            description = "my service",
        )
}
