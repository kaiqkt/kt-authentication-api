package com.kaiqkt.authentication.unit.domain.dtos

import com.kaiqkt.authentication.domain.dtos.RoleDto

object RoleDtoSampler {
    fun sample(
        name: String = "ROLE_USER",
        description: String? = null
    ) = RoleDto(
        name = name,
        description = description
    )
}