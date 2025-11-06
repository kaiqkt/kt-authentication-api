package com.kaiqkt.authentication.unit.domain.dtos

import com.kaiqkt.authentication.domain.models.Permission
import com.kaiqkt.authentication.domain.models.Policy
import com.kaiqkt.authentication.domain.models.ResourceServer
import com.kaiqkt.authentication.domain.models.Role
import com.kaiqkt.authentication.domain.models.enums.Method
import com.kaiqkt.authentication.unit.domain.models.PermissionSampler
import com.kaiqkt.authentication.unit.domain.models.RoleSampler

object PolicySampler {
    fun sample(
        uri: String = "/v1/user",
        method: Method = Method.GET,
        isPublic: Boolean = false,
        resourceServer: ResourceServer = ResourceServer(),
        permission: Permission = PermissionSampler.sample(),
        role: Role = RoleSampler.sample(),
    ) = Policy(
        uri = uri,
        method = method,
        isPublic = isPublic,
        resourceServer = resourceServer,
    ).apply {
        permissions.add(permission)
        roles.add(role)
    }
}
