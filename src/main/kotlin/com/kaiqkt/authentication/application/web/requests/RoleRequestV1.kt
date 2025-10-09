package com.kaiqkt.authentication.application.web.requests

import com.kaiqkt.authentication.domain.dtos.RoleDto
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class RoleRequestV1(
    @field:Size(message = "must not exceed 25 characters", max = 25)
    @field:Pattern(
        regexp = "^[A-Za-z_]+$",
        message = "must contains letter or underlines"
    )
    val name: String,

    @field:Size(message = "must not exceed 255 characters", max = 255)
    val description: String? = null
)

fun RoleRequestV1.toDto() = RoleDto(
    name = this.name,
    description = this.description
)