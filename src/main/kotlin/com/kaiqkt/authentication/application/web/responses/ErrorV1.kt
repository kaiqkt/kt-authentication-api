package com.kaiqkt.authentication.application.web.responses

import com.kaiqkt.authentication.domain.exceptions.ErrorType

data class ErrorV1(
    val type: ErrorType,
    val message: String?
)
