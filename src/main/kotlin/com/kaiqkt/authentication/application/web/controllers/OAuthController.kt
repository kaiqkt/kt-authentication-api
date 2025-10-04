package com.kaiqkt.authentication.application.web.controllers

import com.kaiqkt.authentication.application.web.requests.AuthenticationTokenRequestV1
import com.kaiqkt.authentication.application.web.requests.LoginRequestV1
import com.kaiqkt.authentication.application.web.requests.toDto
import com.kaiqkt.authentication.application.web.responses.AuthenticationTokenResponseV1
import com.kaiqkt.authentication.application.web.responses.IntrospectResponseV1
import com.kaiqkt.authentication.application.web.responses.toResponseV1
import com.kaiqkt.authentication.domain.services.AuthenticationService
import com.kaiqkt.authentication.domain.utils.Constants
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/v1/oauth")
@Validated
class OAuthController(
    private val authenticationService: AuthenticationService
) {

    @PostMapping("/login")
    fun login(@Valid @RequestBody requestV1: LoginRequestV1): ResponseEntity<AuthenticationTokenResponseV1> {
        val code = authenticationService.login(requestV1.email, requestV1.password)

        return ResponseEntity.ok(code.toResponseV1())
    }

    @PostMapping("/token")
    fun token(@Valid @RequestBody requestV1: AuthenticationTokenRequestV1): ResponseEntity<AuthenticationTokenResponseV1?> {
        val tokens = authenticationService.getTokens(requestV1.toDto())

        return ResponseEntity.ok(tokens.toResponseV1())
    }

    @GetMapping("/introspect")
    fun introspect(@RequestHeader(value = "Authorization") accessToken: String): ResponseEntity<IntrospectResponseV1> {
        val introspection = authenticationService.introspect(accessToken.substringAfter(Constants.BEARER_PREFIX))

        return ResponseEntity.ok(introspection.toResponseV1())
    }
}