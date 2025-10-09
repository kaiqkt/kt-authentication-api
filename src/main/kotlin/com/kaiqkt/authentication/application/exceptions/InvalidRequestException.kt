package com.kaiqkt.authentication.application.exceptions

class InvalidRequestException(val errors: Map<String, String>): Exception()