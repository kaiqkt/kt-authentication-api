package com.kaiqkt.authentication.unit.domain.dtos

import com.kaiqkt.authentication.domain.dtos.ClientDto

object ClientDtoSampler {
    fun sample() = ClientDto(
        name = "client-name",
        description = "client-description",
        resourceServer = listOf("resource-server-id")
    )
}