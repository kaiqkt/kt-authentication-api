package com.kaiqkt.authentication.application.web.handler

import com.kaiqkt.authentication.application.exceptions.InvalidRequestException
import com.kaiqkt.authentication.application.web.responses.ErrorV1
import com.kaiqkt.authentication.domain.exceptions.DomainException
import com.kaiqkt.authentication.domain.exceptions.ErrorType
import jakarta.validation.ConstraintViolationException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingRequestHeaderException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@ControllerAdvice
class ErrorHandler : ResponseEntityExceptionHandler() {
    companion object {
        private const val INVALID_REQUEST = "INVALID_REQUEST"
        private const val INVALID_REQUEST_MESSAGE = "Invalid request"
    }

    @ExceptionHandler(DomainException::class)
    fun handleDomainException(ex: DomainException): ResponseEntity<ErrorV1> {
        val error = ErrorV1(ex.type.name, ex.message, mapOf())

        return ResponseEntity(error, getStatusCode(ex.type))
    }

    @ExceptionHandler(MissingRequestHeaderException::class)
    fun handleMissingRequestHeaderException(ex: MissingRequestHeaderException): ResponseEntity<ErrorV1> {
        val error = ErrorV1(INVALID_REQUEST, INVALID_REQUEST_MESSAGE, mapOf(ex.headerName to "required header"))

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error)
    }

    @ExceptionHandler(InvalidRequestException::class)
    fun handleInvalidRequestException(ex: InvalidRequestException): ResponseEntity<ErrorV1> {
        val error = ErrorV1(INVALID_REQUEST, INVALID_REQUEST_MESSAGE, ex.errors)

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error)
    }

    public override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest,
    ): ResponseEntity<Any> {
        val details = ex.bindingResult.fieldErrors.associate { it.field to (it.defaultMessage ?: "invalid") }

        val error = ErrorV1(INVALID_REQUEST, INVALID_REQUEST_MESSAGE, details)

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error)
    }

    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolationException(ex: ConstraintViolationException): ResponseEntity<ErrorV1> {
        val details =
            ex.constraintViolations.associate { v ->
                val path = v.propertyPath.joinToString(".") { it.name }
                path to v.message
            }

        val error = ErrorV1(INVALID_REQUEST, INVALID_REQUEST_MESSAGE, details)

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error)
    }

    private fun getStatusCode(type: ErrorType): HttpStatus =
        when (type) {
            ErrorType.INVALID_TOKEN -> HttpStatus.UNAUTHORIZED
            ErrorType.EXPIRED_TOKEN -> HttpStatus.UNAUTHORIZED
            ErrorType.EMAIL_ALREADY_IN_USE -> HttpStatus.CONFLICT
            ErrorType.INVALID_CREDENTIALS -> HttpStatus.UNAUTHORIZED
            ErrorType.USER_NOT_FOUND -> HttpStatus.NOT_FOUND
            ErrorType.SESSION_NOT_FOUND -> HttpStatus.NOT_FOUND
            ErrorType.INVALID_FIELD -> HttpStatus.BAD_REQUEST
            ErrorType.RESOURCE_SERVER_NOT_FOUND -> HttpStatus.NOT_FOUND
            ErrorType.PERMISSION_ALREADY_EXISTS -> HttpStatus.CONFLICT
            ErrorType.PERMISSION_NOT_FOUND -> HttpStatus.NOT_FOUND
            ErrorType.ROLE_NOT_FOUND -> HttpStatus.NOT_FOUND
            ErrorType.ROLE_ALREADY_EXISTS -> HttpStatus.CONFLICT
            ErrorType.POLICY_ALREADY_EXISTS -> HttpStatus.CONFLICT
            ErrorType.POLICY_NOT_FOUND -> HttpStatus.NOT_FOUND
            ErrorType.CLIENT_NOT_FOUND -> HttpStatus.NOT_FOUND
        }
}
