package com.kaiqkt.authentication.domain.dtos

data class IntrospectDto(
    val active: Boolean,
    val sid: String,
    val sub: String,
    val scope: String,
    val iss: String,
    val exp: Long,
    val iat: Long
)