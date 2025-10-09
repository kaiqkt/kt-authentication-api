package com.kaiqkt.authentication.application.web.responses

import com.kaiqkt.authentication.domain.models.Role

data class RoleResponseV1(
    val id: String,
    val name: String,
    val description: String?,
    val permissions: List<PermissionResponseV1>
)

fun Role.toResponseV1() = RoleResponseV1(
    id = this.id,
    name = this.name,
    description = this.description,
    permissions = this.permissions.map { it.toResponseV1() }
)