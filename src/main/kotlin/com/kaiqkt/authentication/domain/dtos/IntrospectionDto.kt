package com.kaiqkt.authentication.domain.dtos

data class IntrospectionDto(
    val active: Boolean,
    val sid: String,
    val sub: String,
    val scope: String,
    val iss: String,
    val exp: Long,
    val iat: Long
)