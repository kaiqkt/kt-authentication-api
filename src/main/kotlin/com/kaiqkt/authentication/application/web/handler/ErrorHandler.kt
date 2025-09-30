package com.kaiqkt.authentication.application.web.handler

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
            ErrorType.EMAIL_IN_USE -> HttpStatus.CONFLICT
            ErrorType.INVALID_PASSWORD -> HttpStatus.UNAUTHORIZED
            ErrorType.AUTHORIZATION_CODE_NOT_FOUND -> HttpStatus.NOT_FOUND
            ErrorType.INVALID_CODE_CHALLENGE -> HttpStatus.UNAUTHORIZED
            ErrorType.INVALID_GRANT_TYPE_ARGUMENTS -> HttpStatus.BAD_REQUEST
            ErrorType.USER_NOT_FOUND -> HttpStatus.NOT_FOUND
            ErrorType.SESSION_NOT_FOUND -> HttpStatus.NOT_FOUND
        }
    }
}
