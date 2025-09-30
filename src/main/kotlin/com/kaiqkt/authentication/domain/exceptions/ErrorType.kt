package com.kaiqkt.authentication.domain.exceptions

enum class ErrorType(val message: String) {
    INVALID_TOKEN("Invalid token"),
    EXPIRED_TOKEN("Expired token"),
    EMAIL_IN_USE("Email in use"),
    USER_NOT_FOUND("User not found"),
    INVALID_PASSWORD("Invalid password"),
    AUTHORIZATION_CODE_NOT_FOUND("Authorization code not found"),
    INVALID_CODE_CHALLENGE("Invalid code challenge"),
    INVALID_GRANT_TYPE_ARGUMENTS("Invalid grant type arguments"),
    SESSION_NOT_FOUND("Session not found")
}