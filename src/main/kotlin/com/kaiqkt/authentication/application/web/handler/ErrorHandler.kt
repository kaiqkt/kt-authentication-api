package com.kaiqkt.authentication.application.web.handler

import com.kaiqkt.authentication.application.exceptions.InvalidRequestException
import com.kaiqkt.authentication.application.web.responses.ErrorV1
import com.kaiqkt.authentication.application.web.responses.InvalidArgumentErrorV1
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

    @ExceptionHandler(DomainException::class)
    fun handleDomainException(ex: DomainException): ResponseEntity<ErrorV1> {
        val error = ErrorV1(ex.type, ex.message)

        return ResponseEntity(error, getStatusCode(ex.type))
    }

    @ExceptionHandler(MissingRequestHeaderException::class)
    fun handleMissingRequestHeaderException(ex: MissingRequestHeaderException): ResponseEntity<InvalidArgumentErrorV1> {
        val error = InvalidArgumentErrorV1(errors = mapOf(ex.headerName to "required header"))

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error)
    }

    @ExceptionHandler(InvalidRequestException::class)
    fun handleInvalidRequestException(ex: InvalidRequestException): ResponseEntity<InvalidArgumentErrorV1> {
        val error = InvalidArgumentErrorV1(errors = ex.errors)

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error)
    }

    public override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any> {
        val fieldErrs = ex.bindingResult.fieldErrors.associate { it.field to (it.defaultMessage ?: "invalid") }

        val error = InvalidArgumentErrorV1(errors = fieldErrs)

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error)
    }

    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolationException(ex: ConstraintViolationException): ResponseEntity<InvalidArgumentErrorV1> {
        val errors = ex.constraintViolations.associate { v ->
            val path = v.propertyPath.joinToString(".") { it.name }
            path to v.message
        }

        val error = InvalidArgumentErrorV1(errors = errors)

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error)
    }

    private fun getStatusCode(type: ErrorType): HttpStatus {
        return when (type) {
            ErrorType.INVALID_TOKEN -> HttpStatus.UNAUTHORIZED
            ErrorType.EXPIRED_TOKEN -> HttpStatus.UNAUTHORIZED
            ErrorType.EMAIL_ALREADY_IN_USE -> HttpStatus.CONFLICT
            ErrorType.INVALID_CREDENTIALS -> HttpStatus.UNAUTHORIZED
            ErrorType.USER_NOT_FOUND -> HttpStatus.NOT_FOUND
            ErrorType.SESSION_NOT_FOUND -> HttpStatus.NOT_FOUND
            ErrorType.INVALID_SORT_FIELD -> HttpStatus.BAD_REQUEST
            ErrorType.RESOURCE_SERVER_NOT_FOUND -> HttpStatus.NOT_FOUND
            ErrorType.PERMISSION_ALREADY_EXISTS -> HttpStatus.CONFLICT
            ErrorType.PERMISSION_NOT_FOUND -> HttpStatus.NOT_FOUND
            ErrorType.ROLE_NOT_FOUND -> HttpStatus.NOT_FOUND
            ErrorType.ROLE_ALREADY_EXISTS -> HttpStatus.CONFLICT
        }
    }
}
