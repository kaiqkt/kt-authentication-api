package com.kaiqkt.authentication.unit.domain.dtos

import com.kaiqkt.authentication.domain.dtos.ResourceServerDto

object ResourceServerDtoSampler {
    fun sample() =
        ResourceServerDto(
            name = "resource-server",
            description = null,
        )
}
