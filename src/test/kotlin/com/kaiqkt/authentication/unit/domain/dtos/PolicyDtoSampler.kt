package com.kaiqkt.authentication.unit.domain.dtos

import com.kaiqkt.authentication.domain.dtos.PolicyDto
import com.kaiqkt.authentication.domain.models.enums.Method

object PolicyDtoSampler {
    fun sample() = PolicyDto(
        uri = "/v1/user",
        method = Method.GET,
        isPublic = false
    )
}