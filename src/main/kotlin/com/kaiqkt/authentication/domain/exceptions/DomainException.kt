package com.kaiqkt.authentication.domain.exceptions

class DomainException(
    val type: ErrorType,
) : Exception(type.message)
