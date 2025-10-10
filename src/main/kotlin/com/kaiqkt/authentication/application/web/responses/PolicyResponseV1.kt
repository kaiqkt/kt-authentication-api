package com.kaiqkt.authentication.application.web.responses

import com.kaiqkt.authentication.domain.models.Permission
import com.kaiqkt.authentication.domain.models.Policy
import com.kaiqkt.authentication.domain.models.Role
import com.kaiqkt.authentication.domain.models.enums.Method

data class PolicyResponseV1(
    val uri: String,
    val method: Method,
    val isPublic: Boolean,
    val roles: List<String>,
    val permissions: List<String>
)

fun Policy.toResponseV1(): PolicyResponseV1 = PolicyResponseV1(
    uri = this.uri,
    method = this.method,
    isPublic =this.isPublic,
    roles = this.roles.map(Role::name),
    permissions = this.permissions.map(Permission::getResourceVerb)
)