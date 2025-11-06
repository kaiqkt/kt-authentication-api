package com.kaiqkt.authentication.application.web.responses

data class ErrorV1(
    val type: String,
    val message: String?,
    val details: Map<String, Any>,
)
