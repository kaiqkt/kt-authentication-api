package com.kaiqkt.authentication.domain.dtos

data class ClientDto(
    val name: String,
    val description: String?,
    val resourceServer: List<String>
)
