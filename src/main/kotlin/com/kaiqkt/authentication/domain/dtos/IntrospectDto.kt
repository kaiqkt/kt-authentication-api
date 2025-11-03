package com.kaiqkt.authentication.domain.dtos

data class IntrospectDto(
    val active: Boolean,
    val sid: String,
    val sub: String,
    val iss: String,
    val exp: Long,
    val iat: Long,
    val roles: List<String>?,
    val permissions: List<String>?
)