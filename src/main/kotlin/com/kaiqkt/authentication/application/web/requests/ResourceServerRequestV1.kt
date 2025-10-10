package com.kaiqkt.authentication.application.web.requests

import com.kaiqkt.authentication.domain.dtos.ResourceServerDto
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class ResourceServerRequestV1(
    @field:NotBlank(message = "input should not be blank")
    @field:Size(message = "must not exceed 50 characters", max = 50)
    val name: String,
    @field:Size(message = "must not exceed 255 characters", max = 255)
    val description: String? = null
)

fun ResourceServerRequestV1.toDto(): ResourceServerDto = ResourceServerDto(
    name = this.name,
    description = this.description
)
