package com.kaiqkt.authentication.domain.dtos

sealed class UserDto() {
    data class Create(val email: String, val password: String): UserDto()
}