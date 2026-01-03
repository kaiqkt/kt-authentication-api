package com.kaiqkt.authentication.unit.application.web.handlers

import com.kaiqkt.authentication.application.exceptions.InvalidRequestException
import com.kaiqkt.authentication.application.web.handlers.ErrorHandler
import com.kaiqkt.authentication.application.web.responses.ErrorV1
import com.kaiqkt.authentication.domain.exceptions.DomainException
import com.kaiqkt.authentication.domain.exceptions.ErrorType
import io.mockk.every
import io.mockk.mockk
import jakarta.validation.ConstraintViolation
import jakarta.validation.ConstraintViolationException
import org.hibernate.validator.internal.engine.path.PathImpl
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingRequestHeaderException
import org.springframework.web.context.request.WebRequest
import kotlin.test.assertEquals

class ErrorHandlerTest {
    private val webRequest: WebRequest = mockk()
    private val errorHandler = ErrorHandler()

    @Test
    fun `given an DomainException when is PERMISSION_NOT_FOUND should return the message based on the error type`() {
        val domainException = DomainException(ErrorType.PERMISSION_NOT_FOUND)

        val response = errorHandler.handleDomainException(domainException)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals(ErrorType.PERMISSION_NOT_FOUND.name, response.body?.type)
        assertEquals("Permission not found", response.body?.message)
    }

    @Test
    fun `given an DomainException when is ROLE_NOT_FOUND should return the message based on the error type`() {
        val domainException = DomainException(ErrorType.ROLE_NOT_FOUND)

        val response = errorHandler.handleDomainException(domainException)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals(ErrorType.ROLE_NOT_FOUND.name, response.body?.type)
        assertEquals("Role not found", response.body?.message)
    }

    @Test
    fun `given an DomainException when is ROLE_ALREADY_EXISTS should return the message based on the error type`() {
        val domainException = DomainException(ErrorType.ROLE_ALREADY_EXISTS)

        val response = errorHandler.handleDomainException(domainException)

        assertEquals(HttpStatus.CONFLICT, response.statusCode)
        assertEquals(ErrorType.ROLE_ALREADY_EXISTS.name, response.body?.type)
        assertEquals("Role already exists with the given name", response.body?.message)
    }

    @Test
    fun `given an DomainException when is PERMISSION_ALREADY_EXISTS should return the message based on the error type`() {
        val domainException = DomainException(ErrorType.PERMISSION_ALREADY_EXISTS)

        val response = errorHandler.handleDomainException(domainException)

        assertEquals(HttpStatus.CONFLICT, response.statusCode)
        assertEquals(ErrorType.PERMISSION_ALREADY_EXISTS.name, response.body?.type)
        assertEquals("Permission already exists", response.body?.message)
    }

    @Test
    fun `given an DomainException when is INVALID_SORT_FIELD should return the message based on the error type`() {
        val domainException = DomainException(ErrorType.INVALID_FIELD)

        val response = errorHandler.handleDomainException(domainException)

        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertEquals(ErrorType.INVALID_FIELD.name, response.body?.type)
        assertEquals("Field are not valid or does not exist", response.body?.message)
    }

    @Test
    fun `given an DomainException when is EXPIRED_TOKEN should return the message based on the error type`() {
        val domainException = DomainException(ErrorType.EXPIRED_TOKEN)

        val response = errorHandler.handleDomainException(domainException)

        assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
        assertEquals(ErrorType.EXPIRED_TOKEN.name, response.body?.type)
        assertEquals("Expired token", response.body?.message)
    }

    @Test
    fun `given an DomainException when is INVALID_TOKEN should return the message based on the error type`() {
        val domainException = DomainException(ErrorType.INVALID_TOKEN)

        val response = errorHandler.handleDomainException(domainException)

        assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
        assertEquals(ErrorType.INVALID_TOKEN.name, response.body?.type)
        assertEquals("Token sign or parsed are invalid", response.body?.message)
    }

    @Test
    fun `given an DomainException when is EMAIL_ALREADY_IN_USE should return the message based on the error type`() {
        val domainException = DomainException(ErrorType.EMAIL_ALREADY_IN_USE)

        val response = errorHandler.handleDomainException(domainException)

        assertEquals(HttpStatus.CONFLICT, response.statusCode)
        assertEquals(ErrorType.EMAIL_ALREADY_IN_USE.name, response.body?.type)
        assertEquals("Email already in use", response.body?.message)
    }

