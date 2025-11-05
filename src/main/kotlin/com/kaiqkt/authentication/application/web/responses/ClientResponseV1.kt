package com.kaiqkt.authentication.application.web.responses

import com.kaiqkt.authentication.domain.models.Client

data class ClientResponseV1(
    val id: String,
    val name: String,
    val description: String?,
    val policies: List<PolicyResponseV1>
)

fun Client.toResponseV1(): ClientResponseV1 = ClientResponseV1(
    id = this.id,
    name = this.name,
    description = this.description,
    policies = this.policies.map { it.toResponseV1() }
)