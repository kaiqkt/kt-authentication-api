package com.kaiqkt.authentication.application.web.responses

import com.kaiqkt.authentication.domain.dtos.IntrospectionDto

data class IntrospectResponseV1(
    val active: Boolean,
    val sid: String,
    val sub: String,
    val scope: String,
    val iss: String,
    val exp: Long,
    val iat: Long
)

fun IntrospectionDto.toResponseV1() = IntrospectResponseV1(
    active = this.active,
    sid = this.sid,
    sub = this.sub,
    scope = this.scope,
    iss = this.iss,
    exp = this.exp,
    iat = this.iat
)