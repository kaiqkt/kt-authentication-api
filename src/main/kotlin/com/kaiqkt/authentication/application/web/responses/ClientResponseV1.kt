package com.kaiqkt.authentication.application.web.responses

import com.kaiqkt.authentication.domain.models.Client

data class ClientResponseV1(
    val id: String,
    val name: String,
    val description: String?,
    val resourceServer: List<ResourceServerResponseV1>
)

fun Client.toResponseV1(): ClientResponseV1 = ClientResponseV1(
    id = this.id,
    name = this.name,
    description = this.description,
    resourceServer = this.resourceServers.map { it.toResponseV1() }
)