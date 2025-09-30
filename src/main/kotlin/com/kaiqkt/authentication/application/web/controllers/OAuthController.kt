package com.kaiqkt.authentication.application.web.controllers

import com.kaiqkt.authentication.application.web.requests.AuthorizationCodeRequestV1
import com.kaiqkt.authentication.application.web.requests.AuthorizationTokenRequestV1
import com.kaiqkt.authentication.application.web.requests.toDto
import com.kaiqkt.authentication.application.web.responses.AuthorizationCodeResponseV1
import com.kaiqkt.authentication.application.web.responses.AuthorizationTokenResponse
import com.kaiqkt.authentication.application.web.responses.IntrospectResponseV1
import com.kaiqkt.authentication.application.web.responses.toResponseV1
import com.kaiqkt.authentication.domain.dtos.AuthenticationDto
import com.kaiqkt.authentication.domain.services.AuthenticationService
import com.kaiqkt.authentication.domain.services.AuthorizationService
import com.kaiqkt.authentication.domain.utils.Constants
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/oauth")
@Validated
class OAuthController(
    private val authorizationService: AuthorizationService,
    private val authenticationService: AuthenticationService
) {

    @PostMapping("/authorize")
    fun authorize(@Valid @RequestBody requestV1: AuthorizationCodeRequestV1): ResponseEntity<AuthorizationCodeResponseV1> {
        val code = authorizationService.create(requestV1.toDto())

        return ResponseEntity.ok(code.toResponseV1())
    }

    @PostMapping("/token")
    fun token(@Valid @RequestBody requestV1: AuthorizationTokenRequestV1): ResponseEntity<AuthorizationTokenResponse?> {
        val tokens = authenticationService.getTokens(requestV1.toDto())

        return ResponseEntity.ok(tokens.toResponseV1())
    }

    @GetMapping("/introspect")
    fun introspect(@RequestHeader(value = "Authorization") accessToken: String): ResponseEntity<IntrospectResponseV1> {
        val introspection = authenticationService.introspect(accessToken.substringAfter(Constants.BEARER_PREFIX))

        return ResponseEntity.ok(introspection.toResponseV1())
    }
}