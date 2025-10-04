package com.kaiqkt.authentication.domain.exceptions

enum class ErrorType(val message: String) {
    INVALID_TOKEN("Invalid token"),
    EXPIRED_TOKEN("Expired token"),
    EMAIL_IN_USE("Email in use"),
    USER_NOT_FOUND("User not found"),
    INVALID_PASSWORD("Invalid password"),
    INVALID_GRANT_TYPE_ARGUMENTS("Invalid grant type arguments"),
    SESSION_NOT_FOUND("Session not found")
}