package com.kaiqkt.authentication.application.web.controllers

import com.kaiqkt.authentication.application.web.requests.AuthorizationRequestV1
import com.kaiqkt.authentication.application.web.requests.toDto
import com.kaiqkt.authentication.application.web.responses.AuthenticationTokenResponseV1
import com.kaiqkt.authentication.application.web.responses.IntrospectResponseV1
import com.kaiqkt.authentication.application.web.responses.toResponseV1
import com.kaiqkt.authentication.domain.services.AuthenticationService
import com.kaiqkt.authentication.domain.services.AuthorizationService
import com.kaiqkt.authentication.domain.utils.Constants
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController

@RestController
class AuthController(
    private val authorizationService: AuthorizationService,
    private val authenticationService: AuthenticationService,
) {
    @PostMapping("/v1/oauth/token")
    fun token(
        @Valid @RequestBody requestV1: AuthorizationRequestV1,
    ): ResponseEntity<AuthenticationTokenResponseV1?> {
        val tokens = authorizationService.getTokens(requestV1.toDto())

        return ResponseEntity.ok(tokens.toResponseV1())
    }

    @GetMapping("/v1/oauth/introspect")
    fun introspect(
        @RequestHeader(value = "Authorization") accessToken: String,
    ): ResponseEntity<IntrospectResponseV1> {
        val introspection = authenticationService.introspect(accessToken.substringAfter(Constants.Headers.BEARER))

        return ResponseEntity.ok(introspection.toResponseV1())
    }
}
