package com.kaiqkt.authentication.domain.dtos

import com.kaiqkt.authentication.domain.models.User

sealed class UserDto() {
    data class Create(val email: String, val password: String): UserDto()
}
fun UserDto.Create.toModel() = User(
    email = this.email
)