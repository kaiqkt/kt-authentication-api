package com.kaiqkt.authentication.unit.domain.models

import com.kaiqkt.authentication.domain.models.Permission
import com.kaiqkt.authentication.domain.models.ResourceServer

object PermissionSampler {
    fun sample(
        resource: String = "authentication",
        verb: String = "view",
        resourceServer: ResourceServer = ResourceServerSampler.sample(),
    ) = Permission(
        resource = resource,
        verb = verb,
        resourceServer = resourceServer,
    )
}
