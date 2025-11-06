package com.kaiqkt.authentication.application.web.responses

import com.kaiqkt.authentication.domain.models.ResourceServer

data class ResourceServerResponseV1(
    val id: String,
    val name: String,
    val description: String? = null,
)

fun ResourceServer.toResponseV1(): ResourceServerResponseV1 =
    ResourceServerResponseV1(
        id = this.id,
        name = this.name,
        description = this.description,
    )
