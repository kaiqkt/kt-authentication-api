package com.kaiqkt.authentication.unit.domain.models

import com.kaiqkt.authentication.domain.models.Permission

object PermissionSampler {
    fun sample(
        resource: String = "authentication",
        verb: String = "view",
    ) = Permission(
        resource = resource,
        verb = verb,
    )
}
