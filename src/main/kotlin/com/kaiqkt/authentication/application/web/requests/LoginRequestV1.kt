package com.kaiqkt.authentication.application.web.requests

import jakarta.validation.constraints.NotBlank

data class LoginRequestV1(
    @field:NotBlank(message = "must not be blank")
    val email: String,
    @field:NotBlank(message = "must not be blank")
    val password: String
)
