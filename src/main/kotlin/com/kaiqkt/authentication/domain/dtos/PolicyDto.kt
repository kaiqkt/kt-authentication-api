package com.kaiqkt.authentication.domain.dtos

import com.kaiqkt.authentication.domain.models.enums.Method

data class PolicyDto(
    val uri: String,
    val method: Method,
    val isPublic: Boolean
)
