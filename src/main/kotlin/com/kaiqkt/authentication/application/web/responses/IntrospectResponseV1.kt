package com.kaiqkt.authentication.application.web.responses

import com.kaiqkt.authentication.domain.dtos.IntrospectDto

data class IntrospectResponseV1(
    val active: Boolean,
    val sid: String,
    val sub: String,
    val iss: String,
    val exp: Long,
    val iat: Long
)

fun IntrospectDto.toResponseV1() = IntrospectResponseV1(
    active = this.active,
    sid = this.sid,
    sub = this.sub,
    iss = this.iss,
    exp = this.exp,
    iat = this.iat
)