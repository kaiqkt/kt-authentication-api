package com.kaiqkt.authentication.application.exceptions

class InvalidRequestException(val errors: Map<String, Any>): Exception()