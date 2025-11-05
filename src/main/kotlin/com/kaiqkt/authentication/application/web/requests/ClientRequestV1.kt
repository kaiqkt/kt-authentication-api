package com.kaiqkt.authentication.application.web.requests

import com.kaiqkt.authentication.domain.dtos.ClientDto
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Size

data class ClientRequestV1(
    @field:NotBlank(message = "must not be blank")
    @field:Size(max = 50, message = "must not exceed 50 characters")
    val name: String,

    @field:Size(max = 255, message = "must not exceed 255 characters")
    val description: String?,

    @field:NotEmpty(message = "must not be empty")
    val policies: Set<String>
)

fun ClientRequestV1.toDto(): ClientDto = ClientDto(
    name = this.name,
    description = this.description,
    policies = this.policies.toList()
)