package com.kaiqkt.authentication.domain.exceptions

enum class ErrorType(val message: String) {
    INVALID_TOKEN("Token sign or parsed are invalid"),
    EXPIRED_TOKEN("Expired token"),
    EMAIL_ALREADY_IN_USE("Email already in use"),
    USER_NOT_FOUND("User not found"),
    INVALID_CREDENTIALS("Invalid credentials"),
    SESSION_NOT_FOUND("Session not found"),
    INVALID_FIELD("Field are not valid or does not exist"),
    RESOURCE_SERVER_NOT_FOUND("Resource not found"),
    PERMISSION_ALREADY_EXISTS("Permission with resource and verb already exists"),
    PERMISSION_NOT_FOUND("Permission not found"),
    ROLE_NOT_FOUND("Role not found"),
    ROLE_ALREADY_EXISTS("Role already exists with the given name"),
    POLICY_ALREADY_EXISTS("Policy already exists with uri and method for the given resource server"),
    POLICY_NOT_FOUND("Policy not found"),
    CLIENT_NOT_FOUND("Client not found"),
}