package com.kaiqkt.authentication.application.web.requests

import com.kaiqkt.authentication.domain.dtos.PolicyDto
import com.kaiqkt.authentication.domain.models.enums.Method
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

data class PolicyRequestV1(
    @field:NotBlank(message = "must not be blank")
    val uri: String,

    @field:Pattern(regexp = "POST|GET|DELETE|PUT|PATCH", message = "must be POST, GET, DELETE, PUT or PATCH")
    val method: String,
    val isPublic: Boolean
)

fun PolicyRequestV1.toDto(): PolicyDto = PolicyDto(
    uri = this.uri,
    method = Method.valueOf(this.method),
    isPublic = this.isPublic,
)