    @Test
    fun `given an DomainException when is USER_NOT_FOUND should return the message based on the error type`() {
        val domainException = DomainException(ErrorType.USER_NOT_FOUND)

        val response = errorHandler.handleDomainException(domainException)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals(ErrorType.USER_NOT_FOUND.name, response.body?.type)
        assertEquals("User not found", response.body?.message)
    }

    @Test
    fun `given an DomainException when is INVALID_PASSWORD should return the message based on the error type`() {
        val domainException = DomainException(ErrorType.INVALID_CREDENTIALS)

        val response = errorHandler.handleDomainException(domainException)

        assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
        assertEquals(ErrorType.INVALID_CREDENTIALS.name, response.body?.type)
        assertEquals("Invalid credentials", response.body?.message)
    }

    @Test
    fun `given an DomainException when is SESSION_NOT_FOUND should return the message based on the error type`() {
        val domainException = DomainException(ErrorType.SESSION_NOT_FOUND)

        val response = errorHandler.handleDomainException(domainException)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals(ErrorType.SESSION_NOT_FOUND.name, response.body?.type)
        assertEquals("Session not found", response.body?.message)
    }

    @Test
    fun `given an InvalidRequestException when handling should return all fields errors with his associated message`() {
        val invalidRequestException = InvalidRequestException(mapOf("field" to "invalid"))

        val response = errorHandler.handleInvalidRequestException(invalidRequestException)

        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertEquals("invalid", response.body?.details?.get("field"))
        assertEquals("Invalid request", response.body?.message)
        assertEquals("INVALID_REQUEST", response.body?.type)
    }

    @Suppress("UNCHECKED_CAST")
    @Test
    fun `given an MethodArgumentNotValid when handling should return all fields errors with his associated message`() {
        val methodArgumentNotValidException = mockk<MethodArgumentNotValidException>()
        val fieldError = mockk<FieldError>()

        every { fieldError.field } returns "field"
        every { fieldError.defaultMessage } returns "defaultMessage"
        every { methodArgumentNotValidException.bindingResult.fieldErrors } returns listOf(fieldError)

        val response =
            errorHandler.handleMethodArgumentNotValid(
                methodArgumentNotValidException,
                HttpHeaders(),
                HttpStatus.BAD_REQUEST,
                webRequest,
            ) as ResponseEntity<ErrorV1>

        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertEquals("defaultMessage", response.body?.details?.get("field"))
        assertEquals("Invalid request", response.body?.message)
        assertEquals("INVALID_REQUEST", response.body?.type)
    }

    @Suppress("UNCHECKED_CAST")
    @Test
    fun `given an MethodArgumentNotValid when field error message is null should return all fields errors with invalid message`() {
        val methodArgumentNotValidException = mockk<MethodArgumentNotValidException>()
        val fieldError = mockk<FieldError>()

        every { fieldError.field } returns "field"
        every { fieldError.defaultMessage } returns null
        every { methodArgumentNotValidException.bindingResult.fieldErrors } returns listOf(fieldError)

        val response =
            errorHandler.handleMethodArgumentNotValid(
                methodArgumentNotValidException,
                HttpHeaders(),
                HttpStatus.BAD_REQUEST,
                webRequest,
            ) as ResponseEntity<ErrorV1>

        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertEquals("invalid", response.body?.details?.get("field"))
        assertEquals("Invalid request", response.body?.message)
        assertEquals("INVALID_REQUEST", response.body?.type)
    }

    @Test
    fun `given an ConstraintViolationException should return the constraint violations`() {
        val path = PathImpl.createPathFromString("object.field")

        val violation = mockk<ConstraintViolation<Any>>()
        every { violation.propertyPath } returns path
        every { violation.message } returns "message"

        val ex = mockk<ConstraintViolationException>()
        every { ex.constraintViolations } returns setOf(violation)

        val response = errorHandler.handleConstraintViolationException(ex)

        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertEquals("message", response.body?.details?.get("object.field"))
        assertEquals("Invalid request", response.body?.message)
        assertEquals("INVALID_REQUEST", response.body?.type)
    }

    @Test
    fun `given an MissingRequestHeaderException should return the missing headers`() {
        val ex = mockk<MissingRequestHeaderException>()

        every { ex.headerName } returns "header_name"

        val response = errorHandler.handleMissingRequestHeaderException(ex)

        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertEquals("required header", response.body?.details?.get("header_name"))
        assertEquals("Invalid request", response.body?.message)
        assertEquals("INVALID_REQUEST", response.body?.type)
    }
}
