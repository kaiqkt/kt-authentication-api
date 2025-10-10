package com.kaiqkt.authentication.application.web.requests

import com.kaiqkt.authentication.domain.dtos.PermissionDto
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class PermissionRequestV1(
    @field:Size(message = "must not exceed 25 characters", max = 25)
    @field:Pattern(
        regexp = "^[A-Za-z_]+$",
        message = "must contains letter or underlines"
    )
    val resource: String,

    @field:Size(message = "must not exceed 25 characters", max = 25)
    @field:Pattern(
        regexp = "^[A-Za-z_]+$",
        message = "must contains letter or underlines"
    )
    val verb: String,

    @field:Size(message = "must not exceed 255 characters", max = 255)
    val description: String? = null
)

fun PermissionRequestV1.toDto(): PermissionDto = PermissionDto(
    resource = this.resource,
    verb = this.verb,
    description = this.description
)