package com.kaiqkt.authentication.domain.dtos

data class PermissionDto(
    val resource: String,
    val verb: String,
    val description: String?
)
