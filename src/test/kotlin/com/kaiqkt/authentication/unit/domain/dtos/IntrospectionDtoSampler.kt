package com.kaiqkt.authentication.unit.domain.dtos

import com.kaiqkt.authentication.domain.dtos.IntrospectionDto
import io.azam.ulidj.ULID

object IntrospectionDtoSampler {
    fun sample() = IntrospectionDto(
        active = true,
        sid = ULID.random(),
        sub = ULID.random(),
        scope = " ",
        iss = "kt-authentication-api",
        exp = 300L,
        iat = 300L
    )
}