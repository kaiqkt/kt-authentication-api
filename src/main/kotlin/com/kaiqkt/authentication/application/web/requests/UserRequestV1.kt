package com.kaiqkt.authentication.application.web.requests

import com.kaiqkt.authentication.domain.dtos.UserDto
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

data class UserRequestV1(
    @field:NotBlank(message = "input should not be blank")
    @field:Email(message = "must be a valid email")
    val email: String,

    @field:Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=]).{8,}$",
        message = "must be at least 8 characters long and include at least one letter, one special character, and one number"
    )
    val password: String
)

fun UserRequestV1.toDto() = UserDto.Create(
    email = this.email,
    password = this.password
)