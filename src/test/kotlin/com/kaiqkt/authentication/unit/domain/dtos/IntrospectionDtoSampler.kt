package com.kaiqkt.authentication.unit.domain.dtos

import com.kaiqkt.authentication.domain.dtos.IntrospectDto
import io.azam.ulidj.ULID

object IntrospectionDtoSampler {
    fun sample() = IntrospectDto(
        active = true,
        sid = ULID.random(),
        sub = ULID.random(),
        iss = "kt-authentication-api",
        exp = 300L,
        iat = 300L
    )
}