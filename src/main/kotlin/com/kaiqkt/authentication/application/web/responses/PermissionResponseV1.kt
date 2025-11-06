package com.kaiqkt.authentication.application.web.responses

import com.kaiqkt.authentication.domain.models.Permission

data class PermissionResponseV1(
    val id: String,
    val resource: String,
    val verb: String,
    val description: String? = null,
)

fun Permission.toResponseV1(): PermissionResponseV1 =
    PermissionResponseV1(
        id = this.id,
        resource = this.resource,
        verb = this.verb,
        description = this.description,
    )
